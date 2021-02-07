package com.diasjoao.bolanatv.adapters;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.diasjoao.bolanatv.fragments.GamesFragment;
import com.diasjoao.bolanatv.models.Game;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class TabAdapter extends FragmentStatePagerAdapter {

    Map<Date, Map<String, List<Game>>> games;

    public TabAdapter(@NonNull FragmentManager fm, int behavior, Map<Date, Map<String, List<Game>>> games) {
        super(fm, behavior);
        this.games = games;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Date key = (Date) games.keySet().toArray()[position];
        return GamesFragment.newInstance(games.get(key));
    }

    @Override
    public int getCount() {
        return games.keySet().size();
    }
}
