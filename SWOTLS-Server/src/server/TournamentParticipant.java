package server;

public class TournamentParticipant {
    private int id; //Identyfikuje id zawodnika lub drużyny w bazie danych
    private int startingPosition;
    private int points;
    //public enum Type { PERSON, TEAM }
    //private Type type;

    public TournamentParticipant(int id) {
        this.id = id;
    }

    public TournamentParticipant(int id, int startingPosition) {
        this.id = id;
        this.startingPosition = startingPosition;
    }

    public TournamentParticipant(int id, int startingPosition, int points) {
        this.id = id;
        this.startingPosition = startingPosition;
        this.points = points;
    }

    public int getId() {
        return id;
    }

    public int getStartingPosition() {
        return startingPosition;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }
}
