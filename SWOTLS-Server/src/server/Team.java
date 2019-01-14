package server;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Team extends Competitor {
    private int id;
    private String name;
    private String whereFrom;
    private Player leader;
    private List<Player> players = new ArrayList<>();
    private static DatabaseHandler dbH = null;
    public static void setDbh(DatabaseHandler db) {
    	dbH = db;
    }
    public Team(int id, String name, String whereFrom, Player leader, List<Player> members) {
        this.id = id;
        this.name = name;
        this.whereFrom = whereFrom;
        this.leader = leader;
        
        
        try {
			this.players = dbH.getQueryBuilder().getTeamPlayers(id);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    @Override
    public String displayedName() {
        return name;
    }
}
