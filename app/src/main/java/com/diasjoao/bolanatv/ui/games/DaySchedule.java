package com.diasjoao.bolanatv.ui.games;

import com.diasjoao.bolanatv.data.Game;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

/** The games grouped into chronologically sorted days, with a tab label for each day. */
final class DaySchedule {

    private static final TimeZone LISBON = TimeZone.getTimeZone("Europe/Lisbon");
    private static final Locale PT = Locale.forLanguageTag("pt-PT");

    final List<String> dates;      // "22-10-26", in chronological order
    final List<String> labels;     // "Hoje\n22 out"
    final List<List<Game>> games;  // the games of each day, sorted by time
    private final int todayIndex;  // -1 when today has no games

    private DaySchedule(List<String> dates, List<String> labels,
                        List<List<Game>> games, int todayIndex) {
        this.dates = dates;
        this.labels = labels;
        this.games = games;
        this.todayIndex = todayIndex;
    }

    static DaySchedule empty() {
        return new DaySchedule(new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), -1);
    }

    static DaySchedule from(List<Game> source, String todayLabel) {
        List<Game> sorted = new ArrayList<>(source);
        sorted.sort(Comparator.comparing(DaySchedule::sortKey));

        Map<String, List<Game>> byDate = new LinkedHashMap<>();
        for (Game g : sorted) {
            if (g.getDate() == null) continue;
            byDate.computeIfAbsent(g.getDate(), k -> new ArrayList<>()).add(g);
        }

        SimpleDateFormat parse = formatter("dd-MM-yy", Locale.ROOT);
        SimpleDateFormat dayFmt = formatter("EEE", PT);
        SimpleDateFormat dateFmt = formatter("d MMM", PT);
        String today = parse.format(new Date());

        List<String> dates = new ArrayList<>(byDate.keySet());
        List<String> labels = new ArrayList<>();
        for (String d : dates) {
            String label = d; // fallback if the date can't be parsed
            try {
                Date date = parse.parse(d);
                String name = d.equals(today)
                        ? todayLabel
                        : capitalize(dayFmt.format(date).replace(".", ""));
                label = name + "\n" + dateFmt.format(date).replace(".", "");
            } catch (ParseException ignored) { }
            labels.add(label);
        }

        return new DaySchedule(dates, labels, new ArrayList<>(byDate.values()),
                dates.indexOf(today));
    }

    boolean isEmpty() {
        return dates.isEmpty();
    }

    int indexOfDate(String date) {
        return dates.indexOf(date);
    }

    int todayIndex() {
        return todayIndex;
    }

    // "22-10-26" + "19:45"  ->  "261022" + "19:45"  (yyMMdd + time)
    private static String sortKey(Game g) {
        String d = g.getDate() == null ? "" : g.getDate();
        String t = g.getTime() == null ? "" : g.getTime();
        if (d.length() != 8) return "~" + d + t; // malformed dates go to the end
        return d.substring(6, 8) + d.substring(3, 5) + d.substring(0, 2) + t;
    }

    private static SimpleDateFormat formatter(String pattern, Locale locale) {
        SimpleDateFormat f = new SimpleDateFormat(pattern, locale);
        f.setTimeZone(LISBON);
        return f;
    }

    private static String capitalize(String s) {
        return s.isEmpty() ? s : s.substring(0, 1).toUpperCase(PT) + s.substring(1);
    }
}
