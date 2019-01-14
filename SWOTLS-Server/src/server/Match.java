package server;

import java.util.Date;

public class Match {
    private int id;
    private Competitor sideA;
    private Competitor sideB;
    private int scoreA;
    private int scoreB;
    private int competitionId;
    private Date date;

    public Match(int id, Competitor sideA, Competitor sideB, int competitionId) {
        this.id = id;
        this.sideA = sideA;
        this.sideB = sideB;
        this.competitionId = competitionId;
        this.date = date;
    }

    public Match(int id, Competitor sideA, Competitor sideB, int competitionId, Date date) {
        this.id = id;
        this.sideA = sideA;
        this.sideB = sideB;
        this.competitionId = competitionId;
        this.date = date;
    }

    public Match(int id, Competitor sideA, Competitor sideB, int scoreA, int scoreB, int competitionId, Date date) {
        this.id = id;
        this.sideA = sideA;
        this.sideB = sideB;
        this.scoreA = scoreA;
        this.scoreB = scoreB;
        this.competitionId = competitionId;
        this.date = date;
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

    public int getCompetitionId() {
        return competitionId;
    }

    public Date getDate() {
        return date;
    }

    public void setScoreA(int scoreA) {
        this.scoreA = scoreA;
    }

    public void setScoreB(int scoreB) {
        this.scoreB = scoreB;
    }

    @Override
    public String toString() {
        return sideA.displayedName() +
                " vs " + sideB.displayedName() +
                " - " + date;
    }
}
