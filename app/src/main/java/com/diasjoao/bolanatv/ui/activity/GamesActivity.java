package com.diasjoao.bolanatv.ui.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.animation.ObjectAnimator;
import android.provider.Settings;
import android.content.pm.PackageManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.widget.ViewPager2;

import android.util.DisplayMetrics;
import android.widget.FrameLayout;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.ump.ConsentInformation;
import com.google.android.ump.ConsentRequestParameters;
import com.google.android.ump.UserMessagingPlatform;

import java.util.concurrent.atomic.AtomicBoolean;
import com.diasjoao.bolanatv.R;
import com.diasjoao.bolanatv.model.Game;
import com.diasjoao.bolanatv.ui.adapter.DayPagerAdapter;
import com.diasjoao.bolanatv.util.GamesApi;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;
import java.util.List;

public class GamesActivity extends AppCompatActivity {

    private static final Locale PT = Locale.forLanguageTag("pt-PT");

    private TabLayout tabs;
    private View skeleton, skeletonTabs, error, emptyView;
    private ObjectAnimator pulse;
    private MaterialToolbar toolbar;
    private FrameLayout adContainer;
    private AdView adBannerView;
    private ConsentInformation consent;
    private final AtomicBoolean adsStarted = new AtomicBoolean(false);

    private ViewPager2 pager;
    private DayPagerAdapter pagerAdapter;
    private TabLayoutMediator mediator;
    private final List<String> labels = new ArrayList<>();

