package com.diasjoao.bolanatv;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.diasjoao.bolanatv.activities.LoadingActivity;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class MainActivityOld extends AppCompatActivity {
    private DrawerLayout drawer;
    private FrameLayout frame;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private AdView mAdView;

    private static Map<Date, List<Game>> gamesPerDay = new TreeMap();
    private static Map<String, List<Game>> gamesPerCompetition = new TreeMap();
    private static Map<String, List<Game>> gamesPerChannel = new TreeMap();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainold);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        frame = (FrameLayout)findViewById(R.id.fragment_container);
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        Intent intent = getIntent();

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Intent intent;
                switch (menuItem.getItemId()) {
                    /*case R.id.nav_settings:
                        intent = new Intent(MainActivityOld.this, SettingsActivity.class);
                        if (getIntent().getSerializableExtra("games") != null) {
                            intent.putExtra("games", getIntent().getSerializableExtra("games"));
                            intent.putExtra("hasNetwork", getIntent().getBooleanExtra("hasNetwork", true));
                            dumpOldVars();
                        }
                        startActivity(intent);
                        break;*/
                    case R.id.nav_rate:
                        launchMarket();
                        break;
                    case R.id.nav_comment:
                        Intent emailIntent = new Intent(Intent.ACTION_SEND);
                        emailIntent.putExtra(Intent.EXTRA_EMAIL, getResources().getStringArray(R.array.developer_emails));
                        emailIntent.setType("message/rfc822");
                        startActivity(Intent.createChooser(emailIntent, "Selecione um provedor de email"));
                        break;
                    case R.id.nav_about:
                        intent = new Intent(MainActivityOld.this, DetailsActivity.class);
                        if (getIntent().getSerializableExtra("games") != null) {
                            intent.putExtra("games", getIntent().getSerializableExtra("games"));
                            intent.putExtra("hasNetwork", getIntent().getBooleanExtra("hasNetwork", true));
                            dumpOldVars();
                        }
                        startActivity(intent);
                        break;
                    case R.id.nav_privacy:
                        intent = new Intent(MainActivityOld.this, PrivacyActivity.class);
                        if (getIntent().getSerializableExtra("games") != null) {
                            intent.putExtra("games", getIntent().getSerializableExtra("games"));
                            intent.putExtra("hasNetwork", getIntent().getBooleanExtra("hasNetwork", true));
                            dumpOldVars();
                        }
                        startActivity(intent);
                        break;
                }

                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        if (intent.getBooleanExtra("hasNetwork", false)) {
            if (buildGamesperDay((ArrayList<Game>) intent.getSerializableExtra("games"))) {
                final BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MainActivityOld.this);
                String type = prefs.getString("DefaultFilter", "Data");

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
        } else {
            new AlertDialog.Builder(MainActivityOld.this)
                    .setTitle("Sem conexão à Internet")
                    .setMessage("Certifique-se de que está conectado à internet e tente novamente")
                    .setPositiveButton("Tentar Novamente", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dumpOldVars();
                            finish();
                            startActivity(new Intent(MainActivityOld.this, SplashActivity.class));
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setCancelable(false)
                    .show();
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
                dumpOldVars();
                finish();
                startActivity(new Intent(MainActivityOld.this, LoadingActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public static void dumpOldVars() {
        if (!gamesPerDay.isEmpty()) {
            gamesPerDay.clear();
        }
        if (!gamesPerCompetition.isEmpty()) {
            gamesPerCompetition.clear();
        }
        if (!gamesPerChannel.isEmpty()) {
            gamesPerChannel.clear();
        }
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
