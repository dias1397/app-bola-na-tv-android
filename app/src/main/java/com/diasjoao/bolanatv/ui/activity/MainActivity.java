package com.diasjoao.bolanatv.ui.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.diasjoao.bolanatv.R;
import com.diasjoao.bolanatv.model.Game;
import com.diasjoao.bolanatv.ui.adapter.TabAdapter;
import com.diasjoao.bolanatv.util.DateUtils;
import com.diasjoao.bolanatv.util.GoogleUtils;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.appbar.MaterialToolbar;
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
    private MaterialToolbar materialToolbar;

    private AdView adBannerView;
    private ViewPager viewPager;
    private TabLayout tabLayout;

    private TabAdapter tabAdapter;
    private Map<Date, Map<String, List<Game>>> gamesPerDay = new TreeMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();

        setupUI();
        setupAds();
        setupListeners();

        loadGamesContent();
    }

    private void initializeViews() {
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        materialToolbar = findViewById(R.id.toolbar);

        adBannerView = findViewById(R.id.adView);
        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);
    }

    private void setupUI() {
        EdgeToEdge.enable(this);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryVariant, null));
        setSupportActionBar(materialToolbar);

        materialToolbar.setNavigationIcon(R.drawable.ic_menu);
        materialToolbar.setNavigationOnClickListener(view -> drawerLayout.openDrawer(GravityCompat.START));
    }

    private void setupAds() {
        MobileAds.initialize(this, initializationStatus -> {
        });

        AdRequest adRequest = new AdRequest.Builder().build();
        adBannerView.loadAd(adRequest);
    }

    private void setupListeners() {
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_rate) {
                GoogleUtils.launchMarket(MainActivity.this);
            } else if (id == R.id.nav_comment) {
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.putExtra(Intent.EXTRA_EMAIL, getResources().getStringArray(R.array.developer_emails));
                emailIntent.setType("message/rfc822");
                startActivity(Intent.createChooser(emailIntent, "Selecione um provedor de email"));
            } else if (id == R.id.nav_about) {
                startActivity(new Intent(MainActivity.this, DetailsActivity.class));
            } else if (id == R.id.nav_privacy) {
                startActivity(new Intent(MainActivity.this, PrivacyActivity.class));
            }

            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));
    }

    private void loadGamesContent() {
        if (getIntent().getSerializableExtra("param1") != null) {
            gamesPerDay = new TreeMap<>((HashMap<Date, HashMap<String, List<Game>>>) getIntent().getSerializableExtra("param1"));

            for (Date key : gamesPerDay.keySet()) {
                tabLayout.addTab(tabLayout.newTab().setText(DateUtils.simpleDateFormat2.format(key)));
            }
            tabAdapter = new TabAdapter(getSupportFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT, gamesPerDay);

            viewPager.setAdapter(tabAdapter);
            viewPager.setOffscreenPageLimit(1);
        }
    }
}