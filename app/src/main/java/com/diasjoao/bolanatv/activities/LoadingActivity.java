package com.diasjoao.bolanatv.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.diasjoao.bolanatv.R;
import com.diasjoao.bolanatv.adapters.CustomAdapter;
import com.diasjoao.bolanatv.models.Game;

import android.os.Bundle;
import android.widget.Button;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class LoadingActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private Button button;

    private RequestQueue requestQueue;

    private List<Game> games;
    private CustomAdapter customAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        button = findViewById(R.id.button);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        requestQueue = Volley.newRequestQueue(this);

        button.setOnClickListener(view -> games = jsonParse());

    }

    private ArrayList<Game> jsonParse() {
        List<Game> result = new ArrayList<>();
        String url = "https://bola-na-tv-api.vercel.app/";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, response -> {
            try {
                JSONArray jsonArray = response.getJSONArray("games");

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    result.add(
                        new Game(
                            jsonObject.getString("date"),
                            jsonObject.getString("time"),
                            jsonObject.getString("homeTeam"),
                            jsonObject.getString("awayTeam"),
                            jsonObject.getString("channel"),
                            jsonObject.getString("competition")
                        )
                    );

                    System.out.println(result.get(i));

                    customAdapter = new CustomAdapter(result);
                    recyclerView.setAdapter(customAdapter);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }, Throwable::printStackTrace);

        requestQueue.add(request);


        return (ArrayList<Game>) result;
    }
}