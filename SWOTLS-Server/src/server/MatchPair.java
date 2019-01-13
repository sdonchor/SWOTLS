package server;

public class MatchPair {
    private int competitorIdSideA;
    private int competitorIdSideB;

    public MatchPair(int sideA, int sideB) {
        this.competitorIdSideA = sideA;
        this.competitorIdSideB = sideB;
    }

    public int getCompetitorIdSideA() {
        return competitorIdSideA;
    }

    public int getCompetitorIdSideB() {
        return competitorIdSideB;
    }
}
