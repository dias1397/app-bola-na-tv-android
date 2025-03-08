package com.diasjoao.bolanatv.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.provider.CalendarContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.diasjoao.bolanatv.R;
import com.diasjoao.bolanatv.model.Game;

import java.util.Calendar;
import java.util.List;

public class ChildRecyclerAdapter extends RecyclerView.Adapter<ChildRecyclerAdapter.ViewHolder> {

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView homeTeam;
        private TextView awayTeam;
        private TextView competition;
        private TextView channel;
        private ImageView bell;
        private TextView live;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            homeTeam = (TextView) itemView.findViewById(R.id.homeTeamTextView);
            awayTeam = (TextView) itemView.findViewById(R.id.awayTeamTextView);
            competition = (TextView) itemView.findViewById(R.id.competitionTextView);
            channel = (TextView) itemView.findViewById(R.id.channelTextView);
            bell = (ImageView) itemView.findViewById(R.id.notification);
            live = (TextView) itemView.findViewById(R.id.liveTextView);

            bell.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Game game = games.get(getAdapterPosition());
            String[] date = game.getDate().split("-");
            String[] time = game.getTime().split(":");

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, Integer.parseInt("20" + date[2]));
            calendar.set(Calendar.MONTH, Integer.parseInt(date[1]) - 1);
            calendar.set(Calendar.DATE, Integer.parseInt(date[0]));
            calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time[0]));
            calendar.set(Calendar.MINUTE, Integer.parseInt(time[1]));

            Long startTime = calendar.getTimeInMillis();

            Intent intent = new Intent(Intent.ACTION_INSERT);
            intent.setType("vnd.android.cursor.item/event");
            intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startTime);
            intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, startTime + (105*60*1000));
            intent.putExtra(CalendarContract.Events.ALL_DAY, false);
            intent.putExtra(CalendarContract.Events.TITLE, game.getHome() + " vs " + game.getAway() + " (" + game.getChannel() + ")");
            intent.putExtra(CalendarContract.Events.DESCRIPTION, game.getCompetition());
            context.startActivity(intent);
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_games_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Game game = games.get(position);

        holder.homeTeam.setText(game.getHome());
        holder.awayTeam.setText(game.getAway());
        holder.competition.setText(game.getCompetition());
        holder.channel.setText(game.getChannel());

        if (game.isLive()) {
            holder.bell.setVisibility(View.GONE);
            holder.live.setVisibility(View.VISIBLE);
            holder.live.startAnimation(AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.pulse));
        } else {
            holder.bell.setVisibility(View.VISIBLE);
            holder.live.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return games.size();
    }

}
