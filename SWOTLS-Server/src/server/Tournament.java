package server;



import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.rowset.CachedRowSet;

public class Tournament {
	public static DatabaseHandler dbH = null;
	public static void setDbh(DatabaseHandler d) {
		dbH=d;
	}
    public static String getDisplayedNameOfPlayer(int playerId){
        try {
			return dbH.getQueryBuilder().getContestantName(playerId);
		} catch (SQLException e) {
			return null;
		}
    }

    public static String getNameOfTeam(int teamId){
        try {
			return dbH.getQueryBuilder().getTeamName(teamId);
		} catch (SQLException e) {
			return null;
		}
    }

    /**
     * @param tournamentId id turnieju do sprawdzenia
     * @return true jeżeli typ turnieju to solo, false jeżeli typ turnieju to drużynowy
     */
    public static boolean isSoloType(int tournamentId){
        try {
			if(dbH.getQueryBuilder().getTournamentType(tournamentId).equals("solo"))
				return true;
			else
				return false;
		} catch (SQLException e) {
			return false;
		}
    }

    public static server.Match getMatchById(int matchId){

        try {
			return dbH.getQueryBuilder().getMatchById(matchId);
		} catch (SQLException e) {
			e.printStackTrace();
			  return null;
		}
      
    }

    public static void saveElo(Player p){
        int id = p.getId();
        int elo = p.getElo();
        try {
			dbH.getQueryBuilder().setElo(id, elo);
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
    }

    /**
     * Ustawia punkty uczestnikowi (zawodnikowi albo drużynie) punkty w turnieju.
     * @param tournamentId Id turnieju
     * @param participantId Id uczestnika (zawodnika albo drużyny)
     * @param score Punkty, które ustawić uczestnikowi
     */
    public static void setPoints(int tournamentId, int participantId, int score){
    	try {
	    	String type = dbH.getQueryBuilder().getTournamentType(tournamentId);
	        if(type.equals("solo"))
	        {
	        	dbH.getQueryBuilder().setPlayerPoints(participantId, tournamentId, score);
	        }
	        else if(type.equals("team"))
	        {
	        	dbH.getQueryBuilder().setTeamPoints(participantId, tournamentId, score);
	        }
    	}
    	catch(SQLException e) {
    		
    	}
    }

    //add 2 points
    public static void addPoint(int tournamentId, int participantId){
    	try {
	    	String type = dbH.getQueryBuilder().getTournamentType(tournamentId);
	        if(type.equals("solo"))
	        {
	        	int score = dbH.getQueryBuilder().getPlayerPoints(participantId, tournamentId);
	        	dbH.getQueryBuilder().setPlayerPoints(participantId, tournamentId, score+2);
	        }
	        else if(type.equals("team"))
	        {
	        	int score = dbH.getQueryBuilder().getPlayerPoints(participantId, tournamentId);
	        	dbH.getQueryBuilder().setTeamPoints(participantId, tournamentId, score+2);
	        }
    	}
    	catch(SQLException e) {
    		
    	}
    }
    //add 1 point
    public static void addHalfPoint(int tournamentId, int participantId){
    	try {
	    	String type = dbH.getQueryBuilder().getTournamentType(tournamentId);
	        if(type.equals("solo"))
	        {
	        	int score = dbH.getQueryBuilder().getPlayerPoints(participantId, tournamentId);
	        	dbH.getQueryBuilder().setPlayerPoints(participantId, tournamentId, score+1);
	        }
	        else if(type.equals("team"))
	        {
	        	int score = dbH.getQueryBuilder().getPlayerPoints(participantId, tournamentId);
	        	dbH.getQueryBuilder().setTeamPoints(participantId, tournamentId, score+1);
	        }
    	}
    	catch(SQLException e) {
    		
    	}
    }

    public static ArrayList<TournamentParticipant> getTournamentParticipants(int tournamentId){
    	ArrayList<TournamentParticipant> participants = new ArrayList<>();
    	try {
			CachedRowSet crs = dbH.getQueryBuilder().getCompetitors(tournamentId);
			String type = dbH.getQueryBuilder().getTournamentType(tournamentId);
			int cid=-1;
			int startingPosition=-1;
			int points = -1;
			int leagueClass=-1;
			
			if(type.equals("solo")) {
				while(crs.next()) {
					cid = crs.getInt("contestant_id");
					startingPosition = crs.getInt("starting_position");
					points = crs.getInt("score");
					leagueClass = crs.getInt("league");
					participants.add(new TournamentParticipant(cid, startingPosition, points, leagueClass));
				}
			}
			else if(type.equals("team")) {
				while(crs.next()) {
					cid = crs.getInt("team_id");
					startingPosition = crs.getInt("starting_position");
					points = crs.getInt("score");
					leagueClass = crs.getInt("league");
					participants.add(new TournamentParticipant(cid, startingPosition, points, leagueClass));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
    	
     
        return participants;
    }

    public static void setTournamentStage(int tournamentId, int stage){
        try {
			dbH.getQueryBuilder().setStage(tournamentId, stage);
		} catch (SQLException e) {
		
			e.printStackTrace();
		}
    }

    public static int getTournamentStage(int tournamentId){
        int stage = -1;
        try {
			stage = dbH.getQueryBuilder().getStage(tournamentId);
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
        return stage;
    }

    public static void setTournamentSeason(int tournamentId, int season){
    	try {
			dbH.getQueryBuilder().setSeason(tournamentId, season);
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
    }

    public static int getTournamentSeason(int tournamentId){
    	 int season = -1;
         try {
 			season = dbH.getQueryBuilder().getSeason(tournamentId);
 		} catch (SQLException e) {
 			
 			e.printStackTrace();
 		}
         return season;
     }
    

    public static void createMatches(int tournamentId, List<MatchPair> matchPairs){
    	try {
	    	for(int i = 0 ; i < matchPairs.size() ; i++)
	    	{
	    		MatchPair currentPair = matchPairs.get(i);
	    		int sideA = currentPair.getCompetitorIdSideA();
	    		int sideB = currentPair.getCompetitorIdSideB();
	    		if(sideA==-1 || sideB==-1) {
	    			continue;
	    		}
	    		else
	    		{
	    			dbH.getQueryBuilder().addMatch(tournamentId, sideA, sideB);
	    		}
	    		
	    	}
    	}catch(SQLException e) {
    		e.printStackTrace();
    	}
    }

    public static void createReport(int tid, String title, String content){
    	try {
			dbH.getQueryBuilder().createReport(tid, title, content);
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
    }

    public static void updateRating(int tournamentId, Competitor competitorX, Competitor competitorY, float didXwin){
        boolean isSoloType = isSoloType(tournamentId); //sprawdzenie typu turnieju (solo czy drużynowy)
        if(isSoloType){
            //Solo
            Player pX = (Player) competitorX;
            Player pY = (Player) competitorY;
            EloRatingSystem.updateRating(pX, pY, didXwin);
            saveElo(pX);
            saveElo(pY);
        }else{
            //Team
            Team tX = (Team) competitorX;
            Team tY = (Team) competitorY;
            EloRatingSystem.updateRating(tX, tY, didXwin);
            for(Player p : tX.getPlayers()){
                saveElo(p);
            }
            for(Player p : tY.getPlayers()){
                saveElo(p);
            }
        }
    }


}
