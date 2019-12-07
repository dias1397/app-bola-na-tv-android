package com.diasjoao.bolanatv;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new RetrieveGames().execute();
    }

    class RetrieveGames extends AsyncTask<String, Void, LinkedHashMap<String, List<Game>>> {

        protected LinkedHashMap<String, List<Game>> doInBackground(String... urls) {
            LinkedHashMap<String, List<Game>> result = new LinkedHashMap<>();

            try {
                Document doc = Jsoup.connect("https://www.futebol365.pt/jogos-na-tv/?order=date").get();
                Elements days = doc.getElementsByClass("left-column-group-wrapper").first().children();

                String currentDate = null;

                System.out.println(days.size());

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
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
           /* if (result!=null) {
                System.out.println(result.keySet());
                for (String s : result.keySet()) {
                    System.out.println(s);
                    for (Game g : result.get(s)) {
                        System.out.println(g);
                    }
                }
            }*/
            return result;
        }

        protected void onPostExecute(LinkedHashMap<String, List<Game>> result) {
            if (result != null) {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                intent.putExtra("map", result);
                startActivity(intent);
            }
        }
    }
}
