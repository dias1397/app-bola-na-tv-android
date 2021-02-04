package com.diasjoao.bolanatv.models;

import java.util.Objects;

public class Game {

    private String date;
    private String time;
    private String home;
    private String away;
    private String channel;
    private String competition;

    public Game(String date, String time, String home, String away, String channel, String competition) {
        this.date = date;
        this.time = time;
        this.home = home;
        this.away = away;
        this.channel = channel;
        this.competition = competition;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getHome() {
        return home;
    }

    public void setHome(String home) {
        this.home = home;
    }

    public String getAway() {
        return away;
    }

    public void setAway(String away) {
        this.away = away;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getCompetition() {
        return competition;
    }

    public void setCompetition(String competition) {
        this.competition = competition;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Game game = (Game) o;
        return Objects.equals(date, game.date) &&
                Objects.equals(time, game.time) &&
                Objects.equals(home, game.home) &&
                Objects.equals(away, game.away) &&
                Objects.equals(channel, game.channel) &&
                Objects.equals(competition, game.competition);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, time, home, away, channel, competition);
    }

    @Override
    public String toString() {
        return "Game{" +
                "date='" + date + '\'' +
                ", time='" + time + '\'' +
                ", home='" + home + '\'' +
                ", away='" + away + '\'' +
                ", channel='" + channel + '\'' +
                ", competition='" + competition + '\'' +
                '}';
    }
}
