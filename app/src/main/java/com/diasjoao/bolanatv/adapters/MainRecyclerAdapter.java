package com.diasjoao.bolanatv.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.diasjoao.bolanatv.R;
import com.diasjoao.bolanatv.models.Game;
import com.shuhart.stickyheader.StickyAdapter;

import java.util.List;
import java.util.Map;
import java.util.TreeSet;

public class MainRecyclerAdapter extends StickyAdapter<MainRecyclerAdapter.ViewHolder, MainRecyclerAdapter.ViewHolder> {
    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView sectionTitleTextView;
        private final RecyclerView sectionRecyclerView;

        public ViewHolder(View view) {
            super(view);

            sectionTitleTextView = (TextView) view.findViewById(R.id.sectionTitle);
            sectionRecyclerView = (RecyclerView) view.findViewById(R.id.sectionRecyclerView);
        }
    }

    private Context context;
    private Map<String, List<Game>> sectionList;

    /**
     * Initialize the dataset of the Adapter.
     *
     * @param sectionList String[] containing the data to populate views to be used
     * by RecyclerView.
     */
    public MainRecyclerAdapter(Context context, Map<String, List<Game>> sectionList) {
        this.context = context;
        this.sectionList = sectionList;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.row_section, viewGroup, false);

        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final String sectionTitle = (String) (new TreeSet<>(sectionList.keySet())).toArray()[position];
        final List<Game> sectionItems = sectionList.get(sectionTitle);

        holder.sectionTitleTextView.setText(sectionTitle);
        holder.sectionRecyclerView.setHasFixedSize(true);
        holder.sectionRecyclerView.setLayoutManager(new LinearLayoutManager(context));

        ChildRecyclerAdapter childRecyclerAdapter = new ChildRecyclerAdapter(context, sectionItems);
        holder.sectionRecyclerView.setAdapter(childRecyclerAdapter);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return sectionList.keySet().size();
    }

    @Override
    public int getHeaderPositionForItem(int position) {
        return position;
    }

    @Override
    public void onBindHeaderViewHolder(ViewHolder viewHolder, int position) {
        final String sectionTitle = (String) (new TreeSet<>(sectionList.keySet())).toArray()[position];
        viewHolder.sectionTitleTextView.setText(sectionTitle);
    }

    @Override
    public ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        return createViewHolder(parent, 0);
    }

}


