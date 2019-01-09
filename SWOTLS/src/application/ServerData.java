package application;

import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.effect.PerspectiveTransform;

import javax.sql.rowset.CachedRowSet;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ServerData {
    /**
     * Pobiera listę wszystkich zawodników federacji.
     * @return Map z kluczami oznaczającymi nazwę zawodnika i wartościami oznaczającymi jego id.
     */
	
	public static ArrayList<Player> contestants = new ArrayList<Player>();
	public static ArrayList<Competition> tournaments = new ArrayList<Competition>();
	public static ArrayList<Team> teams = new ArrayList<Team>();
	public static ArrayList<Match> matches = new ArrayList<Match>();
	public static ArrayList<Arena> arenas = new ArrayList<Arena>();
	public static ArrayList<User> sys_users = new ArrayList<User>();
	private static ServerConnection sc;
	private static String currentUser;
	private static Permission perm;

	public static void initializeServerConnection() {
		try {
			sc = new ServerConnection("localhost",4545);
			ServerData.convertContestants(sc.getTable("contestants"));
			ServerData.convertTournaments(sc.getTable("tournaments"));
			ServerData.convertTeams(sc.getTable("teams"));
			ServerData.convertMatches(sc.getTable("matches"));
			ServerData.convertArenas(sc.getTable("arenas"));
			ServerData.convertSysUsrs(sc.getTable("system_users"));
			sc.socketClose();
			
		} catch (IOException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void convertContestants(CachedRowSet crs) {
		try {
			while(crs.next()) {
				int cid = crs.getInt("contestant_id");
				String name = crs.getString("name");
				String surname = crs.getString("surname");
				String nickname = crs.getString("nickname");
				int score = crs.getInt("score");
				String language = crs.getString("language");
				String contact_info = crs.getString("contact_info");
				String additional_info = crs.getString("additional_info");
				int team_id = crs.getInt("team_id");
				Team team = null;
				if(!crs.wasNull())
				{
					team = ServerData.getTeamById(team_id);
				}
				Player p = new Player(cid, name, surname, nickname, score, language, contact_info, additional_info, team);
				contestants.add(p);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void convertTeams(CachedRowSet crs) {
		try {
			while(crs.next()) {
				int tid = crs.getInt("team_id");
				String name = crs.getString("name");
				String where_from = crs.getString("where_from");
				int leader_id = crs.getInt("leader_id");
				Player l = null;
				if(!crs.wasNull())
				{
					l = ServerData.getContestantById(leader_id);
				}
				Team t = new Team(tid,name,where_from,l);
				
				teams.add(t);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void convertTournaments(CachedRowSet crs) {
		try {
			while(crs.next()) {
				int tid = crs.getInt("tournament_id");
				String name = crs.getString("name");
				String type = crs.getString("type");
				Competition.Type t;
				if(type.equals("solo"))
				{
					t=Competition.Type.SOLO;
				}
				else
				{
					t= Competition.Type.TEAM;
				}
				int operator = crs.getInt("operator");
				User u = null;
				if(!crs.wasNull())
				{
					u = ServerData.getUserById(operator);
				}
				String additional_info = crs.getString("additional_info");
				Competition c = new Competition(tid,name,t,additional_info,null);
				tournaments.add(c);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void convertArenas(CachedRowSet crs) {
		try {
			while(crs.next()) {
				int aid = crs.getInt("arena_id");
				String name = crs.getString("name");
				String location = crs.getString("location");
				Arena a = new Arena(aid,location,name);
				arenas.add(a);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void convertMatches(CachedRowSet crs) {
		try {
			while(crs.next()) {
				int mid = crs.getInt("match_id");
				int sideAid = crs.getInt("sideA");
				int sideBid = crs.getInt("sideB");
				int sideAscore = crs.getInt("sideA_score");
				int sideBscore = crs.getInt("sideB_score");
				int arenaId = crs.getInt("arena_id");
				Date time = crs.getDate("time"); //??
				int tournamentId = crs.getInt("tournament");
				
				Team a = ServerData.getTeamById(sideAid);
				Team b = ServerData.getTeamById(sideBid);
				Competition t = ServerData.getTournamentById(tournamentId);
				Arena arena = ServerData.getArenaById(arenaId);

				Match m = new Match(mid,a,b,sideAscore,sideBscore,t,time,arena);
				matches.add(m);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void convertSysUsrs(CachedRowSet crs) {
		try {
			while(crs.next()) {
				int uid = crs.getInt("sys_usr_id");
				String login = crs.getString("login");
				String pw_hash = crs.getString("pw_hash");
				String permissions = crs.getString("permissions");
				User u = new User(uid,login,permissions);
				sys_users.add(u);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
    public static Map<String, Integer> getListOfAllContestants(){
        Map<String, Integer> map = new HashMap<String, Integer>();
        for(int i = 0; i<contestants.size();i++)
        {
        	map.put(contestants.get(i).displayedName(), contestants.get(i).getId());
        }
        return map;
    }

    public static Player getContestantById(int id){
    	for(int i = 0;i<contestants.size();i++)
    	{
    		if(contestants.get(i).getId()==id)
    		{
    			return contestants.get(i);
    		}
    	}
    	return null;
    }
    
    public static Map<String, Integer> getListOfAllTeams(){
    	Map<String, Integer> map = new HashMap<String, Integer>();
        for(int i = 0; i<teams.size();i++)
        {
        	map.put(teams.get(i).getName(), teams.get(i).getId());
        }
        return map;
    }

    public static Team getTeamById(int id){
    	for(int i = 0;i<teams.size();i++)
    	{
    		if(teams.get(i).getId()==id)
    		{
    			return teams.get(i);
    		}
    	}
    	return null;
    }

    public static Map<String, Integer> getListOfAllTournaments(){
    	Map<String, Integer> map = new HashMap<String, Integer>();
        for(int i = 0; i<tournaments.size();i++)
        {
        	map.put(tournaments.get(i).getName(), tournaments.get(i).getId());
        }
        return map;
    }

    public static Competition getTournamentById(int id){
    	for(int i = 0;i<tournaments.size();i++)
    	{
    		if(tournaments.get(i).getId()==id)
    		{
    			return tournaments.get(i);
    		}
    	}
    	return null;
    }

    public static Map<String, Integer> getListOfAllMatches(){
        Map<String, Integer> map = new HashMap<String, Integer>();
       // map.put("2Pesteczka vs Unity Female - 25.11.2018 11:00", 0);
        for(int i = 0; i<matches.size();i++)
        {
        	map.put(matches.get(i).toString(), matches.get(i).getId());
        }
        return map;
    }

    public static Match getMatchById(int id){
    	for(int i = 0;i<matches.size();i++)
    	{
    		if(matches.get(i).getId()==id)
    		{
    			return matches.get(i);
    		}
    	}
    	return null;
    }

    public static Map<String, Integer> getListOfAllArenas(){
        Map<String, Integer> map = new HashMap<String, Integer>();
        for(int i = 0; i<arenas.size();i++)
        {
        	map.put(arenas.get(i).getName(), arenas.get(i).getId());
        }
        return map;
    }

    public static Arena getArenaById(int id){
    	for(int i = 0;i<arenas.size();i++)
    	{
    		if(arenas.get(i).getId()==id)
    		{
    			return arenas.get(i);
    		}
    	}
    	return null;
    }

    public static Map<String, Integer> getListOfAllUsers(){
        Map<String, Integer> map = new HashMap<String, Integer>();
        for(int i = 0; i<sys_users.size();i++)
        {
        	map.put(sys_users.get(i).getLogin(), sys_users.get(i).getId());
        }
        return map;
    }

    public static User getUserById(int id){
    	for(int i = 0;i<sys_users.size();i++)
    	{
    		if(sys_users.get(i).getId()==id)
    		{
    			return sys_users.get(i);
    		}
    	}
    	return null;
    }

	/**
	 * Ta funkcja jest wywoływana gdy klient dodaje nowy obiekt lub edytuje istniejący. Oczekiwany efekt to zapisane tego w bazie danych.
	 * @param data Lista wpisów. Wpis posiada atrybut (identyfikuje kolumnę w bazie) i wartość.
	 * @param type Określa typ danych - czy jest to Arena, Competition, Competitor, Match, Player, Team, User. (identyfikuje encję w bazie)
	 */
	public static void saveEntry(ObservableList<Entry> data, String type){
		System.out.println(type);
		for(Entry e : data){
			System.out.println(e.getAttribute() + " " + e.getValue());
		}

		//TODO wysyłanie do serwera
		Dialogs.error("Niezaimplementowana funkcja.");
		ServertriggeredEvents.dataUpdated(); //TODO dataUpdated() docelowo będzie wywoływane przez serwer
	}

	/**
	 * Ta funkcja jest wywoływane gdy klient usuwa obiekt. Oczekiwany efekt to usunięcie odpowiedniego wpisu w bazie danych.
	 * @param id Id wpisu (wiersza, obiektu) do usunięcia.
	 * @param type Określa typ danych - czy jest to Arena, Competition, Competitor, Match, Player, Team, User. (identyfikuje encję w bazie)
	 */
	public static void deleteEntry(int id, String type){
		
		sc.entryRemoval(id,type);
		ServertriggeredEvents.dataUpdated(); //TODO dataUpdated() docelowo będzie wywoływane przez serwer
	}

	public static void logIn(String id, String pw){
		if(sc.verifyLogin(id,pw))
		{
			perm = sc.getCurrentUserPerms();
			currentUser = sc.getCurrentUserName();
		}
		ServertriggeredEvents.permissionsChanged(Permission.FULL); //TODO docelowo to ma być wywoływane przez serwer z odpowiednimi dla zalogowanego konta uprawnieniami
	}

	public static void register(String id, String pw, Permission perm){
		if(sc.getCurrentUserPerms()==Permission.FULL)
		{
			if(!sc.createNewUser(id,pw,perm))
				Dialogs.error("Nie udało się utworzyć użytkownika ", id);
		}
		else
		{
			Dialogs.error("Brak uprawnień");
		}
		ServertriggeredEvents.dataUpdated(); //TODO dataUpdated() docelowo będzie wywoływane przez serwer
	}

	public static void newTournament(String name, String system, String type, String additional){
		if(sc.getCurrentUserPerms()==Permission.ORGANIZER || sc.getCurrentUserPerms()==Permission.FULL)
		{
			if(!sc.createNewTournament(name,system,type,additional))
			{
				Dialogs.error("Nie udało się utworzyć turnieju");
			}
		}
		else
			Dialogs.error("Brak uprawnień");
		ServertriggeredEvents.dataUpdated(); //TODO dataUpdated() docelowo będzie wywoływane przez serwer
	}

	public static Map<String, Integer> getListOfCompetitionContestants(int competitionId){
		//TODO teraz pobiera wszystkich zawodników, a powinno tylko zapisanych do podanego turnieju
		Map<String, Integer> map = new HashMap<String, Integer>();
		for(int i = 2; i<contestants.size();i++)
		{
			map.put(contestants.get(i).displayedName(), contestants.get(i).getId());
		}
		return map;
	}

	public static Map<String, Integer> getListOfUnplannedMatches(int competitionId){
		//TODO powinno pobierać (tylko) niezaplanowane mecze w podanym turnieju
		Map<String, Integer> map = new HashMap<String, Integer>();
		map.put("2Pesteczka vs Unity Female", 0);
		return map;
	}

	public static Map<String, Integer> getListOfPlannedMatches(int competitionId){
		//TODO powinno pobierać (tylko) zaplanowane mecze w podanym turnieju
		Map<String, Integer> map = new HashMap<String, Integer>();
		map.put("Smoki vs Wilki 02.02.2019 19:00", 0);
		return map;
	}

	public static Map<String, Integer> getListOfFinishedMatches(int competitionId){
		//TODO powinno pobierać (tylko) zakończone mecze w podanym turnieju
		Map<String, Integer> map = new HashMap<String, Integer>();
		map.put("PK vs AGH (2:0)", 0);
		return map;
	}

	public static void addCompetitorToCompetition(int competitorId, int competitionId){
		//TODO powinno zapisywać zawodnika lub drużynę (w zależnościu od typu podanego turnieju) do wydarzenia
		ServertriggeredEvents.dataUpdated(); //TODO dataUpdated() docelowo będzie wywoływane przez serwer
	}

	/**
	 * Przechodzi do następnego etapu podanego turnieju.
	 * @param competitionId Id turnieju, w którym ma nastąpić przejście do następnego etapu.
	 */
	public static void nextStage(int competitionId){
		//TODO serwer powinien sprawdzić czy można przejść (czy wyniki wszystkich meczy zostały wprowadzone) - jeżeli tak, to ma wygenerować mecze dla następnego etapu lub ogłosić zwycięzcę (lub dokonać awansów i spadków w przypadku ligi) jeżeli to był ostatni etap
		ServertriggeredEvents.error("Nie można przejść do następnego etapu przed zakończeniem aktualnego! Wprowadź wyniki wszystkich meczy."); //TODO taki lub podobny komunikat ma wywoływać serwer jeżeli nie można przejść
		ServertriggeredEvents.dataUpdated(); //TODO dataUpdated() docelowo będzie wywoływane przez serwer - po przejściu do następnego etapu
	}
}
