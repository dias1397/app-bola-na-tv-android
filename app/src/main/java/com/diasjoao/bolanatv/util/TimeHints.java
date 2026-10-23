package com.diasjoao.bolanatv.util;


import com.diasjoao.bolanatv.model.Game;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public final class TimeHints {

    private static final SimpleDateFormat FORMAT =
            new SimpleDateFormat("dd-MM-yy HH:mm", Locale.ROOT);

    static {
        FORMAT.setTimeZone(TimeZone.getTimeZone("Europe/Lisbon"));
    }

    private TimeHints() {}

    /** Returns "Em 40 min", "Agora", "Há 10 min", or null when no hint should show. */
    public static String hintFor(Game g) {
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

        if (m > 60 || m < -30) return null;
        if (m >= -5 && m <= 5) return "Agora";
        if (m > 5) return "Em " + m + " min";   // rounded up to 5
        return "Há " + (-m) + " min";                  // rounded down to 5
    }
}