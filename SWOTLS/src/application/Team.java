package application;

public class Team extends Competitor {
    private int id;
    private String name;
    private String whereFrom;
    private Player leader;

    public Team(int id, String name, String whereFrom, Player leader) {
        this.id = id;
        this.name = name;
        this.whereFrom = whereFrom;
        this.leader = leader;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getWhereFrom() {
        return whereFrom;
    }

    public Player getLeader() {
        return leader;
    }

    @Override
    public String displayedName() {
        return name;
    }
}
