package com.diasjoao.bolanatv;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class SplashActivity extends AppCompatActivity {

    ProgressBar progressBar;
    Button repetir;
    TextView carregando;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        progressBar = findViewById(R.id.progressBar);
        carregando = findViewById(R.id.carregando);
        repetir = findViewById(R.id.repetir);
        repetir.setVisibility(View.GONE);

        progressBar.setIndeterminate(false);
        progressBar.setProgress(0);
        progressBar.setMax(7);

        if (isNetworkConnected()) {
            RetrieveGames task = new RetrieveGames();
            task.setProgressBar(progressBar);
            task.execute();
        } else {
            carregando.setText("Sem Ligação\n à Internet");
            repetir.setVisibility(View.VISIBLE);

            repetir.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                    startActivity(getIntent());
                }
            });
        }

    }

    class RetrieveGames extends AsyncTask<String, Integer, LinkedHashMap<String, List<Game>>> {

        ProgressBar pb;
        int status = 0;

        public void setProgressBar(ProgressBar progressBar) {
            this.pb = progressBar;
        }

        protected LinkedHashMap<String, List<Game>> doInBackground(String... urls) {
            LinkedHashMap<String, List<Game>> result = new LinkedHashMap<>();

            try {
                Document doc = Jsoup.connect("https://www.futebol365.pt/jogos-na-tv/?order=date").get();
                Elements days = doc.getElementsByClass("left-column-group-wrapper").first().children();

                String currentDate = null;

                for (Element child : days) {
                    if (child.tagName().equals("h3")) {
                        currentDate = child.text();
                    }
                    if (child.tagName().equals("table")) {
                        ArrayList<Game> games = new ArrayList<>();
                        for (Element row : child.child(1).children()) {
                            games.add(new Game(row.child(0).text(), row.child(1).text(), row.child(3).text(), row.child(5).text()));
                        }
                        result.put(currentDate, games);
                    }
                    status++;
                    publishProgress(status);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            return result;
        }

        @Override
        protected void onProgressUpdate(Integer[] values) {
            pb.setProgress(values[0]);
            super.onProgressUpdate(values);
        }

        protected void onPostExecute(LinkedHashMap<String, List<Game>> result) {
            if (result != null) {
                ArrayList<String> orderPerDay = new ArrayList<>(result.keySet());

                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                intent.putExtra("gamesPerDay", result);
                intent.putExtra("orderPerDay", orderPerDay);
                startActivity(intent);
                finish();
            }
        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }
}
