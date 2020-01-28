package com.diasjoao.bolanatv;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.CalendarContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.Toast;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class CompetitionFragment extends Fragment {

    ExpandableListView expandableListView;
    ExpandableListAdapter expandableListAdapter;

    List<String> expandableListTitle;
    Map<String, List<Game>> expandableListDetail;

    private int lastExpandedPosition = -1;

    public CompetitionFragment(Map<String, List<Game>> expandableListDetail) {
        this.expandableListDetail = expandableListDetail;
        this.expandableListTitle = new ArrayList<String>(expandableListDetail.keySet());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_competition, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        expandableListView = (ExpandableListView) getView().findViewById(R.id.expandableListView);

        expandableListAdapter = new CustomExpandableListAdapter(getActivity(), expandableListTitle, expandableListDetail, "Competition");
        expandableListView.setAdapter(expandableListAdapter);

        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
                if (lastExpandedPosition != -1 && groupPosition != lastExpandedPosition) {
                    expandableListView.collapseGroup(lastExpandedPosition);
                }
                lastExpandedPosition = groupPosition;
            }
        });
        expandableListView.expandGroup(0);

        expandableListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {

            }
        });

        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                Game game = expandableListDetail.get(expandableListTitle.get(groupPosition)).get(childPosition);

                Calendar beginTime = Calendar.getInstance();
                try {
                    beginTime.setTime(game.getFullDate());
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                Intent intent = new Intent(Intent.ACTION_EDIT);
                intent.setType("vnd.android.cursor.item/event");
                intent.putExtra(CalendarContract.Events.DTSTART, beginTime.getTimeInMillis());
                intent.putExtra(CalendarContract.Events.DTEND, beginTime.getTimeInMillis() + (105*60*1000));
                intent.putExtra(CalendarContract.Events.ALL_DAY, false);
                intent.putExtra(CalendarContract.Events.TITLE, game.getHomeTeam() + " vs " + game.getAwayTeam() + "( " + game.getChannel() + ")");
                intent.putExtra(CalendarContract.Events.DESCRIPTION, "Jogo a contar para " + game.getCompetition());
                startActivity(intent);

                Toast.makeText(getActivity().getApplicationContext(), "Jogo a contar para " + expandableListDetail.get(expandableListTitle.get(groupPosition)).get(childPosition).getCompetition(), Toast.LENGTH_SHORT).show();
                return false;
            }
        });
    }
}
