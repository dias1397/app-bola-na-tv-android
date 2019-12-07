package com.diasjoao.bolanatv;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    LinkedHashMap<String, List<Game>> gamesPerDay = null;

    ExpandableListView expandableListView;
    ExpandableListAdapter expandableListAdapter;
    List<String> expandableListTitle;
    LinkedHashMap<String, List<Game>> expandableListDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new RetrieveGames().execute();

        expandableListView = (ExpandableListView) findViewById(R.id.expandableListView);
        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
                Toast.makeText(getApplicationContext(),
                        expandableListTitle.get(groupPosition) + " List Expanded.",
                        Toast.LENGTH_SHORT).show();
            }
        });

        expandableListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {
                Toast.makeText(getApplicationContext(),
                        expandableListTitle.get(groupPosition) + " List Collapsed.",
                        Toast.LENGTH_SHORT).show();

            }
        });

        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                Toast.makeText(
                        getApplicationContext(),
                        expandableListTitle.get(groupPosition)
                                + " -> "
                                + expandableListDetail.get(
                                expandableListTitle.get(groupPosition)).get(
                                childPosition), Toast.LENGTH_SHORT
                ).show();
                return false;
            }
        });
    }

    class RetrieveGames extends AsyncTask<String, Void, LinkedHashMap<String, List<Game>>> {

        protected LinkedHashMap<String, List<Game>> doInBackground(String... urls) {
            LinkedHashMap<String, List<Game>> result = new LinkedHashMap<>();

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

        protected void onPostExecute(LinkedHashMap<String, List<Game>> result) {
            gamesPerDay = result;

            expandableListDetail = gamesPerDay;
            expandableListTitle = new ArrayList<String>(expandableListDetail.keySet());
            expandableListAdapter = new CustomExpandableListAdapter(getBaseContext(), expandableListTitle, expandableListDetail);
            expandableListView.setAdapter(expandableListAdapter);
        }
    }
}