    private List<Game> allGames = new ArrayList<>();
    private final List<String> dates = new ArrayList<>();
    private String currentDate;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable tick = new Runnable() {
        @Override
        public void run() {
            if (pagerAdapter != null) pagerAdapter.refreshHints();
            // Run again right after the next minute starts.
            handler.postDelayed(this, 60_000L - (System.currentTimeMillis() % 60_000L) + 500L);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeSettings.applySaved(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_games);

        View header = findViewById(R.id.header);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.root), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            header.setPadding(0, bars.top, 0, 0);   // header color extends behind the status bar
            v.setPadding(0, 0, 0, bars.bottom);     // keep the list above the navigation bar
            return insets;
        });
        toolbar = findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.main_menu);
        toolbar.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.action_theme) { showThemeDialog(); return true; }
            if (id == R.id.action_privacy) {
                UserMessagingPlatform.showPrivacyOptionsForm(this, formError -> { });
                return true;
            }
            if (id == R.id.action_about) { showAbout(); return true; }
            return false;
        });

        adContainer = findViewById(R.id.ad_container);
        setupAds();

        skeleton = findViewById(R.id.skeleton);
        skeletonTabs = findViewById(R.id.tabs_skeleton);
        emptyView = findViewById(R.id.empty);
        error = findViewById(R.id.error);

        findViewById(R.id.retry).setOnClickListener(v -> load());

        tabs = findViewById(R.id.tabs);
        pager = findViewById(R.id.pager);
        pagerAdapter = new DayPagerAdapter(
                game -> GameSheet.newInstance(game).show(getSupportFragmentManager(), "game"),
                this::load);
        pager.setAdapter(pagerAdapter);
        pager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                if (position >= 0 && position < dates.size()) currentDate = dates.get(position);
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
                    RecyclerView list = vh.itemView.findViewById(R.id.list);
                    list.smoothScrollToPosition(0);
                }
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
                stopRefreshing();
                allGames = new ArrayList<>(games);
                allGames.sort(Comparator.comparing(GamesActivity::sortKey));
                buildTabs();
                emptyView.setVisibility(allGames.isEmpty() ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onError(Exception e) {
                showSkeleton(false);
                stopRefreshing();
                // If we already have data, keep showing it; only show the error screen when empty.
                if (allGames.isEmpty()) {
                    error.setVisibility(View.VISIBLE);
                }
            }
        });
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

        List<List<Game>> perDay = new ArrayList<>();
        labels.clear();
        for (String d : dates) {
            List<Game> day = new ArrayList<>();
            for (Game g : allGames) {
                if (d.equals(g.getDate())) day.add(g);
            }
            perDay.add(day);

            String label = d; // fallback if the date can't be parsed
            try {
                Date date = parse.parse(d);
                String name = d.equals(today)
                        ? "Hoje"
                        : capitalize(dayFmt.format(date).replace(".", ""));
                label = name + "\n" + dateFmt.format(date).replace(".", "");
            } catch (ParseException ignored) { }
            labels.add(label);
        }

        // Keep the same day after a refresh; otherwise today; otherwise the first day.
        int selected = dates.indexOf(currentDate);
        if (selected < 0) selected = dates.indexOf(today);
        if (selected < 0) selected = 0;

        pagerAdapter.submit(perDay);
        if (mediator != null) mediator.detach();
        mediator = new TabLayoutMediator(tabs, pager, (tab, pos) -> tab.setText(labels.get(pos)));
        mediator.attach();

        tabs.setVisibility(dates.isEmpty() ? View.GONE : View.VISIBLE);
        if (!dates.isEmpty()) pager.setCurrentItem(selected, false);
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

    private void setupAds() {
        consent = UserMessagingPlatform.getConsentInformation(this);
        consent.requestConsentInfoUpdate(this, new ConsentRequestParameters.Builder().build(),
                () -> UserMessagingPlatform.loadAndShowConsentFormIfRequired(this, formError -> {
                    updatePrivacyItem();
                    startAds();
                }),
                requestError -> {
                    updatePrivacyItem();
                    startAds();
                });

        startAds(); // consent saved from a previous session may already allow ads
    }

    private void startAds() {
        if (isDestroyed() || !consent.canRequestAds()
                || !adsStarted.compareAndSet(false, true)) return;

        new Thread(() -> MobileAds.initialize(this,
                status -> runOnUiThread(this::loadBanner))).start();
    }

    private void loadBanner() {
        if (isDestroyed()) return;

        DisplayMetrics dm = getResources().getDisplayMetrics();
        adBannerView = new AdView(this);
        adBannerView.setAdUnitId(getString(R.string.admob_banner_main)); // same banner id as before
        adBannerView.setAdSize(AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(
                this, (int) (dm.widthPixels / dm.density)));
        adBannerView.setAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(LoadAdError error) {
                adContainer.removeAllViews(); // no blank band when nothing loads
            }
        });

        adContainer.removeAllViews();
        adContainer.addView(adBannerView);
        adBannerView.loadAd(new AdRequest.Builder().build());
    }

    private void updatePrivacyItem() {
        if (toolbar == null || consent == null) return;
        toolbar.getMenu().findItem(R.id.action_privacy).setVisible(
                consent.getPrivacyOptionsRequirementStatus()
                        == ConsentInformation.PrivacyOptionsRequirementStatus.REQUIRED);
    }

    private void showThemeDialog() {
        String[] options = {"Seguir o sistema", "Claro", "Escuro"};
        new MaterialAlertDialogBuilder(this)
                .setTitle("Tema")
                .setSingleChoiceItems(options, ThemeSettings.get(this), (dialog, which) -> {
                    ThemeSettings.set(this, which);
                    dialog.dismiss();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void showAbout() {
        String version = "";
        try {
            version = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException ignored) { }
        new MaterialAlertDialogBuilder(this)
                .setTitle("Bola na TV")
                .setMessage("Versão " + version
                        + "\n\nHorários e canais sujeitos a alteração pelas emissoras.")
                .setPositiveButton("OK", null)
                .show();
    }

    @Override
    protected void onDestroy() {
        if (pulse != null) pulse.cancel();
        if (adBannerView != null) {
            adContainer.removeAllViews();
            adBannerView.destroy();
            adBannerView = null;
        }
        super.onDestroy();
    }

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
    protected void onPause() {
        if (adBannerView != null) adBannerView.pause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adBannerView != null) adBannerView.resume();
    }
}