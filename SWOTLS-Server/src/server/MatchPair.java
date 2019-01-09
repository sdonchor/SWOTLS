package server;

public class MatchPair {
    private TournamentParticipant sideA;
    private TournamentParticipant sideB;

    public MatchPair(TournamentParticipant sideA, TournamentParticipant sideB) {
        this.sideA = sideA;
        this.sideB = sideB;
    }

    public TournamentParticipant getSideA() {
        return sideA;
    }

    public TournamentParticipant getSideB() {
        return sideB;
    }
}
