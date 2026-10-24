package com.diasjoao.bolanatv.settings;

import android.content.Context;

import androidx.appcompat.app.AppCompatDelegate;

public final class ThemeSettings {

    public static final int SYSTEM = 0, LIGHT = 1, DARK = 2;
    private static final String PREFS = "settings";
    private static final String KEY = "theme_mode";

    private ThemeSettings() {}

    public static int get(Context c) {
        return c.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getInt(KEY, SYSTEM);
    }

    public static void set(Context c, int mode) {
        c.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putInt(KEY, mode).apply();
        applySaved(c);
    }

    public static void applySaved(Context c) {
        switch (get(c)) {
            case LIGHT:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case DARK:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            default:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        }
    }
}
