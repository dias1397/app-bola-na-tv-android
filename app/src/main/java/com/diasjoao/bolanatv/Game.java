package com.diasjoao.bolanatv;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Game implements java.io.Serializable, Comparable<Game>{

    private String date;
    private String hour;
    private String homeTeam;
    private String awayTeam;
    private String channel;
    private String competition;

    private static SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yy");

    public Game(String date, String hour, String homeTeam, String awayTeam, String channel, String competition) {
        this.date = date;
        this.hour = hour;
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.channel = channel;
        this.competition = competition;
    }

    public String getDate() {
        return date;
    }

    public Date getFullDate() throws ParseException {
        return formatter.parse(date);
    }

    public String getHour() {
        return hour;
    }

    public String getHomeTeam() {
        return homeTeam;
    }

    public String getAwayTeam() {
        return awayTeam;
    }

    public String getTeams() {
        return homeTeam + " - " + awayTeam;
    }

    public String getChannel() {
        return channel;
    }

    public String getCompetition() {
        return competition;
    }

    @Override
    public String toString() {
        return "Game{" +
                "date='" + date + '\'' +
                ", hour='" + hour + '\'' +
                ", homeTeam='" + homeTeam + '\'' +
                ", awayTeam='" + awayTeam + '\'' +
                ", channel='" + channel + '\'' +
                '}';
    }

    @Override
    public int compareTo(Game g) {
        if (getHour() == null || g.getHour() == null) {
            return 0;
        }
        return getHour().compareTo(g.getHour());
    }
}
