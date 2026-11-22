package com.diasjoao.bolanatv.settings;

import android.content.Context;
import android.content.SharedPreferences;

import com.diasjoao.bolanatv.data.Game;

import java.text.Normalizer;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public final class ChannelSettings {

    public static final String[] CHANNELS = {
            "SportTV", "DAZN", "Canal 11", "Sporting TV", "BTV",
            "A Bola TV", "Porto Canal", "RTP", "V+"
    };

    private static final String PREFS = "channel_settings";
    private static final String KEY_HIDDEN = "hidden";

    private ChannelSettings() {}

    /** "SportTV 2" -> "sporttv", "DAZN 4" -> "dazn", "Canal 11" -> "canal11". */
    public static String keyOf(String channel) {
        String key = normalize(channel);
        if (key.startsWith("sporttv")) return "sporttv";
        if (key.startsWith("dazn")) return "dazn";
        if (key.startsWith("benfica")) return "btv";
        return key;
    }

    // "SportTV 2" -> "sporttv2", "Sporting TV" -> "sportingtv", "A Bola TV" -> "abolatv"
    public static String normalize(String s) {
        if (s == null) return "";
        String n = Normalizer.normalize(s, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        return n.toLowerCase(Locale.ROOT).replaceAll("[\\s.\\-]", "");
    }

    private static SharedPreferences prefs(Context c) {
        return c.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public static Set<String> hiddenKeys(Context c) {
        // The set returned by SharedPreferences must not be modified, so copy it.
        return new HashSet<>(prefs(c).getStringSet(KEY_HIDDEN, Collections.emptySet()));
    }

    public static boolean hasHidden(Context c) {
        return !hiddenKeys(c).isEmpty();
    }

    public static boolean isVisible(Set<String> hiddenKeys, Game g) {
        return !hiddenKeys.contains(keyOf(g.getChannel()));
    }

    public static void setVisible(Context c, String key, boolean visible) {
        Set<String> hidden = hiddenKeys(c);
        if (visible) hidden.remove(key); else hidden.add(key);
        prefs(c).edit().putStringSet(KEY_HIDDEN, hidden).apply();
    }

    public static void showAll(Context c) {
        prefs(c).edit().remove(KEY_HIDDEN).apply();
    }
}
