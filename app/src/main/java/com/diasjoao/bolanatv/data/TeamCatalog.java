package com.diasjoao.bolanatv.data;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * The fixed list of teams the user can choose from (assets/teams.json), plus the lookup that
 * turns a team name sent by the API into a catalog team.
 */
public final class TeamCatalog {

    public static final class Group {
        public final String country;
        public final String name;
        public final List<Team> teams;

        Group(String country, String name, List<Team> teams) {
            this.country = country;
            this.name = name;
            this.teams = teams;
        }
    }

    private static TeamCatalog instance;

    private final List<Group> groups = new ArrayList<>();
    private final Map<String, Team> byId = new HashMap<>();
    private final Map<String, Team> byKey = new HashMap<>(); // normalized name or alias -> team

    public static synchronized TeamCatalog get(Context context) {
        if (instance == null) instance = new TeamCatalog(context.getApplicationContext());
        return instance;
    }

    private TeamCatalog(Context context) {
        try {
            JSONArray jsonGroups = new JSONObject(readAsset(context, "teams.json"))
                    .getJSONArray("groups");
            for (int g = 0; g < jsonGroups.length(); g++) {
                JSONObject jg = jsonGroups.getJSONObject(g);
                String groupName = jg.getString("name");
                List<Team> teams = new ArrayList<>();

                JSONArray jsonTeams = jg.getJSONArray("teams");
                for (int t = 0; t < jsonTeams.length(); t++) {
                    JSONObject jt = jsonTeams.getJSONObject(t);
                    Team team = new Team(jt.getString("id"), jt.getString("name"), groupName);
                    teams.add(team);
                    byId.put(team.id, team);
                    index(team.name, team);

                    JSONArray aliases = jt.optJSONArray("aliases");
                    if (aliases != null) {
                        for (int a = 0; a < aliases.length(); a++) index(aliases.getString(a), team);
                    }
                }
                groups.add(new Group(jg.optString("country", ""), groupName,
                        Collections.unmodifiableList(teams)));
            }
        } catch (IOException | JSONException e) {
            // A broken catalog must never crash the app: favorites just stay empty.
            groups.clear();
            byId.clear();
            byKey.clear();
        }
    }

    private void index(String name, Team team) {
        String key = normalize(name);
        if (!key.isEmpty() && !byKey.containsKey(key)) byKey.put(key, team); // first one wins
    }

    public List<Group> groups() {
        return Collections.unmodifiableList(groups);
    }

    public Team byId(String id) {
        return byId.get(id);
    }

    /** The catalog team for a name sent by the API, or null if it is not in the catalog. */
    public Team find(String apiName) {
        return byKey.get(normalize(apiName));
    }

    // "Borussia M´gladbach" -> "borussiamgladbach", "Atlético Madrid" -> "atleticomadrid"
    public static String normalize(String s) {
        if (s == null) return "";
        String n = Normalizer.normalize(s, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        return n.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]", "");
    }

    private static String readAsset(Context context, String name) throws IOException {
        try (InputStream in = context.getAssets().open(name)) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            int read;
            while ((read = in.read(buffer)) != -1) out.write(buffer, 0, read);
            return out.toString(StandardCharsets.UTF_8.name());
        }
    }
}