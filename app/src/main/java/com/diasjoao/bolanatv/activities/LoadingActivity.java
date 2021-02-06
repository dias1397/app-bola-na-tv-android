package com.diasjoao.bolanatv.activities;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.diasjoao.bolanatv.R;
import com.diasjoao.bolanatv.models.Game;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoadingActivity extends AppCompatActivity {
    private Button button;

    private RequestQueue requestQueue;
    private SimpleDateFormat sdfmt = new SimpleDateFormat("dd-MM-yy");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        button = findViewById(R.id.button);

        requestQueue = Volley.newRequestQueue(this);

        button.setOnClickListener(view -> jsonParse());

    }

    private void jsonParse() {
        Map<Date, List<Game>> result = new HashMap<>();
        String url = "https://bola-na-tv-api.vercel.app/";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, response -> {
            try {
                JSONArray jsonArray = response.getJSONArray("games");

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    if (!result.containsKey(sdfmt.parse(jsonObject.getString("date")))) {
                        result.put(sdfmt.parse(jsonObject.getString("date")), new ArrayList<>());
                    }
                    result.get(sdfmt.parse(jsonObject.getString("date"))).add(
                        new Game(
                            jsonObject.getString("date"),
                            jsonObject.getString("time"),
                            jsonObject.getString("homeTeam"),
                            jsonObject.getString("awayTeam"),
                            jsonObject.getString("channel"),
                            jsonObject.getString("competition")
                        )
                    );
                }

                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("param1", (Serializable) result);
                startActivity(intent);
            } catch (JSONException | ParseException e) {
                e.printStackTrace();
            }

        }, Throwable::printStackTrace);

        requestQueue.add(request);
    }
}