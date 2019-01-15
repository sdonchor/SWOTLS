package application;

import java.util.Date;

public class Match {
    private int id;
    private Competitor sideA;
    private Competitor sideB;
    private int scoreA;
    private int scoreB;
    private Competition competition;
    private Date date;
    private Arena arena;

    public Match(int id, Competitor sideA, Competitor sideB, Competition competition, Arena arena) {
        this.id = id;
        this.sideA = sideA;
        this.sideB = sideB;
        this.competition = competition;
        this.date = date;
        this.arena = arena;
    }

    public Match(int id, Competitor sideA, Competitor sideB, Competition competition, Date date, Arena arena) {
        this.id = id;
        this.sideA = sideA;
        this.sideB = sideB;
        this.competition = competition;
        this.date = date;
        this.arena = arena;
    }

    public Match(int id, Competitor sideA, Competitor sideB, int scoreA, int scoreB, Competition competition, Date date, Arena arena) {
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

    public int getScoreA() {
        return scoreA;
    }

    public int getScoreB() {
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

    public void setScoreA(int scoreA) {
        this.scoreA = scoreA;
    }

    public void setScoreB(int scoreB) {
        this.scoreB = scoreB;
    }

    @Override
    public String toString() {
    	if(sideA==null || sideB==null) return "Mecz " + id;
        return sideA.displayedName() +
                " vs " + sideB.displayedName() +
                " - " + date;
    }
}
