package com.diasjoao.bolanatv.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.diasjoao.bolanatv.R;
import com.diasjoao.bolanatv.model.Game;
import com.diasjoao.bolanatv.util.NetworkUtils;
import com.diasjoao.bolanatv.util.DateUtils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LoadingActivity extends AppCompatActivity {

    private ImageView logoImageView;
    private MaterialButton retryButton;
    private MaterialTextView statusTextView;

    private String apiUrl;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        initializeViews();
        setupUI();
        loadGamesContent();
    }

    private void initializeViews() {
        logoImageView = findViewById(R.id.logo_imageView);
        retryButton = findViewById(R.id.retry_button);
        statusTextView = findViewById(R.id.status_text);
    }

    private void setupUI() {
        EdgeToEdge.enable(this);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryVariant, null));

        logoImageView.startAnimation(AnimationUtils.loadAnimation(LoadingActivity.this, R.anim.pulse));
        retryButton.setOnClickListener(view -> {
            finish();
            startActivity(getIntent());
        });
    }

    private void loadGamesContent() {
        requestQueue = Volley.newRequestQueue(this);

        if (NetworkUtils.isNetworkConnected(this)) {
            fetchGamesData();
        } else {
            logoImageView.clearAnimation();
            statusTextView.setText(getString(R.string.noConnection));
            retryButton.setVisibility(View.VISIBLE);
        }
    }

    private void fetchGamesData() {
        apiUrl = getString(R.string.api_url_bola_na_tv);
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