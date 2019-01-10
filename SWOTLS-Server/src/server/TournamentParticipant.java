package server;

public class TournamentParticipant {
    private int id; //Identyfikuje id zawodnika lub drużyny w bazie danych
    private int startingPosition;
    private int leagueClass;
    private int points;
    //public enum Type { PERSON, TEAM }
    //private Type type;

    public TournamentParticipant(int id) {
        this.id = id;
    }

    public TournamentParticipant(int id, int leagueClass) {
        this.id = id;
        this.leagueClass = leagueClass;
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

    public void setStartingPosition(int startingPosition) {
        this.startingPosition = startingPosition;
    }

    public int getLeagueClass() {
        return leagueClass;
    }

    public void setLeagueClass(int leagueClass) {
        this.leagueClass = leagueClass;
    }
}
