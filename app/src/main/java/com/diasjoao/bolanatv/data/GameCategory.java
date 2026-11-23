package com.diasjoao.bolanatv.data;

import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;

/** Whether a game is men's senior football, women's or youth, judged by the competition name. */
public enum GameCategory {
    MEN, WOMEN, YOUTH;

    // Matched against the lowercase competition name without accents.
    private static final Pattern WOMEN_PATTERN = Pattern.compile(
            "femin|femen|femmin|women|woman|frauen|damas|\\bbpi\\b|\\bwsl\\b|\\bnwsl\\b|\\bliga f\\b");
    private static final Pattern YOUTH_PATTERN = Pattern.compile(
            "\\bsub[- ]?\\d{2}\\b|\\bu[- ]?\\d{2}\\b|\\bunder[- ]?\\d{2}\\b"
                    + "|next gen|youth|juniores|juvenis|iniciados|revelacao|primavera|premier league 2\\b");

    /** Unknown competitions count as MEN, the safer mistake for a favorites highlight. */
    public static GameCategory of(Game game) {
        String competition = game.getCompetition() == null ? "" : game.getCompetition();
        String c = Normalizer.normalize(competition, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT);
        if (WOMEN_PATTERN.matcher(c).find()) return WOMEN;
        if (YOUTH_PATTERN.matcher(c).find()) return YOUTH;
        return MEN;
    }
}