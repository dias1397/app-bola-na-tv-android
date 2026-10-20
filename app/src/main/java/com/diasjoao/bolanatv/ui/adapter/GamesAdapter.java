package com.diasjoao.bolanatv.ui.adapter;

import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.diasjoao.bolanatv.R;
import com.diasjoao.bolanatv.model.Game;
import com.diasjoao.bolanatv.ui.ChannelStyle;

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
                .inflate(R.layout.item_game, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        Game g = items.get(position);

        h.time.setText(g.getTime());
        h.competition.setText(g.getCompetition());
        h.home.setText(g.getHome());
        h.away.setText(g.getAway());
        h.channel.setText(g.getChannel());

        ChannelStyle style = ChannelStyle.of(h.itemView.getContext(), g.getChannel());
        ViewCompat.setBackgroundTintList(h.channel, ColorStateList.valueOf(style.background));
        h.channel.setTextColor(style.text);

        h.itemView.setContentDescription(g.getTime() + ". " + g.getHome() + " contra "
                + g.getAway() + ". " + g.getCompetition() + ". Canal " + g.getChannel() + ".");
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        final TextView time, competition, home, away, channel;

        VH(@NonNull View v) {
            super(v);
            time = v.findViewById(R.id.time);
            competition = v.findViewById(R.id.competition);
            home = v.findViewById(R.id.home);
            away = v.findViewById(R.id.away);
            channel = v.findViewById(R.id.channel);
        }
    }
}
