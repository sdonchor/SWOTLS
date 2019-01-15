package application;

import javax.sql.rowset.CachedRowSet;
import java.io.IOException;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
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
	private static User currentUser=null;

    public static ServerConnection getServerConnection() {
        return sc;
    }

    public static Permission getCurrentUserPerms() {
		if(currentUser!=null) {
			return Permission.valueOf(currentUser.getPermissions());
		}
		else
			return Permission.GUEST;
	}

	public static boolean downloadEverything(){
        try {
            contestants.clear();
            tournaments.clear();
            teams.clear();
            matches.clear();
            arenas.clear();
            sys_users.clear();
            ServerData.convertContestants(sc.getTable("contestants"));
            ServerData.convertTournaments(sc.getTable("tournaments"));
            ServerData.convertTeams(sc.getTable("teams"));
            ServerData.convertMatches(sc.getTable("matches"));
            ServerData.convertArenas(sc.getTable("arenas"));
            ServerData.convertSysUsrs(sc.getTable("system_users"));
            return true;
        } catch (Exception e) { //IOException | ClassNotFoundException e
            System.out.println("Couldn't download database.");
            ClientLog.logLine("ERROR", "Nie udało się pobrać danych z bazy danych.");
            return false;
        }
    }

	public static boolean initializeServerConnection(String address, int port) {
		try {
			sc = new ServerConnection(address,port);
			 ClientLog.logLine("INFO", "Połączono z serwerem "+address+":"+port+".");
            downloadEverything();
            //test here

            //
            sc.socketClose();
           
            return true;
		} catch (Exception e) {
			System.out.println("Couldn't connect to server.");
			ClientLog.logLine("ERROR", "Nie udało się połączyć z serwerem "+address+":"+port+".");
			return false;
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
			ClientLog.logLine("ERROR", "Nie udało się przetworzyć tabeli contestants.");
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
			ClientLog.logLine("ERROR", "Nie udało się przetworzyć tabeli teams.");
		}
	}
	
	public static void convertTournaments(CachedRowSet crs) {
		try {
			while(crs.next()) {
				int tid = crs.getInt("tournament_id");
				String name = crs.getString("name");
				String type = crs.getString("type");
				int season = crs.getInt("season");
				int system = crs.getInt("system");
				int operator = crs.getInt("operator");
				int stage = crs.getInt("stage");
				Competition.Type t;
				if(type.equals("solo"))
				{
					t=Competition.Type.SOLO;
				}
				else
				{
					t= Competition.Type.TEAM;
				}
				User u = null;
				if(!crs.wasNull())
				{
					u = ServerData.getUserById(operator);
				}
				String additional_info = crs.getString("additional_info");
				Competition c = new Competition(tid,name,t,additional_info,u, stage,system,season);
				tournaments.add(c);
			}
		} catch (SQLException e) {
			ClientLog.logLine("ERROR", "Nie udało się przetworzyć tabeli tournaments.");
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
			ClientLog.logLine("ERROR", "Nie udało się przetworzyć tabeli arenas.");
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
			ClientLog.logLine("ERROR", "Nie udało się przetworzyć tabeli matches.");

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
			ClientLog.logLine("ERROR", "Nie udało się przetworzyć tabeli system_users.");
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
	 * Ta funkcja jest wywoływane gdy klient usuwa obiekt. Oczekiwany efekt to usunięcie odpowiedniego wpisu w bazie danych.
	 * @param id Id wpisu (wiersza, obiektu) do usunięcia.
	 * @param type nazwa tabeli z której należy usunąć rekord (tournaments,matches,contestants,system_users,teams,arenas)
	 */
	public static boolean deleteEntry(int id, String type){
		boolean success=false;
		try {
			success=sc.entryRemoval(id,type);
			ClientLog.logLine("INFO", "Usunięto rekord o ID "+id+" z tabeli "+type+".");
		} catch (ClassNotFoundException | IOException e) {
			ClientLog.logLine("ERROR", "Nie udało się usunąć rekordu o ID "+id+" z tabeli "+type+".");
			success=false;
			
		}
		ServertriggeredEvents.dataUpdated(); //wywoływane gdy serwer zakończy operację
		return success;
	}

	public static void deleteTournament(int tournamentId){
	    deleteEntry(tournamentId, "tournaments");
    }

    public static void deleteMatch(int matchId){
        deleteEntry(matchId, "matches");
    }

    public static void deletePlayer(int playerId){
        deleteEntry(playerId, "contestants");
    }

    public static void deleteUser(int userId){
        deleteEntry(userId, "system_users");
    }

    public static void deleteTeam(int teamId){
	    deleteEntry(teamId, "teams");
    }

    public static void deleteArena(int arenaId){
        deleteEntry(arenaId, "arenas");
    }
    
	public static void logIn(String id, String pw){
		try {
			if(sc.verifyLogin(id,pw))
			{
				System.out.println("log in success");
				ClientLog.logLine("INFO", "Pomyślnie zalogowano użytkownika "+id+".");
			    ServertriggeredEvents.permissionsChanged(ServerData.getCurrentUserPerms()); //wywoływane gdy serwer potwierdzi zmianę uprawnień
			}
			else
			{
				ClientLog.logLine("ERROR", "Nie udało się zalogować użytkownika "+id+".");
				System.out.println("log in fail");
			}
			
		} catch (ClassNotFoundException | IOException e) {
			ClientLog.logLine("ERROR", "Nie udało się zalogować. Błąd połączenia.");
		}
	}
	/**
	 * Wylogowuje użytkownika, powinna się wywoływać przy każdym zamknięciu klienta.
	 */
	public static void logOut() {
		try {
		    if(sc!=null)
		    {
			    sc.logOut();
			    ClientLog.logLine("INFO", "Pomyślnie wylogowano bieżącego użytkownika.");
		    }
		    sc = null;
		} catch (IOException e) {
			System.out.println("Couldn't send logout request.");
			 ClientLog.logLine("ERROR", "Nie udało się wysłać żądania wylogowania.");
		}
	}
	public static void register(String id, String pw, Permission perm){
		if(ServerData.getCurrentUserPerms()==Permission.FULL)
		{
			try {
				if(!sc.createNewUser(id,pw,perm))
				{
					System.out.println("register fail");
					Dialogs.error("Nie udało się utworzyć użytkownika ", id);
					ClientLog.logLine("INFO", "Utworzono użytkownika "+id+".");
				}
				else
				{
					System.out.println("register success");
					 ClientLog.logLine("INFO", "Utworzono użytkownika "+id+".");
				}
			} catch (ClassNotFoundException | IOException e) {
				ClientLog.logLine("ERROR", "Nie udało się utworzyć użytkownika "+id+". Błąd połączenia.");
			}
		}
		else
		{
			System.out.println("no perms");
			ClientLog.logLine("ERROR", "Nie udało się utworzyć użytkownika "+id+". Brak uprawnień.");
			Dialogs.insufficientPermissions();
		}
		ServertriggeredEvents.dataUpdated(); //wywoływane gdy serwer zakończy operację
	}

	public static void newTournament(String name, String system, String type, String additional){
	    int iSystem = 0;
	    if(system.equals("Pucharowy"))
	        iSystem = 1;
	    else if(system.equals("Szwajcarski"))
	        iSystem = 2;
	    else if(system.equals("Kołowy"))
	        iSystem = 3;
        else if(system.equals("McMahona"))
            iSystem = 4;
        else if(system.equals("Wieloklasowa liga"))
            iSystem = 5;

       if(type.equals("Indywidualny"))
       {
    	   type="SOLO";
       }
       else if(type.equals("Drużynowy"))
       {
    	   type="TEAM";
       }
		if(ServerData.getCurrentUserPerms()==Permission.ORGANIZER || ServerData.getCurrentUserPerms()==Permission.FULL)
		{
            try {
                if(!sc.createNewTournament(name,iSystem,type,additional))
                {
                   Dialogs.error("Nie udało się utworzyć turnieju");
                   ClientLog.logLine("ERROR", "Nie udało się utworzyć turnieju "+name+".");
                }
                else
                {
                	ClientLog.logLine("INFO", "Utworzono turniej "+name+".");
                }
            } catch (IOException e) {
            	ClientLog.logLine("ERROR", "Nie udało się utworzyć turnieju "+name+". Błąd połączenia.");
            } catch (ClassNotFoundException e) {
            	ClientLog.logLine("ERROR", "Nie udało się utworzyć turnieju "+name+". Błąd połaczenia.");
            }
        }
		else
		{
			Dialogs.insufficientPermissions();
			ClientLog.logLine("ERROR", "Nie udało się utworzyć turnieju "+name+". Brak uprawnień.");
		}
		ServertriggeredEvents.dataUpdated(); //wywoływane gdy serwer zakończy operację
	}

    /**
     * Dodaje nowego zawodnika
     * @param teamid Id drużyny, -1 jeżeli gracz ma nie mieć drużyny.
     */
	public static void newPlayer(String name, String surname, String nickname, String contact, String language, String additional, int teamid){
		try {
			if(sc.addNewPlayer(name,surname,nickname,contact,language,additional,teamid)) {
				System.out.println("add player success");
				ClientLog.logLine("INFO", "Dodano gracza "+nickname+".");
			}
			else
			{
				System.out.println("add player fail");
				Dialogs.error("Nie udało się dodać gracza.");
				ClientLog.logLine("ERROR", "Nie udało się dodać gracza "+nickname+".");
			}
		} catch (ClassNotFoundException | IOException e) {
			ClientLog.logLine("ERROR", "Nie udało się dodać gracza "+nickname+". Błąd połączenia.");
		}
        ServertriggeredEvents.dataUpdated(); //To wywoływane gdy serwer zakończy dodawanie
    }

    /**
     * Edytuje istniejącego zawodnika
     * @param teamid Id drużyny, -1 jeżeli gracz ma nie mieć drużyny (drużyna ustawiona na "** Brak **").
     */
    public static void editPlayer(int playerId, String name, String surname, String nickname, String contact, String language, String additional, int teamid){
    	try {
			if(!sc.editContestant(playerId, name,surname,nickname,contact,language,additional,teamid))
			{
				Dialogs.error("Nie udało się edytować zawodnika.");
				ClientLog.logLine("ERROR", "Nie udało się edytować gracza "+nickname+".");
				return;
			}
			else
			{
				ClientLog.logLine("INFO", "Utworzono gracza "+nickname+".");
			}
			Player player = getContestantById(playerId);
	        new VistaPlayerViewerController(MainController.newTab("Zawodnik - " + player.getName()), player);
		} catch (ClassNotFoundException | IOException e) {
			ClientLog.logLine("ERROR", "Nie udało się edytować gracza "+nickname+". Błąd połączenia.");
		}

        ServertriggeredEvents.dataUpdated();
    }

    public static void newTeam(String name, String from, int leaderId){ //serverside ready
    	try {
			if(sc.newTeam(name,from,leaderId))
			{
				ClientLog.logLine("INFO", "Dodano drużynę "+name+".");
			}
			else
			{
				ClientLog.logLine("ERROR", "Nie udało się dodać drużyny "+name+".");

			}
		} catch (ClassNotFoundException | IOException e) {
			ClientLog.logLine("ERROR", "Nie udało się dodać drużyny "+name+". Błąd połączenia.");
		}
    
        ServertriggeredEvents.dataUpdated(); //To wywoływane gdy serwer zakończy dodawanie
    }

    public static void editTeam(int teamId, String name, String from, int leaderId){ //serverside ready
    	try {
			if(sc.editTeam(teamId,name,from,leaderId))
			{
				ClientLog.logLine("INFO", "Edytowano drużynę "+name+".");
			}
			else
			{
				ClientLog.logLine("ERROR", "Nie udało się edytować drużyny "+name+".");

			}
		} catch (ClassNotFoundException | IOException e) {
			ClientLog.logLine("ERROR", "Nie udało się edytować drużyny "+name+". Błąd połączenia.");
		}
    	 ServertriggeredEvents.dataUpdated();
        Team team = getTeamById(teamId);
        new VistaTeamViewerController(MainController.newTab("Drużyna - " + team.getName()), team);
    }

    public static void newArena(String name, String location){ //serverside ready
    	try {
			if(sc.newArena(name,location))
			{
				ClientLog.logLine("INFO", "Dodano arenę "+name+".");
			}
			else
			{
				ClientLog.logLine("ERROR", "Nie udało się dodać areny "+name+".");

			}
		} catch (ClassNotFoundException | IOException e) {
			ClientLog.logLine("ERROR", "Nie udało się dodać areny "+name+". Błąd połączenia.");
		}
        ServertriggeredEvents.dataUpdated(); //To wywoływane gdy serwer zakończy dodawanie
    }

    public static void editArena(int arenaId, String name, String location){ //serverside ready
    	try {
			if(sc.editArena(arenaId,name,location))
			{
				ClientLog.logLine("INFO", "Edytowano arenę "+name+".");
			}
			else
			{
				ClientLog.logLine("ERROR", "Nie udało się edytować areny "+name+".");

			}
		} catch (ClassNotFoundException | IOException e) {
			ClientLog.logLine("ERROR", "Nie udało się edytować areny "+name+". Błąd połączenia.");
		}
    	 ServertriggeredEvents.dataUpdated();
        Arena arena = getArenaById(arenaId);
        new VistaArenaViewerController(MainController.newTab("Arena - " + arena.getName()), arena);
    }

    /**
     * Oznacza mecz jako zaplanowany - ustawia mu datę i arenę.
     * @param matchId Id meczu, który zaplanować.
     * @param localDate Data meczu.
     * @param arenaId Id areny, która przypisać do meczu. (-1 gdy arena nie została określona)
     */
    public static void planMatch(int matchId, LocalDateTime localDate, int arenaId){
        Date date = Date.from(localDate.atZone(ZoneId.systemDefault()).toInstant());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = sdf.format(date);
        try {
			sc.planMatch(matchId,time,arenaId);
		} catch (ClassNotFoundException | IOException e) {
			ClientLog.logLine("ERROR", "Nie udało się zaplanować meczu. Błąd połączenia.");
		}
        ServertriggeredEvents.dataUpdated(); //To wywoływane gdy serwer zakończy operację
    }

    /**
     * Pobriera listę uczestników danego turnieju.
     * @param competitionId Id turnieju, z którego pobrać uczestników (zawodników lub drużyn, w zależności od typu turnieju).
     * @return Mapa zawierająca jako klucz nazwę nazwodnika, a jako wartość jego id. (analogicznie inne tego typu metody)
     */
    public static Map<String, Integer> getListOfCompetitionContestants(int competitionId){
    	Map<String, Integer> map = new HashMap<String, Integer>();
		try {
			CachedRowSet crs = sc.getTournamentCompetitors(competitionId);
			
			if(sc.getTournamentType(competitionId).equals("solo")) {
				while(crs.next()) {
					int contestantId = crs.getInt("contestant_id");
					String name = sc.getContestantName(contestantId);
					map.put(name,contestantId);
				}
			}
			else if(sc.getTournamentType(competitionId).equals("team")) {
				while(crs.next()) {
					int teamId = crs.getInt("team_id");
					String name = sc.getTeamName(teamId);
					map.put(name,teamId);
				}
			}
		} catch (ClassNotFoundException | IOException | SQLException e) {
			ClientLog.logLine("ERROR", "Nie udało się pobrać listy zawodników. Błąd połączenia.");
		}
		
		return map;
	}

    public static String getTournamentType(int tid) {
    	String type="fail";
    	try {
    		type=sc.getTournamentType(tid);
			return type;
		} catch (ClassNotFoundException | IOException e) {
			ClientLog.logLine("ERROR", "Nie udało się pobrać typu turnieju. Błąd połączenia.");
			
		}
    	return type;
    	
    }

    /**
     * Pobiera numer klasy rozgrywkowej (podanej ligi) w której znajduje się podany uczestnik (drużyna lub zawodnik)
     * @param tournamentId Id ligi
     * @param competitorId Id uczestnika turnieju (zawodnika lub drużyny - w zależności od typu podanego turnieju)
     * @return Numer klasy rozgrywkowej
     */
	public static int getCompetitorsLeagueClass(int tournamentId, int competitorId){
		int league = -1;
    	try {
    		league=sc.getLeague(tournamentId, competitorId);
			return league;
		} catch (ClassNotFoundException | IOException e) {
			ClientLog.logLine("ERROR", "Nie udało się pobrać klasy ligowej. Błąd połączenia.");
			
		}
    	return league;
    }

	public static Map<String, Integer> getListOfUnplannedMatches(int competitionId){
		Map<String, Integer> map = new HashMap<String, Integer>();
		try {
			CachedRowSet crs = sc.getUnplannedMatchesById(competitionId);
			String type = sc.getTournamentType(competitionId);
			if(type.equals("fail")) {
				
				return map;
			}
			while(crs.next()) {
				int aId;
				int bId;
				String aName = null;
				String bName = null;
				if(type.equals("solo")) {
					 aId = crs.getInt("sideA");
					 bId = crs.getInt("sideB");
					 aName=sc.getContestantName(aId);
					 bName=sc.getContestantName(bId);
				}
				else if(type.equals("team")) {
					aId = crs.getInt("teamA");
					 bId = crs.getInt("teamB");
					 aName=sc.getTeamName(aId);
					 bName=sc.getTeamName(bId);
				}
				int id = crs.getInt("match_id");
				String title = "$a vs $b";
				title=title.replace("$a", aName);
				title=title.replace("$b", bName);
				map.put(title, id);
			}
		} catch (ClassNotFoundException | IOException | SQLException e) {
			ClientLog.logLine("ERROR", "Nie udało się pobrać listy niezaplanowanych. Błąd połączenia.");
		}
		return map;
	}

    public static Map<String, Integer> getListOfAllPlannedMatches(){
    	Map<String, Integer> map = new HashMap<String, Integer>();
		try {
			CachedRowSet crs = sc.getPlannedMatches();
			
			while(crs.next()) {
				String type = sc.getTournamentTypeByMatchId(crs.getInt("match_id"));
				if(type.equals("fail")) {
					
					return map;
				}
				int aId;
				int bId;
				String aName = null;
				String bName = null;
				if(type.equals("solo")) {
					 aId = crs.getInt("sideA");
					 bId = crs.getInt("sideB");
					 aName=sc.getContestantName(aId);
					 bName=sc.getContestantName(bId);
				}
				else if(type.equals("team")) {
					aId = crs.getInt("teamA");
					 bId = crs.getInt("teamB");
					 aName=sc.getTeamName(aId);
					 bName=sc.getTeamName(bId);
				}
				int id = crs.getInt("match_id");
				String title = "$a vs $b";
				title=title.replace("$a", aName);
				title=title.replace("$b", bName);
				map.put(title, id);
			}
		} catch (ClassNotFoundException | IOException | SQLException e) {
			ClientLog.logLine("ERROR", "Nie udało się pobrać listy planowanych. Błąd połączenia.");
		}
		return map;
	}
    

	public static Map<String, Integer> getListOfPlannedMatches(int competitionId){
		Map<String, Integer> map = new HashMap<String, Integer>();
		try {
			CachedRowSet crs = sc.getPlannedMatchesById(competitionId);
			String type = sc.getTournamentType(competitionId);
			if(type.equals("fail")) {
				
				return map;
			}
			while(crs.next()) {
				int aId;
				int bId;
				String aName = null;
				String bName = null;
				if(type.equals("solo")) {
					 aId = crs.getInt("sideA");
					 bId = crs.getInt("sideB");
					 aName=sc.getContestantName(aId);
					 bName=sc.getContestantName(bId);
				}
				else if(type.equals("team")) {
					aId = crs.getInt("teamA");
					 bId = crs.getInt("teamB");
					 aName=sc.getTeamName(aId);
					 bName=sc.getTeamName(bId);
				}
				int id = crs.getInt("match_id");
				String title = "$a vs $b";
				title=title.replace("$a", aName);
				title=title.replace("$b", bName);
				map.put(title, id);
			}
		} catch (ClassNotFoundException | IOException | SQLException e) {
			ClientLog.logLine("ERROR", "Nie udało się pobrać listy planowanych. Błąd połączenia.");

		}
		return map;
	}
	public static void printCRS(CachedRowSet crs) {
		System.out.println("printin crs");
		ResultSetMetaData rsmd;
		try {
			rsmd = crs.getMetaData();
			int columnsNumber = rsmd.getColumnCount();
			while (crs.next()) {
			    for (int i = 1; i <= columnsNumber; i++) {
			        if (i > 1) System.out.print(",  ");
			        String columnValue = crs.getString(i);
			        System.out.print(columnValue + " " + rsmd.getColumnName(i));
			    }
			    System.out.println("");
			}
		} catch (SQLException e) {
			
		}
		
	}
	public static Map<String, Integer> getListOfFinishedMatches(int competitionId){
		Map<String, Integer> map = new HashMap<String, Integer>();
		try {
			CachedRowSet crs = sc.getFinishedMatchesById(competitionId);
			String type = sc.getTournamentType(competitionId);
			if(type.equals("fail")) {
				
				return map;
			}
			while(crs.next()) {
				int aId;
				int bId;
				String aName = null;
				String bName = null;
				if(type.equals("solo")) {
					 aId = crs.getInt("sideA");
					 bId = crs.getInt("sideB");
					 aName=sc.getContestantName(aId);
					 bName=sc.getContestantName(bId);
				}
				else if(type.equals("team")) {
					aId = crs.getInt("teamA");
					 bId = crs.getInt("teamB");
					 aName=sc.getTeamName(aId);
					 bName=sc.getTeamName(bId);
				}
				int id = crs.getInt("match_id");
				String title = "$a vs $b";
				title=title.replace("$a", aName);
				title=title.replace("$b", bName);
				map.put(title, id);
			}
		} catch (ClassNotFoundException | IOException | SQLException e) {
			ClientLog.logLine("ERROR", "Nie udało się pobrać listy zakończonych. Błąd połączenia.");

		}
		return map;
	}

    public static Map<String, Integer> getListOfReports(int competitionId){
    	   Map<String, Integer> map = new HashMap<String, Integer>();
    	try {
			CachedRowSet crs = sc.getReports(competitionId);
			while(crs.next()) {
				String title = crs.getString("title");
				int id=crs.getInt("report_id");
				map.put(title,id);
			}
		} catch (ClassNotFoundException | IOException | SQLException e) {
			ClientLog.logLine("ERROR", "Nie udało się pobrać listy raportów. Błąd połączenia.");

		}
    	return map;
    }

    public static Report getReportById(int reportId){
    	String title = "";
	    String s ="";
	    try {
	    	CachedRowSet crs = sc.getReportById(reportId);
		    
			if(crs.next()) {
				 title = crs.getString("title");
				 s = crs.getString("content");
			}
		} catch (SQLException | ClassNotFoundException | IOException e) {
			ClientLog.logLine("ERROR", "Nie udało się pobrać raportu. Błąd połączenia.");

		}
        return new Report(title, s);
    }

	public static void addCompetitorToCompetition(int competitorId, int competitionId){
        try {
			if(sc.getTournamentType(competitionId).equals("solo"))
			{
				sc.addContestantToCompetition(competitorId,competitionId);
			}
			else
			{
				sc.addTeamToCompetition(competitorId,competitionId);
			}
		} catch (ClassNotFoundException | IOException e) {
			ClientLog.logLine("ERROR", "Nie udało się dodać zawodnika. Błąd połączenia.");

		}
        ServertriggeredEvents.dataUpdated();
	}

	/**
	 * Przechodzi do następnego etapu podanego turnieju.
	 * @param competitionId Id turnieju, w którym ma nastąpić przejście do następnego etapu.
	 */
	public static void nextStage(int competitionId){

		try {
			if(!sc.nextStage(competitionId)) {
				Dialogs.error("Turniej zakończony.");
			}
		} catch (ClassNotFoundException | IOException e) {
			ClientLog.logLine("ERROR", "Nie udało się przejść do kolejnego etapu. Błąd połączenia.");

		}
		ServertriggeredEvents.dataUpdated(); //wywoływane gdy serwer zakończy operację
	}

    public static void setScore(int matchId, int scoreA, int scoreB) {
	    
    	try {
			if(sc.setScore(matchId,scoreA,scoreB))
			{
				ClientLog.logLine("INFO", "Ustawiono wynik meczu "+matchId+".");
			}
			else
			{
				ClientLog.logLine("ERROR", "Nie udało się ustawić wyniku.");
			}
		} catch (ClassNotFoundException | IOException e) {
			ClientLog.logLine("ERROR", "Nie udało się ustawić wyniku. Błąd połączenia.");
		}
	    ServertriggeredEvents.dataUpdated();
    }
	public static User getCurrentUser() {
		return currentUser;
	}
	public static void setCurrentUser(User currentUser) {
		ServerData.currentUser = currentUser;
	}

	public static void demotePlayer(int tournamentId, int playerId) {
		try {
			if(sc.demotePlayer(tournamentId, playerId))
			{
				ClientLog.logLine("INFO", "Zdegradowano gracza "+playerId+".");
			}
			else
			{
				ClientLog.logLine("ERROR", "Nie udało się zdegradować gracza "+playerId+".");
			}
		} catch (ClassNotFoundException | IOException e) {
			ClientLog.logLine("ERROR", "Nie udało się zdegradować gracza "+playerId+". Błąd połączenia.");
		}
	}

	public static void promotePlayer(int tournamentId, int playerId) {
		try {
			if(sc.promotePlayer(tournamentId, playerId))
			{
				ClientLog.logLine("INFO", "Awansowano gracza "+playerId+".");
			}
			else
			{
				ClientLog.logLine("ERROR", "Nie udało się awansować gracza "+playerId+".");

			}
		} catch (ClassNotFoundException | IOException e) {
			ClientLog.logLine("ERROR", "Nie udało się awansować gracza "+playerId+". Błąd połączenia.");

		}
	}

	public static void promoteTeam(int tournamentId, int teamId){
		try {
			if(sc.promoteTeam(tournamentId, teamId))
			{
				ClientLog.logLine("INFO", "Awansowano gracza "+teamId+".");
			}
			else
			{
				ClientLog.logLine("ERROR", "Nie udało się awansować drużyny "+teamId+".");

			}
		} catch (ClassNotFoundException | IOException e) {
			ClientLog.logLine("ERROR", "Nie udało się awansować drużyny "+teamId+". Błąd połączenia.");

		}
	}

	public static void demoteTeam(int tournamentId, int teamId){
		try {
			if(sc.demoteTeam(tournamentId, teamId))
			{
				ClientLog.logLine("INFO", "Zdegradowano drużynę "+teamId+".");
			}
			else
			{
				ClientLog.logLine("ERROR", "Nie udało się zdegradować drużyny "+teamId+".");
			}
		} catch (ClassNotFoundException | IOException e) {
			ClientLog.logLine("ERROR", "Nie udało się zdegradować drużyny "+teamId+". Błąd połączenia.");
		}
	}

	public static void setNumberOfRounds(int tournamentId, int numberOfRounds) {
		try {
			if(sc.setRounds(tournamentId, numberOfRounds))
			{
				ClientLog.logLine("INFO", "Ustawiono liczbę rund turnieju "+tournamentId+" na "+numberOfRounds+".");
			}
			else
			{
				ClientLog.logLine("ERROR", "Nie udało się ustawić liczby rund.");

			}
		} catch (ClassNotFoundException | IOException e) {
			ClientLog.logLine("ERROR", "Nie udało się ustawić liczby rund. Błąd połączenia.");

		}
	}

	public static boolean removeFromTournament(int participantId, int tournamentId){
		
	    try {
	    	if(sc.removeFromTournament(participantId,tournamentId))
	    	{
	    		ClientLog.logLine("INFO", "Usunięto z turnieju "+tournamentId+" gracza "+tournamentId+".");
	    		ServertriggeredEvents.dataUpdated();
	    		return true;
	    	}
	    	else
	    	{
	    		ClientLog.logLine("ERROR", "Nie udało się usunąć gracza.");
	    		return false;
	    	}
	    }catch(ClassNotFoundException | IOException e) {
	    	ClientLog.logLine("ERROR", "Nie udało się usunąć gracza. Błąd połączenia.");
	    	return false;
	    }
    }
}
