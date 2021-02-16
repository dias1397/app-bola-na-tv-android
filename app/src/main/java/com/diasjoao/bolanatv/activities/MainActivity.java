package com.diasjoao.bolanatv.activities;

import com.diasjoao.bolanatv.utils.DateUtils;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;

import com.diasjoao.bolanatv.R;
import com.diasjoao.bolanatv.adapters.TabAdapter;
import com.diasjoao.bolanatv.models.Game;
import com.diasjoao.bolanatv.utils.GoogleUtils;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;

    private TabAdapter tabAdapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);

        Map<Date, Map<String, List<Game>>> games = new TreeMap<>((HashMap<Date, HashMap<String, List<Game>>>) getIntent().getSerializableExtra("param1"));

        for (Date key : games.keySet()) {
            tabLayout.addTab(tabLayout.newTab().setText(DateUtils.simpleDateFormat2.format(key)));
        }
        tabAdapter = new TabAdapter(getSupportFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT, games);

        viewPager.setAdapter(tabAdapter);
        viewPager.setOffscreenPageLimit(1);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));

        navigationView.setNavigationItemSelectedListener(menuItem -> {
            Intent intent;
            switch (menuItem.getItemId()) {
                /*case R.id.nav_settings:
                    intent = new Intent(MainActivity.this, SettingsActivity.class);
                    if (getIntent().getSerializableExtra("games") != null) {
                        intent.putExtra("games", getIntent().getSerializableExtra("games"));
                        intent.putExtra("hasNetwork", getIntent().getBooleanExtra("hasNetwork", true));
                    }
                    startActivity(intent);
                    break;*/
                case R.id.nav_rate:
                    GoogleUtils.launchMarket(MainActivity.this);
                    break;
                case R.id.nav_comment:
                    Intent emailIntent = new Intent(Intent.ACTION_SEND);
                    emailIntent.putExtra(Intent.EXTRA_EMAIL, getResources().getStringArray(R.array.developer_emails));
                    emailIntent.setType("message/rfc822");
                    startActivity(Intent.createChooser(emailIntent, "Selecione um provedor de email"));
                    break;
                case R.id.nav_about:
                    intent = new Intent(MainActivity.this, DetailsActivity.class);
                    /*if (getIntent().getSerializableExtra("games") != null) {
                        intent.putExtra("games", getIntent().getSerializableExtra("games"));
                        intent.putExtra("hasNetwork", getIntent().getBooleanExtra("hasNetwork", true));
                    }*/
                    startActivity(intent);
                    break;
                case R.id.nav_privacy:
                    intent = new Intent(MainActivity.this, PrivacyActivity.class);
                    /*if (getIntent().getSerializableExtra("games") != null) {
                        intent.putExtra("games", getIntent().getSerializableExtra("games"));
                        intent.putExtra("hasNetwork", getIntent().getBooleanExtra("hasNetwork", true));
                    }*/
                    startActivity(intent);
                    break;
            }

            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
    }
}