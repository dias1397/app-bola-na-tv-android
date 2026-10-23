package com.diasjoao.bolanatv.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.diasjoao.bolanatv.R;
import com.diasjoao.bolanatv.model.Game;

import java.util.ArrayList;
import java.util.List;

public class DayPagerAdapter extends RecyclerView.Adapter<DayPagerAdapter.PageVH> {

    private final List<List<Game>> days = new ArrayList<>();
    private final GamesAdapter.OnGameClick onGameClick;
    private final Runnable onRefresh;

    public DayPagerAdapter(GamesAdapter.OnGameClick onGameClick, Runnable onRefresh) {
        this.onGameClick = onGameClick;
        this.onRefresh = onRefresh;
    }

    public void submit(List<List<Game>> newDays) {
        days.clear();
        days.addAll(newDays);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PageVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.page_day, parent, false);
        return new PageVH(v, onGameClick, onRefresh);
    }

    @Override
    public void onBindViewHolder(@NonNull PageVH h, int position) {
        h.adapter.submit(days.get(position));
    }

    @Override
    public int getItemCount() {
        return days.size();
    }

    static class PageVH extends RecyclerView.ViewHolder {
        final GamesAdapter adapter = new GamesAdapter();

        PageVH(@NonNull View v, GamesAdapter.OnGameClick click, Runnable refresh) {
            super(v);
            RecyclerView list = v.findViewById(R.id.list);
            list.setLayoutManager(new LinearLayoutManager(v.getContext()));
            list.setAdapter(adapter);
            adapter.setOnGameClick(click);

            SwipeRefreshLayout swipe = v.findViewById(R.id.swipe);
            swipe.setColorSchemeResources(R.color.bp_accent);
            swipe.setProgressBackgroundColorSchemeResource(R.color.bp_surface);
            swipe.setOnRefreshListener(refresh::run);
        }
    }
}
