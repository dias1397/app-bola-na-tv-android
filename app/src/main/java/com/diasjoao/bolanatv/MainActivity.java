package com.diasjoao.bolanatv;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static HashMap<String, List<Game>> unorderGamesPerDay = new HashMap<>();
    private static ArrayList<String> orderPerDay = new ArrayList<>();
    private static LinkedHashMap<String, List<Game>> gamesPerDay = null;

    private static HashMap<String, List<Game>> unorderGamesPerChannel = new HashMap<>();
    private static ArrayList<String> orderPerChannel = new ArrayList<>();
    private static LinkedHashMap<String, List<Game>> gamesPerChannel = null;

    private static HashMap<String, List<Game>> unorderGamesPerCompetition = new HashMap<>();
    private static ArrayList<String> orderPerCompetition = new ArrayList<>();
    private static LinkedHashMap<String, List<Game>> gamesPerCompetition = null;

    ProgressBar loading;
    SwipeRefreshLayout refreshLayout;
    FrameLayout frame;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        loading = (ProgressBar)findViewById(R.id.loading);
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);
        frame = (FrameLayout)findViewById(R.id.fragment_container);

        Intent intent = getIntent();

        unorderGamesPerDay = (HashMap<String, List<Game>>)intent.getSerializableExtra("gamesPerDay");
        orderPerDay = (ArrayList<String>) intent.getSerializableExtra("orderPerDay");
        unorderGamesPerChannel = (HashMap<String, List<Game>>)intent.getSerializableExtra("gamesPerChannel");
        orderPerChannel = (ArrayList<String>) intent.getSerializableExtra("orderPerChannel");
        unorderGamesPerCompetition = (HashMap<String, List<Game>>)intent.getSerializableExtra("gamesPerCompetition");
        orderPerCompetition = (ArrayList<String>) intent.getSerializableExtra("orderPerCompetition");

        if (gamesPerDay == null && unorderGamesPerDay != null && orderPerDay != null) {
            gamesPerDay = buildLinkedMap(orderPerDay, unorderGamesPerDay);
        }
        if (gamesPerChannel == null && unorderGamesPerChannel != null && orderPerChannel != null) {
            gamesPerChannel = buildLinkedMap(orderPerChannel, unorderGamesPerChannel);
        }
        if (gamesPerCompetition == null && unorderGamesPerCompetition != null && orderPerCompetition != null) {
            gamesPerCompetition = buildLinkedMap(orderPerCompetition, unorderGamesPerCompetition);
        }

        final BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        String type = prefs.getString("DefaultType", "Data");
        if (type.equals("Data")) {
            bottomNav.setSelectedItemId(R.id.nav_date);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new DateFragment(gamesPerDay)).commit();
        } else if (type.equals("Competição")) {
            bottomNav.setSelectedItemId(R.id.nav_competition);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new DateFragment(gamesPerCompetition)).commit();
        } else if (type.equals("Canal")) {
            bottomNav.setSelectedItemId(R.id.nav_channel);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new DateFragment(gamesPerChannel)).commit();
        }
        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.nav_date:
                        if (gamesPerDay == null) {
                            try {
                                new RetrieveGames().execute("Date");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new DateFragment(gamesPerDay)).commit();
                        }
                        break;
                    case R.id.nav_competition:
                        if (gamesPerCompetition == null) {
                            try {
                                new RetrieveGames().execute("Competition");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new CompetitionFragment(gamesPerCompetition)).commit();
                        }
                        break;
                    case R.id.nav_channel:
                        if (gamesPerChannel == null) {
                            try {
                                new RetrieveGames().execute("Channel");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ChannelFragment(gamesPerChannel)).commit();
                        }
                        break;
                }

                return true;
            }
        });

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                String type = null;
                switch (bottomNav.getSelectedItemId()) {
                    case R.id.nav_date:
                        type = "Date";
                        break;
                    case R.id.nav_competition:
                        type = "Competition";
                        break;
                    case R.id.nav_channel:
                        type = "Channel";
                        break;
                }

                try {
                    new RetrieveGames().execute(type);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                refreshLayout.setRefreshing(false);
            }
        });
    }

    private LinkedHashMap<String, List<Game>> buildLinkedMap (ArrayList<String> order, HashMap<String, List<Game>> unorderMap) {
        if (order == null || unorderMap == null) {
            return null;
        }

        LinkedHashMap<String, List<Game>> result = new LinkedHashMap<String, List<Game>>();

        for (String s :order) {
            result.put(s, unorderMap.get(s));
        }

        return result;
    }

    class RetrieveGames extends AsyncTask<String, Integer, Boolean> {

        LinkedHashMap<String, List<Game>> resultByDate = new LinkedHashMap<>();
        LinkedHashMap<String, List<Game>> resultByCompetition = new LinkedHashMap<>();
        LinkedHashMap<String, List<Game>> resultByChannel = new LinkedHashMap<>();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            refreshLayout.setEnabled(false);
            frame.setVisibility(View.INVISIBLE);
            loading.setVisibility(View.VISIBLE);
        }

        protected Boolean doInBackground(String... urls) {
            try {
                Document doc = null;
                if (urls[0].equals("Date")) {
                    doc = Jsoup.connect("https://www.futebol365.pt/jogos-na-tv/?order=date").get();
                } else if (urls[0].equals("Competition")) {
                    doc = Jsoup.connect("https://www.futebol365.pt/jogos-na-tv/?order=competition").get();
                } else if (urls[0].equals("Channel")) {
                    doc = Jsoup.connect("https://www.futebol365.pt/jogos-na-tv/?order=channel").get();
                }

                Elements element = doc.getElementsByClass("left-column-group-wrapper").first().children();
                String title = null;

                for (Element child : element) {
                    if (child.tagName().equals("h3")) {
                        title = child.text();
                        continue;
                    }

                    if (urls[0].equals("Date")) {
                        if (child.tagName().equals("table")) {
                            ArrayList<Game> games = new ArrayList<>();
                            for (Element row : child.child(1).children()) {
                                games.add(new Game(null, row.child(0).text(), row.child(1).text(), row.child(3).text(), row.child(5).text()));
                            }
                            resultByDate.put(title, games);
                        }
                    } else if (urls[0].equals("Channel")) {
                        if (child.tagName().equals("table")) {
                            ArrayList<Game> games = new ArrayList<>();
                            for (Element row : child.child(1).children()) {
                                games.add(new Game(row.child(0).text(), row.child(1).text(), row.child(4).text(), row.child(6).text(), null));
                            }
                            resultByChannel.put(title, games);
                        }
                    } else if (urls[0].equals("Competition")) {
                        if (child.tagName().equals("table")) {
                            ArrayList<Game> games = new ArrayList<>();
                            for (Element row : child.child(1).children()) {
                                games.add(new Game(row.child(0).text(), row.child(1).text(), row.child(3).text(), row.child(5).text(), row.child(7).text()));
                            }
                            resultByCompetition.put(title, games);
                        }
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }

        protected void onPostExecute(Boolean result) {
            if (!resultByDate.isEmpty()) {
                gamesPerDay = resultByDate;
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new DateFragment(gamesPerDay)).commit();
            } else if (!resultByCompetition.isEmpty()) {
                gamesPerCompetition = resultByCompetition;
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new CompetitionFragment(gamesPerCompetition)).commit();
            } else if (!resultByChannel.isEmpty()) {
                gamesPerChannel = resultByChannel;
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ChannelFragment(gamesPerChannel)).commit();
            }

            loading.setVisibility(View.GONE);
            frame.setVisibility(View.VISIBLE);
            refreshLayout.setEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item1:
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
