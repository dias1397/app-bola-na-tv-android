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

    class RetrieveGames extends AsyncTask<String, Integer, Boolean> {

        ProgressBar pb;
        int status = 0;

        LinkedHashMap<String, List<Game>> resultByDate = new LinkedHashMap<>();
        LinkedHashMap<String, List<Game>> resultByChannel = new LinkedHashMap<>();
        LinkedHashMap<String, List<Game>> resultByCompetition = new LinkedHashMap<>();

        public void setProgressBar(ProgressBar progressBar) {
            this.pb = progressBar;
        }

        protected Boolean doInBackground(String... urls) {

            try {
                Document doc = Jsoup.connect("https://www.futebol365.pt/jogos-na-tv/?order=date").get();
                Elements date = doc.getElementsByClass("left-column-group-wrapper").first().children();

                String currentDate = null;

                for (Element child : date) {
                    if (child.tagName().equals("h3")) {
                        currentDate = child.text();
                    }
                    if (child.tagName().equals("table")) {
                        ArrayList<Game> games = new ArrayList<>();
                        for (Element row : child.child(1).children()) {
                            games.add(new Game(null, row.child(0).text(), row.child(1).text(), row.child(3).text(), row.child(5).text()));
                        }
                        resultByDate.put(currentDate, games);
                    }
                }

                doc = Jsoup.connect("https://www.futebol365.pt/jogos-na-tv/?order=channel").get();
                Elements channel = doc.getElementsByClass("left-column-group-wrapper").first().children();

                String currentChannel = null;

                for (Element child : channel) {
                    if (child.tagName().equals("h3")) {
                        currentChannel = child.text();
                        continue;
                    }
                    if (child.tagName().equals("table")) {
                        ArrayList<Game> games = new ArrayList<>();
                        for (Element row : child.child(1).children()) {
                            games.add(new Game(row.child(0).text(), row.child(1).text(), row.child(4).text(), row.child(6).text(), null));
                        }
                        resultByChannel.put(currentChannel, games);
                    }
                }

                doc = Jsoup.connect("https://www.futebol365.pt/jogos-na-tv/?order=competition").get();
                Elements competition = doc.getElementsByClass("left-column-group-wrapper").first().children();

                String currentcompetition = null;

                for (Element child : competition) {
                    if (child.tagName().equals("h3")) {
                        currentcompetition = child.text();
                        continue;
                    }
                    if (child.tagName().equals("table")) {
                        ArrayList<Game> games = new ArrayList<>();
                        for (Element row : child.child(1).children()) {
                            games.add(new Game(row.child(0).text(), row.child(1).text(), row.child(3).text(), row.child(5).text(), row.child(7).text()));
                        }
                        resultByCompetition.put(currentcompetition, games);
                    }
                }


            } catch (IOException e) {
                e.printStackTrace();
            }


            return true;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            pb.setProgress(values[0]);
            super.onProgressUpdate(values);
        }

        protected void onPostExecute(Boolean result) {
            if (resultByDate != null && resultByChannel != null && resultByCompetition != null) {

                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                intent.putExtra("gamesPerDay", resultByDate);
                intent.putExtra("orderPerDay", new ArrayList<>(resultByDate.keySet()));
                intent.putExtra("gamesPerChannel", resultByChannel);
                intent.putExtra("orderPerChannel", new ArrayList<>(resultByChannel.keySet()));
                intent.putExtra("gamesPerCompetition", resultByCompetition);
                intent.putExtra("orderPerCompetition", new ArrayList<>(resultByCompetition.keySet()));
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
