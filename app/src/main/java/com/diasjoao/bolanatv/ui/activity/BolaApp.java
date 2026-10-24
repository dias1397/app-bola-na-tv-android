package com.diasjoao.bolanatv.ui.activity;

import android.app.Application;

public class BolaApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ThemeSettings.applySaved(this);
    }
}