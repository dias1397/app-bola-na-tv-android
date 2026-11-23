package com.diasjoao.bolanatv.settings;

import android.content.Context;
import android.content.SharedPreferences;

import com.diasjoao.bolanatv.data.Game;
import com.diasjoao.bolanatv.data.GameCategory;
import com.diasjoao.bolanatv.data.Team;
import com.diasjoao.bolanatv.data.TeamCatalog;

public final class FavoriteSettings {

    public static final int NONE = 0, HOME = 1, AWAY = 2;

    private static final String PREFS = "favorite_settings";
    private static final String KEY_TEAM = "team_id";
    private static final String KEY_WOMEN = "include_women";
    private static final String KEY_YOUTH = "include_youth";
    private static final String KEY_INTRO_SEEN = "intro_seen";

    private FavoriteSettings() {}

    private static SharedPreferences prefs(Context c) {
        return c.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    /** The catalog id of the favorite team, or null when none is chosen. */
    public static String teamId(Context c) {
        return prefs(c).getString(KEY_TEAM, null);
    }

    public static boolean hasTeam(Context c) {
        return teamId(c) != null;
    }

    /** Pass null to remove the favorite. */
    public static void setTeam(Context c, String teamId) {
        SharedPreferences.Editor e = prefs(c).edit();
        if (teamId == null) e.remove(KEY_TEAM); else e.putString(KEY_TEAM, teamId);
        e.apply();
    }

    public static boolean includeWomen(Context c) {
        return prefs(c).getBoolean(KEY_WOMEN, false);
    }

    public static void setIncludeWomen(Context c, boolean value) {
        prefs(c).edit().putBoolean(KEY_WOMEN, value).apply();
    }

    public static boolean includeYouth(Context c) {
        return prefs(c).getBoolean(KEY_YOUTH, false);
    }

    public static void setIncludeYouth(Context c, boolean value) {
        prefs(c).edit().putBoolean(KEY_YOUTH, value).apply();
    }

    /** The first-use card shows until the user taps either of its buttons, or picks a team. */
    public static boolean shouldShowIntro(Context c) {
        return !prefs(c).getBoolean(KEY_INTRO_SEEN, false) && !hasTeam(c);
    }

    public static void markIntroSeen(Context c) {
        prefs(c).edit().putBoolean(KEY_INTRO_SEEN, true).apply();
    }

    /**
     * NONE when the game does not count as a favorite game, otherwise HOME or AWAY: the side
     * the favorite team plays on. Women's and youth games only count when the user allows them.
     */
    public static int sideOf(TeamCatalog catalog, String favoriteId,
                             boolean women, boolean youth, Game g) {
        if (favoriteId == null) return NONE;

        Team home = catalog.find(g.getHome());
        Team away = catalog.find(g.getAway());
        int side = NONE;
        if (home != null && favoriteId.equals(home.id)) side = HOME;
        else if (away != null && favoriteId.equals(away.id)) side = AWAY;
        if (side == NONE) return NONE;

        switch (GameCategory.of(g)) {
            case WOMEN: return women ? side : NONE;
            case YOUTH: return youth ? side : NONE;
            default:    return side;
        }
    }

    public static boolean isFavoriteGame(Context c, Game g) {
        return sideOf(TeamCatalog.get(c), teamId(c), includeWomen(c), includeYouth(c), g) != NONE;
    }
}