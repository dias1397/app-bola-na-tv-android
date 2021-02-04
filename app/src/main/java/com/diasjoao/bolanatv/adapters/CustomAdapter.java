package com.diasjoao.bolanatv.adapters;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.diasjoao.bolanatv.R;
import com.diasjoao.bolanatv.models.Game;

import java.util.List;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.GameHolder> {

    //private String[] localDataSet;
    private List<Game> localDataSet;

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View

            textView = (TextView) view.findViewById(R.id.time);
        }

        public TextView getTextView() {
            return textView;
        }
    }

    /**
     * Initialize the dataset of the Adapter.
     *
     * @param dataSet String[] containing the data to populate views to be used
     * by RecyclerView.
     */
    public CustomAdapter(List<Game> dataSet) {
        localDataSet = dataSet;
    }
    /*public CustomAdapter(String[] dataSet) {
        localDataSet = dataSet;
    }*/

    // Create new views (invoked by the layout manager)
    @Override
    public GameHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.text_row_item, viewGroup, false);

        return new GameHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(GameHolder gameHolder, final int position) {
        final Game game = localDataSet.get(position);
        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        gameHolder.setTime(game.getTime());
        gameHolder.setTeams(game.getHome() + " - " + game.getAway());
        gameHolder.setCompetition(game.getCompetition());
        gameHolder.setChannel(game.getChannel());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return localDataSet.size();
    }

    public class GameHolder extends RecyclerView.ViewHolder {

        private TextView txtTime;
        private TextView txtTeams;
        private TextView txtCompetition;
        private TextView txtChannel;

        public GameHolder(@NonNull View itemView) {
            super(itemView);

            txtTime = itemView.findViewById(R.id.time);
            txtTeams = itemView.findViewById(R.id.teams);
            txtCompetition = itemView.findViewById(R.id.competition);
            txtChannel = itemView.findViewById(R.id.channel);
        }

        public void setTime(String time) {
            txtTime.setText(time);
        }

        public void setTeams(String teams) {
            txtTeams.setText(teams);
        }

        public void setCompetition(String competition) {
            txtCompetition.setText(competition);
        }

        public void setChannel(String channel) {
            txtChannel.setText(channel);
        }
    }
}


