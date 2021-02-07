package com.diasjoao.bolanatv.activities;

import com.diasjoao.bolanatv.utils.DateUtils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.diasjoao.bolanatv.R;
import com.diasjoao.bolanatv.adapters.TabAdapter;
import com.diasjoao.bolanatv.models.Game;
import com.google.android.material.tabs.TabLayout;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class MainActivity extends AppCompatActivity {
    private TabAdapter tabAdapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);

        Map<Date, Map<String, List<Game>>> games = new TreeMap<>((HashMap<Date, HashMap<String, List<Game>>>) getIntent().getSerializableExtra("param1"));
        //Map<Date, List<Game>> games = new TreeMap<>((HashMap<Date, List<Game>>) getIntent().getSerializableExtra("param1"));

        for (Date key : games.keySet()) {
            tabLayout.addTab(tabLayout.newTab().setText(DateUtils.simpleDateFormat2.format(key)));
        }
        tabAdapter = new TabAdapter(getSupportFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT, games);

        viewPager.setAdapter(tabAdapter);
        viewPager.setOffscreenPageLimit(1);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));
    }
}