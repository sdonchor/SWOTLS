package application;

public abstract class Competitor {
    private int leagueClass;

    public abstract String displayedName();

    public int getLeagueClass() {
        return leagueClass;
    }

    public void setLeagueClass(int leagueClass) {
        this.leagueClass = leagueClass;
    }
}
