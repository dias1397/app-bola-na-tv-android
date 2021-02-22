package com.diasjoao.bolanatv.activities;

import com.diasjoao.bolanatv.BuildConfig;
import com.diasjoao.bolanatv.R;
import com.diasjoao.bolanatv.models.Game;
import com.diasjoao.bolanatv.utils.NetworkUtils;
import com.diasjoao.bolanatv.utils.DateUtils;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoadingActivity extends AppCompatActivity {

    private RequestQueue requestQueue;
    private final String apiUrl = BuildConfig.API_URL_BOLA_NA_TV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        TextView retryText = findViewById(R.id.retryText);
        retryText.setVisibility(View.GONE);
        Button retry = findViewById(R.id.retry);
        retry.setVisibility(View.GONE);

        ImageView logo = findViewById(R.id.logo);
        Animation animation = AnimationUtils.loadAnimation(LoadingActivity.this, R.anim.pulse);
        logo.startAnimation(animation);

        TextView loadingText = findViewById(R.id.loading);
        loadingText.setVisibility(View.VISIBLE);

        requestQueue = Volley.newRequestQueue(this);

        if (NetworkUtils.isNetworkConnected(this)) {
            jsonParse();
        } else {
            logo.clearAnimation();
            loadingText.setVisibility(View.GONE);

            retryText.setVisibility(View.VISIBLE);
            retry.setVisibility(View.VISIBLE);
            retry.setOnClickListener(view -> {
                finish();
                startActivity(getIntent());
            });
        }
    }

    private void jsonParse() {
        Map<Date, Map<String, List<Game>>> result = new HashMap<>();

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, apiUrl, null, response -> {
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

                    result.get(DateUtils.simpleDateFormat1.parse(jsonObject.getString("date"))).get(jsonObject.getString("time")).add(
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
                startActivity(intent);
                finish();
            } catch (JSONException | ParseException e) {
                e.printStackTrace();
            }
        }, Throwable::printStackTrace);

        requestQueue.add(request);
    }
}