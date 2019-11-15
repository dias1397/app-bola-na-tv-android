package com.diasjoao.bolanatv;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    Map<String, ArrayList<Game>> gamesPerDay = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new RetrieveGames().execute();
    }

    class RetrieveGames extends AsyncTask<String, Void, Map<String, ArrayList<Game>>> {

        protected Map<String, ArrayList<Game>> doInBackground(String... urls) {
            Map<String, ArrayList<Game>> result = new HashMap<>();

            try {
                Document doc = Jsoup.connect("https://www.futebol365.pt/jogos-na-tv/?order=date").get();
                Elements days = doc.getElementsByClass("left-column-group-wrapper").first().children();

                String currentDate = null;

                for (Element child : days) {
                    if (child.tagName().equals("h3")) {
                        System.out.println(child.text());
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
            if (result!=null) {
                System.out.println(result.keySet());
                for (String s : result.keySet()) {
                    System.out.println(s);
                    for (Game g : result.get(s)) {
                        System.out.println(g);
                    }
                }
            }
            return result;
        }

        protected void onPostExecute(Map<String, ArrayList<Game>> result) {
            gamesPerDay = result;


        }
    }
}
