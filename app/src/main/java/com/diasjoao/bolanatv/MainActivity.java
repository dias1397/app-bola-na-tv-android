package com.diasjoao.bolanatv;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    HashMap<String, List<Game>> unorderGamesPerDay = null;
    ArrayList<String> orderPerDay = new ArrayList<>();
    LinkedHashMap<String, List<Game>> gamesPerDay = null;


    HashMap<String, List<Game>> unorderGamesPerChannel = null;
    ArrayList<String> orderPerChannel = new ArrayList<>();
    LinkedHashMap<String, List<Game>> gamesPerChannel = null;


    HashMap<String, List<Game>> unorderGamesPerCompetition = null;
    ArrayList<String> orderPerCompetition = new ArrayList<>();
    LinkedHashMap<String, List<Game>> gamesPerCompetition = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();

        unorderGamesPerDay = (HashMap<String, List<Game>>)intent.getSerializableExtra("gamesPerDay");
        orderPerDay = (ArrayList<String>) intent.getSerializableExtra("orderPerDay");
        gamesPerDay = buildLinkedMap(orderPerDay, unorderGamesPerDay);

        unorderGamesPerChannel = (HashMap<String, List<Game>>)intent.getSerializableExtra("gamesPerChannel");
        orderPerChannel = (ArrayList<String>) intent.getSerializableExtra("orderPerChannel");
        gamesPerChannel = buildLinkedMap(orderPerChannel, unorderGamesPerChannel);

        unorderGamesPerCompetition = (HashMap<String, List<Game>>)intent.getSerializableExtra("gamesPerCompetition");
        orderPerCompetition = (ArrayList<String>) intent.getSerializableExtra("orderPerCompetition");
        gamesPerCompetition = buildLinkedMap(orderPerCompetition, unorderGamesPerCompetition);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Fragment selectedFragment = null;

                switch (menuItem.getItemId()) {
                    case R.id.nav_lines:
                        selectedFragment = new DateFragment(gamesPerDay);
                        break;
                    case R.id.nav_map:
                        selectedFragment = new CompetitionFragment(gamesPerCompetition);
                        break;
                    case R.id.nav_info:
                        selectedFragment = new ChannelFragment(gamesPerChannel);
                        break;
                }

                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();

                return true;
            }
        });

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new DateFragment(gamesPerDay)).commit();
    }

    private LinkedHashMap<String, List<Game>> buildLinkedMap (ArrayList<String> order, HashMap<String, List<Game>> unorderMap) {
        LinkedHashMap<String, List<Game>> result = new LinkedHashMap<String, List<Game>>();

        for (String s :order) {
            result.put(s, unorderMap.get(s));
        }

        return result;
    }
}
