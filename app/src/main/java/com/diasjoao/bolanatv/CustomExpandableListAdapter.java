package com.diasjoao.bolanatv;

import java.util.LinkedHashMap;
import java.util.List;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomExpandableListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<String> expandableListTitle;
    private LinkedHashMap<String, List<Game>> expandableListDetail;
    private String layoutType;

    public CustomExpandableListAdapter(Context context, List<String> expandableListTitle, LinkedHashMap<String, List<Game>> expandableListDetail, String layoutType) {
        this.context = context;
        this.expandableListTitle = expandableListTitle;
        this.expandableListDetail = expandableListDetail;
        this.layoutType = layoutType;
    }

    @Override
    public Game getChild(int listPosition, int expandedListPosition) {
        return this.expandableListDetail.get(this.expandableListTitle.get(listPosition))
                .get(expandedListPosition);
    }

    @Override
    public long getChildId(int listPosition, int expandedListPosition) {
        return expandedListPosition;
    }

    @Override
    public View getChildView(int listPosition, final int expandedListPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final String homeTeamString = (String) getChild(listPosition, expandedListPosition).getHomeTeam();
        final String awayTeamString = (String) getChild(listPosition, expandedListPosition).getAwayTeam();
        final String hoursString = (String) getChild(listPosition, expandedListPosition).getHour();

        String dateString = (String) getChild(listPosition, expandedListPosition).getDate();
        String channelString = (String) getChild(listPosition, expandedListPosition).getChannel();

        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            if (layoutType.equals("Date")) {
                dateString = getGroup(listPosition).toString();
                convertView = layoutInflater.inflate(R.layout.list_item, null);
            } else if (layoutType.equals("Competition")){
                convertView = layoutInflater.inflate(R.layout.list_item2, null);

            } else if (layoutType.equals("Channel")) {
                channelString = getGroup(listPosition).toString();
                convertView = layoutInflater.inflate(R.layout.list_item3, null);
            }
        }

        if (layoutType.equals("Date") || layoutType.equals("Competition")) {
            ImageView channel = (ImageView) convertView.findViewById(R.id.imageView);

            switch (channelString) {
                case "RTP 1":
                    channel.setImageResource(R.drawable.rtp1);
                    break;
                case "SIC":
                    channel.setImageResource(R.drawable.sic);
                    break;
                case "TVI":
                    channel.setImageResource(R.drawable.tvi);
                    break;
                case "CANAL 11":
                    channel.setImageResource(R.drawable.canal11);
                    break;
                case "ELEVENSPORTS 1":
                    channel.setImageResource(R.drawable.elevensports1);
                    break;
                case "ELEVENSPORTS 2":
                    channel.setImageResource(R.drawable.elevensports2);
                    break;
                case "ELEVENSPORTS 3":
                    channel.setImageResource(R.drawable.elevensports3);
                    break;
                case "ELEVENSPORTS 4":
                    channel.setImageResource(R.drawable.elevensports4);
                    break;
                case "ELEVENSPORTS 5":
                    channel.setImageResource(R.drawable.elevensports5);
                    break;
                case "ELEVENSPORTS 6":
                    channel.setImageResource(R.drawable.elevensports6);
                    break;
                case "SPORT.TV1":
                    channel.setImageResource(R.drawable.sporttv1);
                    break;
                case "SPORT.TV2":
                    channel.setImageResource(R.drawable.sporttv2);
                    break;
                case "SPORT.TV3":
                    channel.setImageResource(R.drawable.sporttv3);
                    break;
                case "SPORT.TV4":
                    channel.setImageResource(R.drawable.sporttv4);
                    break;
                case "SPORT.TV5":
                    channel.setImageResource(R.drawable.sporttv5);
                    break;
                case "BENFICA TV":
                    channel.setImageResource(R.drawable.benficatv);
                    break;
                case "PORTO CANAL":
                    channel.setImageResource(R.drawable.portocanal);
                    break;
                case "SPORTING TV":
                    channel.setImageResource(R.drawable.sportingtv);
                    break;
                default:
                    channel.setImageResource(R.drawable.ic_launcher_background);
            }
        }

        if (layoutType.equals("Channel") || layoutType.equals("Competition")) {
            TextView date = (TextView) convertView.findViewById(R.id.date);
            date.setText(dateString);
        }

        TextView homeTeam = (TextView) convertView.findViewById(R.id.homeTeam);
        TextView awayTeam = (TextView) convertView.findViewById(R.id.awayTeam);
        TextView hours   = (TextView) convertView.findViewById(R.id.hours);

        homeTeam.setText(homeTeamString);
        awayTeam.setText(awayTeamString);
        hours.setText(hoursString);

        return convertView;
    }

    @Override
    public int getChildrenCount(int listPosition) {
        return this.expandableListDetail.get(this.expandableListTitle.get(listPosition))
                .size();
    }

    @Override
    public Object getGroup(int listPosition) {
        return this.expandableListTitle.get(listPosition);
    }

    @Override
    public int getGroupCount() {
        return this.expandableListTitle.size();
    }

    @Override
    public long getGroupId(int listPosition) {
        return listPosition;
    }

    @Override
    public View getGroupView(int listPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String listTitle = (String) getGroup(listPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.list_group, null);
        }
        TextView listTitleTextView = (TextView) convertView
                .findViewById(R.id.listTitle);
        listTitleTextView.setTypeface(null, Typeface.BOLD);
        listTitleTextView.setText(listTitle);
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int listPosition, int expandedListPosition) {
        return true;
    }
}

