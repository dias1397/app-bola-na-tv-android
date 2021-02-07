package com.diasjoao.bolanatv.activities;

import androidx.appcompat.app.AppCompatActivity;

import com.diasjoao.bolanatv.utils.DateUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.diasjoao.bolanatv.R;
import com.diasjoao.bolanatv.models.Game;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

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
    private Button repetir;
    private Animation animation;
    private ImageView logo;

    private RequestQueue requestQueue;
    private SimpleDateFormat sdfmt = new SimpleDateFormat("dd-MM-yy");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        logo = findViewById(R.id.logo);
        repetir = findViewById(R.id.repetir);
        repetir.setVisibility(View.GONE);

        animation = AnimationUtils.loadAnimation(LoadingActivity.this, R.anim.pulse);
        logo.startAnimation(animation);

        requestQueue = Volley.newRequestQueue(this);

        if (isNetworkConnected()) {
            jsonParse();
        } else {
            repetir.setVisibility(View.VISIBLE);
            repetir.setOnClickListener(view -> {
                finish();
                startActivity(getIntent());
            });
        }

    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    private void jsonParse() {
        Map<Date, Map<String, List<Game>>> result = new HashMap<>();
        String url = "https://bola-na-tv-api.vercel.app/";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, response -> {
            try {
                JSONArray jsonArray = response.getJSONArray("games");

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    if (!result.containsKey(DateUtils.simpleDateFormat1.parse(jsonObject.getString("date")))) {
                        result.put(DateUtils.simpleDateFormat1.parse(jsonObject.getString("date")), new HashMap<>());
                    }

                    if (!result.get(DateUtils.simpleDateFormat1.parse(jsonObject.getString("date"))).containsKey(jsonObject.getString("time"))) {
                        result.get(DateUtils.simpleDateFormat1.parse(jsonObject.getString("date"))).put(jsonObject.getString("time"), new ArrayList<>());
                    }

                    result.get(sdfmt.parse(jsonObject.getString("date"))).get(jsonObject.getString("time")).add(
                        new Game(
                            jsonObject.getString("date"),
                            jsonObject.getString("time"),
                            jsonObject.getString("homeTeam"),
                            jsonObject.getString("awayTeam"),
                            jsonObject.getString("channel"),
                            jsonObject.getString("competition").substring(0, jsonObject.getString("competition").length() - 1)
                        )
                    );


                }

                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("param1", (Serializable) result);
                finish();
                startActivity(intent);
            } catch (JSONException | ParseException e) {
                e.printStackTrace();
            }

        }, Throwable::printStackTrace);

        requestQueue.add(request);
    }
}