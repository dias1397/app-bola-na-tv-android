package com.diasjoao.bolanatv.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.animation.ObjectAnimator;
import android.provider.Settings;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.diasjoao.bolanatv.R;
import com.diasjoao.bolanatv.model.Game;
import com.diasjoao.bolanatv.ui.adapter.GamesAdapter;
import com.diasjoao.bolanatv.util.GamesApi;
import com.google.android.material.tabs.TabLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;
import java.util.List;

public class GamesActivity extends AppCompatActivity {

    private static final Locale PT = Locale.forLanguageTag("pt-PT");

    private RecyclerView list;
    private TabLayout tabs;
    private SwipeRefreshLayout swipe;
    private View skeleton, skeletonTabs, error, emptyView;
    private ObjectAnimator pulse;
    private final GamesAdapter adapter = new GamesAdapter();

    private List<Game> allGames = new ArrayList<>();
    private final List<String> dates = new ArrayList<>();
    private String currentDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_games);

        swipe = findViewById(R.id.swipe);
        skeleton = findViewById(R.id.skeleton);
        skeletonTabs = findViewById(R.id.tabs_skeleton);
        emptyView = findViewById(R.id.empty);
        error = findViewById(R.id.error);

        list = findViewById(R.id.list);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(adapter);

        adapter.setOnGameClick(game -> GameSheet.newInstance(game).show(getSupportFragmentManager(), "game"));

        swipe.setColorSchemeResources(R.color.bp_accent);
        swipe.setProgressBackgroundColorSchemeResource(R.color.bp_surface);
        swipe.setOnRefreshListener(this::load);
        findViewById(R.id.retry).setOnClickListener(v -> load());

        tabs = findViewById(R.id.tabs);
        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                showDay(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) { }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                list.scrollToPosition(0);
            }
        });

        load();
    }

    private void load() {
        error.setVisibility(View.GONE);
        emptyView.setVisibility(View.GONE);
        // First load: centered spinner. Pull to refresh: the swipe spinner.
        if (allGames.isEmpty()) {
            showSkeleton(true);
        }

        GamesApi.getInstance(this).fetchGames(new GamesApi.Callback() {
            @Override
            public void onSuccess(List<Game> games) {
                showSkeleton(false);
                swipe.setRefreshing(false);
                allGames = new ArrayList<>(games);
                allGames.sort(Comparator.comparing(GamesActivity::sortKey));
                buildTabs();
                emptyView.setVisibility(allGames.isEmpty() ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onError(Exception e) {
                showSkeleton(false);
                swipe.setRefreshing(false);
                // If we already have data, keep showing it; only show the error screen when empty.
                if (allGames.isEmpty()) {
                    error.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void buildTabs() {
        dates.clear();
        for (Game g : allGames) {
            if (g.getDate() != null && !dates.contains(g.getDate())) {
                dates.add(g.getDate());
            }
        }

        SimpleDateFormat parse = new SimpleDateFormat("dd-MM-yy", Locale.ROOT);
        SimpleDateFormat dayFmt = new SimpleDateFormat("EEE", PT);
        SimpleDateFormat dateFmt = new SimpleDateFormat("d MMM", PT);
        String today = parse.format(new Date());

        // Keep the same day after a refresh; otherwise today; otherwise the first day.
        int selected = dates.indexOf(currentDate);
        if (selected < 0) selected = dates.indexOf(today);
        if (selected < 0) selected = 0;

        tabs.removeAllTabs();
        for (String d : dates) {
            String label = d; // fallback if the date can't be parsed
            try {
                Date date = parse.parse(d);
                String day = d.equals(today)
                        ? "Hoje"
                        : capitalize(dayFmt.format(date).replace(".", ""));
                label = day + "\n" + dateFmt.format(date).replace(".", "");
            } catch (ParseException ignored) { }
            tabs.addTab(tabs.newTab().setText(label), false);
        }

        tabs.setVisibility(dates.isEmpty() ? View.GONE : View.VISIBLE);
        if (!dates.isEmpty()) {
            TabLayout.Tab tab = tabs.getTabAt(selected);
            if (tab != null) tab.select();
            showDay(selected);
        }
    }

    private void showDay(int position) {
        if (position < 0 || position >= dates.size()) return;
        currentDate = dates.get(position);

        List<Game> day = new ArrayList<>();
        for (Game g : allGames) {
            if (currentDate.equals(g.getDate())) day.add(g);
        }
        adapter.submit(day);
        list.scrollToPosition(0);
    }

    private static String capitalize(String s) {
        return s.isEmpty() ? s : s.substring(0, 1).toUpperCase(PT) + s.substring(1);
    }

    private static String sortKey(Game g) {
        String d = g.getDate() == null ? "" : g.getDate();
        String t = g.getTime() == null ? "" : g.getTime();
        if (d.length() != 8) return "~" + d + t; // malformed dates go to the end
        return d.substring(6, 8) + d.substring(3, 5) + d.substring(0, 2) + t;
    }

    private void showSkeleton(boolean show) {
        skeleton.setVisibility(show ? View.VISIBLE : View.GONE);
        skeletonTabs.setVisibility(show ? View.VISIBLE : View.GONE);
        if (show) {
            // Respect "remove animations" in system settings: placeholders stay static.
            float scale = Settings.Global.getFloat(getContentResolver(),
                    Settings.Global.ANIMATOR_DURATION_SCALE, 1f);
            if (scale > 0f && pulse == null) {
                pulse = ObjectAnimator.ofFloat(skeleton, View.ALPHA, 1f, 0.45f);
                pulse.setDuration(900);
                pulse.setRepeatMode(ObjectAnimator.REVERSE);
                pulse.setRepeatCount(ObjectAnimator.INFINITE);
                pulse.addUpdateListener(a ->
                        skeletonTabs.setAlpha((float) a.getAnimatedValue()));
            }
            if (pulse != null && !pulse.isStarted()) pulse.start();
        } else if (pulse != null) {
            pulse.cancel();
            skeleton.setAlpha(1f);
            skeletonTabs.setAlpha(1f);
        }
    }

    @Override
    protected void onDestroy() {
        if (pulse != null) pulse.cancel();
        super.onDestroy();
    }
}