package com.diasjoao.bolanatv.ui.games;

import android.animation.ObjectAnimator;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.widget.ViewPager2;

import com.diasjoao.bolanatv.R;
import com.diasjoao.bolanatv.ads.AdsManager;
import com.diasjoao.bolanatv.data.Game;
import com.diasjoao.bolanatv.data.GamesApi;
import com.diasjoao.bolanatv.settings.ThemeSettings;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.List;

public class GamesActivity extends AppCompatActivity {

    private static final long MINUTE_MS = 60_000L;

    private MaterialToolbar toolbar;
    private TabLayout tabs;
    private ViewPager2 pager;
    private View skeleton, skeletonTabs, errorView, emptyView;

    private DayPagerAdapter pagerAdapter;
    private TabLayoutMediator mediator;
    private AdsManager ads;
    private ObjectAnimator pulse;

    private DaySchedule schedule = DaySchedule.empty();
    private String currentDate;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable tick = new Runnable() {
        @Override
        public void run() {
            pagerAdapter.refreshHints();
            // Run again right after the next minute starts.
            handler.postDelayed(this, MINUTE_MS - (System.currentTimeMillis() % MINUTE_MS) + 500L);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_games);

        bindViews();
        setupInsets();
        setupToolbar();
        setupPager();

        ads = new AdsManager(this, findViewById(R.id.ad_container), this::updatePrivacyItem);
        ads.start();

        load();
    }

    // ---- Setup ----

    private void bindViews() {
        toolbar = findViewById(R.id.toolbar);
        tabs = findViewById(R.id.tabs);
        pager = findViewById(R.id.pager);
        skeleton = findViewById(R.id.skeleton);
        skeletonTabs = findViewById(R.id.tabs_skeleton);
        errorView = findViewById(R.id.error);
        emptyView = findViewById(R.id.empty);
        findViewById(R.id.retry).setOnClickListener(v -> load());
    }

    private void setupInsets() {
        View header = findViewById(R.id.header);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.root), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            header.setPadding(0, bars.top, 0, 0); // header color extends behind the status bar
            v.setPadding(0, 0, 0, bars.bottom);   // keep the content above the navigation bar
            return insets;
        });
    }

    private void setupToolbar() {
        toolbar.inflateMenu(R.menu.main_menu);
        toolbar.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.action_theme) { showThemeDialog(); return true; }
            if (id == R.id.action_privacy) { ads.showPrivacyOptions(); return true; }
            if (id == R.id.action_about) { showAbout(); return true; }
            return false;
        });
    }

    private void setupPager() {
        pagerAdapter = new DayPagerAdapter(
                game -> GameSheet.newInstance(game).show(getSupportFragmentManager(), "game"),
                this::load);
        pager.setAdapter(pagerAdapter);

        pager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                if (position >= 0 && position < schedule.dates.size()) {
                    currentDate = schedule.dates.get(position);
                }
            }
        });

        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override public void onTabSelected(TabLayout.Tab tab) { }
            @Override public void onTabUnselected(TabLayout.Tab tab) { }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                RecyclerView inner = (RecyclerView) pager.getChildAt(0);
                RecyclerView.ViewHolder vh = inner.findViewHolderForAdapterPosition(tab.getPosition());
                if (vh != null) {
                    ((RecyclerView) vh.itemView.findViewById(R.id.list)).smoothScrollToPosition(0);
                }
            }
        });
    }

    // ---- Data ----

    private void load() {
        errorView.setVisibility(View.GONE);
        emptyView.setVisibility(View.GONE);
        if (schedule.isEmpty()) {
            showSkeleton(true); // first load only; a pull to refresh keeps the list visible
        }

        GamesApi.getInstance(this).fetchGames(new GamesApi.Callback() {
            @Override
            public void onSuccess(List<Game> games) {
                showSkeleton(false);
                stopRefreshing();
                schedule = DaySchedule.from(games, getString(R.string.tab_today));
                showSchedule();
                emptyView.setVisibility(schedule.isEmpty() ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onError(Exception e) {
                showSkeleton(false);
                stopRefreshing();
                // Keep showing old data if we have it; the error screen only appears when empty.
                if (schedule.isEmpty()) errorView.setVisibility(View.VISIBLE);
            }
        });
    }

    private void showSchedule() {
        // Keep the same day after a refresh; otherwise today; otherwise the first day.
        int selected = schedule.indexOfDate(currentDate);
        if (selected < 0) selected = schedule.todayIndex();
        if (selected < 0) selected = 0;

        pagerAdapter.submit(schedule.games);
        if (mediator != null) mediator.detach();
        mediator = new TabLayoutMediator(tabs, pager,
                (tab, pos) -> tab.setText(schedule.labels.get(pos)));
        mediator.attach();

        tabs.setVisibility(schedule.isEmpty() ? View.GONE : View.VISIBLE);
        if (!schedule.isEmpty()) pager.setCurrentItem(selected, false);
    }

    // Stops the pull-to-refresh spinner on every page.
    private void stopRefreshing() {
        RecyclerView inner = (RecyclerView) pager.getChildAt(0);
        for (int i = 0; i < inner.getChildCount(); i++) {
            View page = inner.getChildAt(i);
            if (page instanceof SwipeRefreshLayout) {
                ((SwipeRefreshLayout) page).setRefreshing(false);
            }
        }
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
                pulse.addUpdateListener(a -> skeletonTabs.setAlpha((float) a.getAnimatedValue()));
            }
            if (pulse != null && !pulse.isStarted()) pulse.start();
        } else if (pulse != null) {
            pulse.cancel();
            skeleton.setAlpha(1f);
            skeletonTabs.setAlpha(1f);
        }
    }

    // ---- Menu ----

    private void updatePrivacyItem() {
        if (ads == null) return;
        toolbar.getMenu().findItem(R.id.action_privacy)
                .setVisible(ads.isPrivacyOptionsRequired());
    }

    private void showThemeDialog() {
        String[] options = {
                getString(R.string.theme_system),
                getString(R.string.theme_light),
                getString(R.string.theme_dark)
        };
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.menu_theme)
                .setSingleChoiceItems(options, ThemeSettings.get(this), (dialog, which) -> {
                    ThemeSettings.set(this, which);
                    dialog.dismiss();
                })
                .setNegativeButton(R.string.action_cancel, null)
                .show();
    }

    private void showAbout() {
        String version = "";
        try {
            version = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException ignored) { }
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.app_name)
                .setMessage(getString(R.string.about_version, version)
                        + "\n\n" + getString(R.string.app_description))
                .setPositiveButton(R.string.action_ok, null)
                .show();
    }

    // ---- Lifecycle ----

    @Override
    protected void onStart() {
        super.onStart();
        handler.post(tick);
    }

    @Override
    protected void onStop() {
        handler.removeCallbacks(tick);
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ads.resume();
    }

    @Override
    protected void onPause() {
        ads.pause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (pulse != null) pulse.cancel();
        ads.destroy();
        super.onDestroy();
    }
}