package server;

import java.util.ArrayList;
import java.util.List;

public class Team extends Competitor {
    private int id;
    private String name;
    private String whereFrom;
    private Player leader;
    private List<Player> players = new ArrayList<>();

    public Team(int id, String name, String whereFrom, Player leader) {
        this.id = id;
        this.name = name;
        this.whereFrom = whereFrom;
        this.leader = leader;
    }

    @Override
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

    public List<Player> getPlayers() {
        return players;
    }

    @Override
    public String displayedName() {
        return name;
    }
}
