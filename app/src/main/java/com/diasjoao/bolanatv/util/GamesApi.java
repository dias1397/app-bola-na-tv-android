package com.diasjoao.bolanatv.util;

import android.content.Context;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.diasjoao.bolanatv.model.Game;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class GamesApi {

    private static final String URL = "https://bola-na-tv-api.vercel.app/";

    private static GamesApi instance;
    private final RequestQueue queue;

    public interface Callback {
        void onSuccess(List<Game> games);
        void onError(Exception error);
    }

    private GamesApi(Context context) {
        queue = Volley.newRequestQueue(context.getApplicationContext());
    }

    public static synchronized GamesApi getInstance(Context context) {
        if (instance == null) {
            instance = new GamesApi(context);
        }
        return instance;
    }

    public void fetchGames(Callback callback) {
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET, URL, null,
                response -> {
                    try {
                        callback.onSuccess(parseGames(response));
                    } catch (JSONException e) {
                        callback.onError(e);
                    }
                },
                callback::onError);

        request.setRetryPolicy(new DefaultRetryPolicy(10_000, 1, 1f));
        queue.add(request);
    }

    private List<Game> parseGames(JSONObject root) throws JSONException {
        if (!"success".equals(root.optString("status"))) {
            throw new JSONException("Invalid status");
        }
        JSONArray array = root.getJSONArray("games");
        List<Game> games = new ArrayList<>(array.length());
        for (int i = 0; i < array.length(); i++) {
            JSONObject o = array.getJSONObject(i);
            Game g = new Game();
            g.setDate(str(o, "date"));
            g.setTime(str(o, "time"));
            g.setHome(str(o, "homeTeam"));
            g.setAway(str(o, "awayTeam"));
            g.setCompetition(str(o, "competition"));
            g.setChannel(str(o, "channel"));
            games.add(g);
        }
        return games;
    }

    // Returns "" for missing or null values, so we never store the text "null".
    private static String str(JSONObject o, String key) {
        return o.isNull(key) ? "" : o.optString(key, "");
    }
}
