package com.diasjoao.bolanatv;

import android.app.Application;

import com.diasjoao.bolanatv.settings.ThemeSettings;

public class BolaApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ThemeSettings.applySaved(this);
    }
}