package com.diasjoao.bolanatv.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.diasjoao.bolanatv.R;
import com.diasjoao.bolanatv.models.Game;

import java.util.List;

public class ChildRecyclerAdapter extends RecyclerView.Adapter<ChildRecyclerAdapter.ViewHolder> {

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView homeTeam;
        private TextView awayTeam;
        private TextView competition;
        private TextView channel;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            homeTeam = (TextView) itemView.findViewById(R.id.homeTeam);
            awayTeam = (TextView) itemView.findViewById(R.id.awayTeam);
            competition = (TextView) itemView.findViewById(R.id.competition);
            channel = (TextView) itemView.findViewById(R.id.channel);
        }
    }

    private Context context;
    private List<Game> games;

    public ChildRecyclerAdapter(Context context, List<Game> games) {
        this.context = context;
        this.games = games;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Game game = games.get(position);

        holder.homeTeam.setText(game.getHome());
        holder.awayTeam.setText(game.getAway());
        holder.competition.setText(game.getCompetition());
        holder.channel.setText(game.getChannel());
    }

    @Override
    public int getItemCount() {
        return games.size();
    }

}
