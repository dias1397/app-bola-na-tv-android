package com.diasjoao.bolanatv;

public class Game {

    private String hour;
    private String homeTeam;
    private String awayTeam;
    private String channel;

    public Game(String hour, String homeTeam, String awayTeam, String channel) {
        this.hour = hour;
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.channel = channel;
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

    public String getChannel() {
        return channel;
    }

    @Override
    public String toString() {
        return "Game{" +
                "hour='" + hour + '\'' +
                ", homeTeam='" + homeTeam + '\'' +
                ", awayTeam='" + awayTeam + '\'' +
                ", channel='" + channel + '\'' +
                '}';
    }
}
