package com.diasjoao.bolanatv;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
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

public class ChannelFragment extends Fragment {

    ExpandableListView expandableListView;
    ExpandableListAdapter expandableListAdapter;

    List<String> expandableListTitle;
    Map<String, List<Game>> expandableListDetail;

    private int lastExpandedPosition = -1;

    public ChannelFragment(Map<String, List<Game>> expandableListDetail) {
        this.expandableListDetail = expandableListDetail;
        this.expandableListTitle = new ArrayList<String>(expandableListDetail.keySet());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_date, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        expandableListView = (ExpandableListView) getView().findViewById(R.id.expandableListView);

        expandableListAdapter = new CustomExpandableListAdapter(getActivity(), expandableListTitle, expandableListDetail, "Channel");
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

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        if (prefs.getBoolean("AutoExpand", true)) {
            expandableListView.expandGroup(0);
        }

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

                Long startTime = beginTime.getTimeInMillis() +
                        (Integer.parseInt(game.getHour().split(":")[0]) * 3600000) +
                        (Integer.parseInt(game.getHour().split(":")[1]) * 60000);

                Intent intent = new Intent(Intent.ACTION_INSERT);
                intent.setType("vnd.android.cursor.item/event");
                intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startTime);
                intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, startTime + (105*60*1000));
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
