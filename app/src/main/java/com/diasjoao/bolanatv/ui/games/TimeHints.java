package com.diasjoao.bolanatv.ui.games;

import android.content.Context;

import com.diasjoao.bolanatv.R;
import com.diasjoao.bolanatv.data.Game;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public final class TimeHints {

    private static final long SOON_WINDOW_MIN = 60;    // "Em N min" appears this long before kickoff
    private static final long STARTED_WINDOW_MIN = 30; // "Há N min" lasts this long after kickoff
    private static final long NOW_WINDOW_MIN = 5;      // "Agora" within ±5 minutes of kickoff

    private static final SimpleDateFormat FORMAT =
            new SimpleDateFormat("dd-MM-yy HH:mm", Locale.ROOT);

    static {
        FORMAT.setTimeZone(TimeZone.getTimeZone("Europe/Lisbon"));
    }

    private TimeHints() {}

    /** Returns "Em 40 min", "Agora", "Há 10 min", or null when no hint should show. */
    public static String hintFor(Context context, Game g) {
        if (g.getDate() == null || g.getTime() == null) return null;

        long kickoff;
        try {
            Date d = FORMAT.parse(g.getDate() + " " + g.getTime());
            if (d == null) return null;
            kickoff = d.getTime();
        } catch (ParseException e) {
            return null;
        }

        // Whole minutes until kickoff (negative once it has started).
        long m = Math.floorDiv(kickoff - System.currentTimeMillis(), 60_000L);

        if (m > SOON_WINDOW_MIN || m < -STARTED_WINDOW_MIN) return null;
        if (Math.abs(m) <= NOW_WINDOW_MIN) return context.getString(R.string.hint_now);
        if (m > 0) {
            return context.getString(R.string.hint_in_minutes, m);
        }
        return context.getString(R.string.hint_minutes_ago, -m);
    }
}