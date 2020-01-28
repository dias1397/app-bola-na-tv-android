package com.diasjoao.bolanatv;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout drawer;
    private ProgressBar loading;
    private FrameLayout frame;
    private Toolbar toolbar;

    private static Map<Date, List<Game>> gamesPerDay = new TreeMap();
    private static Map<String, List<Game>> gamesPerCompetition = new TreeMap();
    private static Map<String, List<Game>> gamesPerChannel = new TreeMap();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loading = (ProgressBar)findViewById(R.id.loading);
        frame = (FrameLayout)findViewById(R.id.fragment_container);

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.nav_settings:
                        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                        intent.putExtra("games", getIntent().getSerializableExtra("games"));
                        finish();
                        startActivity(intent);
                        break;
                    case R.id.nav_rate:
                        launchMarket();
                        break;
                    case R.id.nav_comment:
                        Toast.makeText(getApplicationContext(), "Send Email", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.nav_details:
                        Toast.makeText(getApplicationContext(), "Details", Toast.LENGTH_SHORT).show();
                        break;
                }

                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        Intent intent = getIntent();
        if (buildGamesperDay((ArrayList<Game>) intent.getSerializableExtra("games"))) {
            final BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
            String type = prefs.getString("DefaultType", "Data");

            if (type.equals("Data")) {
                bottomNav.setSelectedItemId(R.id.nav_date);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new DateFragment(gamesPerDay)).commit();
            } else if (type.equals("Competição")) {
                bottomNav.setSelectedItemId(R.id.nav_competition);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new CompetitionFragment(gamesPerCompetition)).commit();
            } else if (type.equals("Canal")) {
                bottomNav.setSelectedItemId(R.id.nav_channel);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ChannelFragment(gamesPerChannel)).commit();
            }

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

    private void launchMarket() {
        Uri uri = Uri.parse("market://details?id=" + getPackageName());
        Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try {
            startActivity(myAppLinkToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName())));
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
                finish();
                startActivity(new Intent(MainActivity.this, SplashActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
