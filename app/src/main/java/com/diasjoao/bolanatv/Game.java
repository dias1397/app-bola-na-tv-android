package com.diasjoao.bolanatv;

public class Game implements java.io.Serializable{

    private String date;
    private String hour;
    private String homeTeam;
    private String awayTeam;
    private String channel;

    public Game(String date, String hour, String homeTeam, String awayTeam, String channel) {
        this.date = date;
        this.hour = hour;
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.channel = channel;
    }

    public String getDate() {
        return date;
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
}
