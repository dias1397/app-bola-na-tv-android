package com.diasjoao.bolanatv.ads;

import android.app.Activity;
import android.util.DisplayMetrics;
import android.widget.FrameLayout;

import com.diasjoao.bolanatv.R;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.ump.ConsentInformation;
import com.google.android.ump.ConsentRequestParameters;
import com.google.android.ump.UserMessagingPlatform;

import java.util.concurrent.atomic.AtomicBoolean;

/** Consent (UMP) plus the anchored adaptive banner of the main screen. */
public final class AdsManager {

    private final Activity activity;
    private final FrameLayout container;
    private final Runnable onConsentChanged;
    private final ConsentInformation consent;
    private final AtomicBoolean started = new AtomicBoolean(false);
    private AdView adView;
    private boolean released;

    public AdsManager(Activity activity, FrameLayout container, Runnable onConsentChanged) {
        this.activity = activity;
        this.container = container;
        this.onConsentChanged = onConsentChanged;
        this.consent = UserMessagingPlatform.getConsentInformation(activity);
    }

    /** Call once from onCreate. */
    public void start() {
        if (!activity.getResources().getBoolean(R.bool.ads_enabled)) return;

        consent.requestConsentInfoUpdate(activity, new ConsentRequestParameters.Builder().build(),
                () -> UserMessagingPlatform.loadAndShowConsentFormIfRequired(activity,
                        formError -> onConsentResolved()),
                requestError -> onConsentResolved());

        startAdsIfAllowed(); // consent saved from a previous session may already allow ads
    }

    public boolean isPrivacyOptionsRequired() {
        return consent.getPrivacyOptionsRequirementStatus()
                == ConsentInformation.PrivacyOptionsRequirementStatus.REQUIRED;
    }

    public void showPrivacyOptions() {
        UserMessagingPlatform.showPrivacyOptionsForm(activity, formError -> startAdsIfAllowed());
    }

    public void pause()  { if (adView != null) adView.pause(); }
    public void resume() { if (adView != null) adView.resume(); }

    public void destroy() {
        released = true;
        if (adView != null) {
            container.removeAllViews();
            adView.destroy();
            adView = null;
        }
    }

    private void onConsentResolved() {
        onConsentChanged.run();
        startAdsIfAllowed();
    }

    private void startAdsIfAllowed() {
        if (isReleased() || !consent.canRequestAds() || !started.compareAndSet(false, true)) return;
        new Thread(() -> MobileAds.initialize(activity,
                status -> activity.runOnUiThread(this::loadBanner))).start();
    }

    private void loadBanner() {
        if (isReleased()) return;

        DisplayMetrics dm = activity.getResources().getDisplayMetrics();
        adView = new AdView(activity);
        adView.setAdUnitId(activity.getString(R.string.admob_banner_main));
        adView.setAdSize(AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(
                activity, (int) (dm.widthPixels / dm.density)));
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(LoadAdError error) {
                container.removeAllViews(); // no blank band when nothing loads
            }
        });

        container.removeAllViews();
        container.addView(adView);
        adView.loadAd(new AdRequest.Builder().build());
    }

    private boolean isReleased() {
        return released || activity.isDestroyed();
    }
}
