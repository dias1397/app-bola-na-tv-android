package com.diasjoao.bolanatv.ui.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.diasjoao.bolanatv.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.appbar.MaterialToolbar;

public class PrivacyActivity extends AppCompatActivity {
    private MaterialToolbar materialToolbar;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy);

        getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryVariant, null));

        materialToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(materialToolbar);
        materialToolbar.setNavigationOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        MobileAds.initialize(this, initializationStatus -> {
        });
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }
}
