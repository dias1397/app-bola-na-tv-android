package com.diasjoao.bolanatv.ui.activity;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


import com.diasjoao.bolanatv.R;
import com.diasjoao.bolanatv.model.Game;
import com.diasjoao.bolanatv.ui.adapter.GamesAdapter;
import com.diasjoao.bolanatv.util.GamesApi;

import java.util.List;

public class GamesActivity extends AppCompatActivity {

    private SwipeRefreshLayout swipe;
    private View progress, error;
    private final GamesAdapter adapter = new GamesAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_games);

        swipe = findViewById(R.id.swipe);
        progress = findViewById(R.id.progress);
        error = findViewById(R.id.error);

        RecyclerView list = findViewById(R.id.list);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(adapter);

        swipe.setColorSchemeResources(R.color.bp_accent);
        swipe.setProgressBackgroundColorSchemeResource(R.color.bp_surface);
        swipe.setOnRefreshListener(this::load);
        findViewById(R.id.retry).setOnClickListener(v -> load());

        load();
    }

    private void load() {
        error.setVisibility(View.GONE);
        // First load: centered spinner. Pull to refresh: the swipe spinner.
        if (adapter.isEmpty()) {
            progress.setVisibility(View.VISIBLE);
        }

        GamesApi.getInstance(this).fetchGames(new GamesApi.Callback() {
            @Override
            public void onSuccess(List<Game> games) {
                progress.setVisibility(View.GONE);
                swipe.setRefreshing(false);
                adapter.submit(games);
            }

            @Override
            public void onError(Exception e) {
                progress.setVisibility(View.GONE);
                swipe.setRefreshing(false);
                // If we already have data, keep showing it; only show the error screen when empty.
                if (adapter.isEmpty()) {
                    error.setVisibility(View.VISIBLE);
                }
            }
        });
    }
}