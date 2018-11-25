package application;

import java.util.Date;

public class Match {
    private int id;
    private Competitor sideA;
    private Competitor sideB;
    private Float scoreA;
    private Float scoreB;
    private Competition competition;
    private Date date;
    private Arena arena;

    public Match(int id, Competitor sideA, Competitor sideB, Competition competition, Date date, Arena arena) {
        this.id = id;
        this.sideA = sideA;
        this.sideB = sideB;
        this.competition = competition;
        this.date = date;
        this.arena = arena;
    }

    public Match(int id, Competitor sideA, Competitor sideB, Float scoreA, Float scoreB, Competition competition, Date date, Arena arena) {
        this.id = id;
        this.sideA = sideA;
        this.sideB = sideB;
        this.scoreA = scoreA;
        this.scoreB = scoreB;
        this.competition = competition;
        this.date = date;
        this.arena = arena;
    }

    public int getId() {
        return id;
    }

    public Competitor getSideA() {
        return sideA;
    }

    public Competitor getSideB() {
        return sideB;
    }

    public Float getScoreA() {
        return scoreA;
    }

    public Float getScoreB() {
        return scoreB;
    }

    public Competition getCompetition() {
        return competition;
    }

    public Date getDate() {
        return date;
    }

    public Arena getArena() {
        return arena;
    }

    public void setScoreA(Float scoreA) {
        this.scoreA = scoreA;
    }

    public void setScoreB(Float scoreB) {
        this.scoreB = scoreB;
    }

    @Override
    public String toString() {
        return sideA +
                " vs " + sideB +
                " - " + date;
    }
}
