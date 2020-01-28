package com.diasjoao.bolanatv;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class MainActivity extends AppCompatActivity {
    private static Map<Date, List<Game>> gamesPerDay = new TreeMap();
    private static Map<String, List<Game>> gamesPerCompetition = new TreeMap();
    private static Map<String, List<Game>> gamesPerChannel = new TreeMap();

    ProgressBar loading;
    FrameLayout frame;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        loading = (ProgressBar)findViewById(R.id.loading);
        frame = (FrameLayout)findViewById(R.id.fragment_container);

        Intent intent = getIntent();
        if (buildGamesperDay((ArrayList<Game>) intent.getSerializableExtra("games"))) {
            final BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
            String type = prefs.getString("DefaultType", "Data");

            //if (type.equals("Data")) {
                bottomNav.setSelectedItemId(R.id.nav_date);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new DateFragment(gamesPerDay)).commit();
            /*} else if (type.equals("Competição")) {
                bottomNav.setSelectedItemId(R.id.nav_competition);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new CompetitionFragment(gamesPerCompetition)).commit();
            } else if (type.equals("Canal")) {
                bottomNav.setSelectedItemId(R.id.nav_channel);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ChannelFragment(gamesPerChannel)).commit();
            }*/

            bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    switch (menuItem.getItemId()) {
                        case R.id.nav_date:
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new DateFragment(gamesPerDay)).commit();
                            break;
                        case R.id.nav_competition:
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new CompetitionFragment(gamesPerCompetition)).commit();
                            break;
                        case R.id.nav_channel:
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ChannelFragment(gamesPerChannel)).commit();
                            break;
                    }
                    return true;
                }
            });

        } else {
            System.out.println("ERROR");
        }
    }

    private static Boolean buildGamesperDay(ArrayList<Game> games) {
        try {
            for (Game g : games) {
                gamesPerDay.computeIfAbsent(g.getFullDate(), k -> new ArrayList<>()).add(g);
                gamesPerCompetition.computeIfAbsent(g.getCompetition(), k -> new ArrayList<>()).add(g);
                gamesPerChannel.computeIfAbsent(g.getChannel(), k -> new ArrayList<>()).add(g);
            }
            for (List value1 : gamesPerDay.values()) {
                Collections.sort(value1);
            }
            for (List value2 : gamesPerCompetition.values()) {
                Collections.sort(value2, new GameComparator());
            }
            for (List value3 : gamesPerChannel.values()) {
                Collections.sort(value3, new GameComparator());
            }
        } catch (Exception e) {
            e.getStackTrace();
            return false;
        }
        return true;
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
                startActivity(new Intent(MainActivity.this, SplashActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
