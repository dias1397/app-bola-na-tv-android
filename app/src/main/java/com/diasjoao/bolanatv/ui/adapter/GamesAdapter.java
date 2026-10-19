package com.diasjoao.bolanatv.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.diasjoao.bolanatv.R;
import com.diasjoao.bolanatv.model.Game;

import java.util.ArrayList;
import java.util.List;

public class GamesAdapter extends RecyclerView.Adapter<GamesAdapter.VH> {

    private final List<Game> items = new ArrayList<>();

    public void submit(List<Game> newItems) {
        items.clear();
        items.addAll(newItems);
        notifyDataSetChanged(); // temporary; we'll switch to DiffUtil later
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_game_simple, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        Game g = items.get(position);
        h.time.setText(g.getTime());
        h.competition.setText(g.getCompetition() == null ? "" : g.getCompetition().trim());
        h.teams.setText(g.getHome() + " – " + g.getAway());
        h.channel.setText(g.getChannel());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        final TextView time, competition, teams, channel;

        VH(@NonNull View v) {
            super(v);
            time = v.findViewById(R.id.time);
            competition = v.findViewById(R.id.competition);
            teams = v.findViewById(R.id.teams);
            channel = v.findViewById(R.id.channel);
        }
    }
}
