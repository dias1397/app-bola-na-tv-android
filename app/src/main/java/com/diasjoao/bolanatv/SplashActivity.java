package com.diasjoao.bolanatv;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
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

import okhttp3.OkHttpClient;
import okhttp3.Request;

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

        if (isNetworkConnected()) {
            new RetrieveInitialGames().execute();
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

    class RetrieveInitialGames extends AsyncTask<String, Integer, Boolean> {

        String type;
        LinkedHashMap<String, List<Game>> resultByDate = new LinkedHashMap<>();
        LinkedHashMap<String, List<Game>> resultByCompetition = new LinkedHashMap<>();
        LinkedHashMap<String, List<Game>> resultByChannel = new LinkedHashMap<>();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(SplashActivity.this);
            type = prefs.getString("DefaultType", "Data");
            Log.println(Log.DEBUG, "myApp", type);
        }

        protected Boolean doInBackground(String... urls) {
            try {
                Document doc = null;
                if (type.equals("Data")) {
                    doc = Jsoup.connect("https://www.futebol365.pt/jogos-na-tv/?order=date").get();
                } else if (type.equals("Competição")) {
                    doc = Jsoup.connect("https://www.futebol365.pt/jogos-na-tv/?order=competition").get();
                } else if (type.equals("Canal")) {
                    doc = Jsoup.connect("https://www.futebol365.pt/jogos-na-tv/?order=channel").get();
                }
                Elements date = doc.getElementsByClass("left-column-group-wrapper").first().children();

                String currentTitle = null;

                for (Element child : date) {
                    if (child.tagName().equals("h3")) {
                        currentTitle = child.text();
                    }

                    if (type.equals("Data")) {
                        if (child.tagName().equals("table")) {
                            ArrayList<Game> games = new ArrayList<>();
                            for (Element row : child.child(1).children()) {
                                games.add(new Game(null, row.child(0).text(), row.child(1).text(), row.child(3).text(), row.child(5).text()));
                            }
                            resultByDate.put(currentTitle, games);
                        }
                    } else if (type.equals("Competição")) {
                        if (child.tagName().equals("table")) {
                            ArrayList<Game> games = new ArrayList<>();
                            for (Element row : child.child(1).children()) {
                                games.add(new Game(row.child(0).text(), row.child(1).text(), row.child(3).text(), row.child(5).text(), row.child(7).text()));
                            }
                            resultByCompetition.put(currentTitle, games);
                        }
                    } else if (type.equals("Canal")) {
                        if (child.tagName().equals("table")) {
                            ArrayList<Game> games = new ArrayList<>();
                            for (Element row : child.child(1).children()) {
                                games.add(new Game(row.child(0).text(), row.child(1).text(), row.child(4).text(), row.child(6).text(), null));
                            }
                            resultByChannel.put(currentTitle, games);
                        }
                    }

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }

        protected void onPostExecute(Boolean result) {
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            if (type.equals("Data")) {
                if (resultByDate != null) {
                    intent.putExtra("gamesPerDay", resultByDate);
                    intent.putExtra("orderPerDay", new ArrayList<>(resultByDate.keySet()));
                }
            } else if (type.equals("Competição")) {
                if (resultByCompetition != null) {
                    intent.putExtra("gamesPerCompetition", resultByCompetition);
                    intent.putExtra("orderPerCompetition", new ArrayList<>(resultByCompetition.keySet()));
                }
            } else if (type.equals("Canal")) {
                if (resultByChannel != null) {
                    intent.putExtra("gamesPerChannel", resultByChannel);
                    intent.putExtra("orderPerChannel", new ArrayList<>(resultByChannel.keySet()));
                }
            }

            startActivity(intent);
            finish();
        }
    }
/*
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
                doc = Jsoup.connect("https://www.futebol365.pt/jogos-na-tv/?order=channel").get();
                Elements channel = doc.getElementsByClass("left-column-group-wrapper").first().children();
                doc = Jsoup.connect("https://www.futebol365.pt/jogos-na-tv/?order=competition").get();
                Elements competition = doc.getElementsByClass("left-column-group-wrapper").first().children();

                int count = date.size() + channel.size() + competition.size();

                Log.d("MyApp","Finish Count");

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
                    publishProgress((int) (((status+1) / (float) count) * 100));
                    status++;
                }

                Log.d("MyApp","Finish 1");

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
                    publishProgress((int) (((status+1) / (float) count) * 100));
                    status++;
                }

                Log.d("MyApp","Finish 2");

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
                    publishProgress((int) (((status+1) / (float) count) * 100));
                    status++;
                }

                Log.d("MyApp","Finish 3");

            } catch (Exception e) {
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
    }*/

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }
}
