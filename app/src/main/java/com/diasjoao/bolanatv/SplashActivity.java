package com.diasjoao.bolanatv;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
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

public class SplashActivity extends AppCompatActivity {

    ProgressBar progressBar;
    Button repetir;
    TextView carregando;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        carregando = findViewById(R.id.carregando);
        progressBar = findViewById(R.id.progressBar);
        repetir = findViewById(R.id.repetir);
        repetir.setVisibility(View.GONE);

        if (isNetworkConnected()) {
            new RetrieveGames().execute();
        } else {
            progressBar.setVisibility(View.GONE);
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

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    class RetrieveGames extends AsyncTask<String, Integer, Boolean> {

        ArrayList<Game> games = new ArrayList<>();

        @Override
        protected Boolean doInBackground(String... strings) {
            try {
                Document doc = Jsoup.connect("https://www.futebol365.pt/jogos-na-tv/?order=competition").get();
                Elements date = doc.getElementsByClass("left-column-group-wrapper").first().children();

                String competition = null;

                for (Element child : date) {
                    if (child.tagName().equals("h3")) {
                        competition = child.text();
                    }

                    if (child.tagName().equals("table")) {
                        for (Element row : child.child(1).children()) {
                            games.add(new Game(row.child(0).text(), row.child(1).text(), row.child(3).text(), row.child(5).text(), row.child(7).text(), competition ));
                        }
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            intent.putExtra("games", games);
            finish();
            startActivity(intent);

        }
    }
}
