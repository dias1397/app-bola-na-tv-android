package com.diasjoao.bolanatv.ui.games;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.diasjoao.bolanatv.R;
import com.diasjoao.bolanatv.data.Game;
import com.diasjoao.bolanatv.settings.ChannelSettings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class DayPagerAdapter extends RecyclerView.Adapter<DayPagerAdapter.PageVH> {

    private final List<List<Game>> days = new ArrayList<>();
    private Set<String> hiddenKeys = Collections.emptySet();

    private final GamesAdapter.OnGameClick onGameClick;
    private final Runnable onRefresh;
    private final Runnable onEditChannels;
    private final Runnable onShowAllChannels;

    public DayPagerAdapter(GamesAdapter.OnGameClick onGameClick, Runnable onRefresh,
                           Runnable onEditChannels, Runnable onShowAllChannels) {
        this.onGameClick = onGameClick;
        this.onRefresh = onRefresh;
        this.onEditChannels = onEditChannels;
        this.onShowAllChannels = onShowAllChannels;
    }

    public void submit(List<List<Game>> newDays, Set<String> hidden) {
        days.clear();
        days.addAll(newDays);
        hiddenKeys = hidden;
        notifyDataSetChanged();
    }

    public void setHiddenKeys(Set<String> hidden) {
        if (hiddenKeys.equals(hidden)) return;
        hiddenKeys = hidden;
        notifyDataSetChanged();
    }

    public void refreshHints() {
        if (!days.isEmpty()) notifyItemRangeChanged(0, days.size(), "hints");
    }

    @NonNull
    @Override
    public PageVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.page_day, parent, false);
        return new PageVH(v, onGameClick, onRefresh, onEditChannels, onShowAllChannels);
    }

    @Override
    public void onBindViewHolder(@NonNull PageVH h, int position) {
        List<Game> all = days.get(position);
        List<Game> visible = new ArrayList<>();
        for (Game g : all) {
            if (ChannelSettings.isVisible(hiddenKeys, g)) visible.add(g);
        }
        h.adapter.submit(visible);

        // Only when the day HAS games and all of them are hidden by the user's channels.
        boolean allHidden = visible.isEmpty() && !all.isEmpty();
        h.list.setVisibility(allHidden ? View.GONE : View.VISIBLE);
        h.emptyView.setVisibility(allHidden ? View.VISIBLE : View.GONE);
        if (allHidden) {
            Context c = h.itemView.getContext();
            h.emptyMessage.setText(c.getResources().getQuantityString(
                    R.plurals.channels_empty_message, all.size(), all.size()));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull PageVH h, int position, @NonNull List<Object> payloads) {
        if (payloads.isEmpty()) {
            onBindViewHolder(h, position);
        } else {
            h.adapter.refresh(); // only the time hints changed
        }
    }

    @Override
    public int getItemCount() {
        return days.size();
    }

    static class PageVH extends RecyclerView.ViewHolder {
        final GamesAdapter adapter = new GamesAdapter();
        final RecyclerView list;
        final View emptyView;
        final TextView emptyMessage;

        PageVH(@NonNull View v, GamesAdapter.OnGameClick click, Runnable refresh,
               Runnable editChannels, Runnable showAllChannels) {
            super(v);
            list = v.findViewById(R.id.list);
            list.setLayoutManager(new LinearLayoutManager(v.getContext()));
            list.setAdapter(adapter);
            adapter.setOnGameClick(click);

            emptyView = v.findViewById(R.id.channels_empty);
            emptyMessage = v.findViewById(R.id.channels_empty_message);
            v.findViewById(R.id.channels_edit).setOnClickListener(x -> editChannels.run());
            v.findViewById(R.id.channels_show_all).setOnClickListener(x -> showAllChannels.run());

            SwipeRefreshLayout swipe = v.findViewById(R.id.swipe);
            swipe.setColorSchemeResources(R.color.bp_accent);
            swipe.setProgressBackgroundColorSchemeResource(R.color.bp_surface);
            swipe.setOnRefreshListener(refresh::run);
            // The container is a FrameLayout, so tell the swipe layout when the LIST is scrolled.
            swipe.setOnChildScrollUpCallback((parent, child) -> list.canScrollVertically(-1));
        }
    }
}