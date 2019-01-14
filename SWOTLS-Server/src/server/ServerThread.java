package server;

import javax.sql.rowset.CachedRowSet;
import java.io.*;
import java.net.Socket;
import java.sql.SQLException;
//
public class ServerThread extends Thread{
	private Socket socket;
	private SystemUser currentUser = null;
	DatabaseHandler dbH=null;
	String remoteIP=null;
	public ServerThread(Socket socket,DatabaseHandler dbH) {
		this.dbH=dbH;
		this.socket=socket;
		
	}
	public String getCleanIP() {
		String currIP=socket.getRemoteSocketAddress().toString();
		String[] split = currIP.split(":");
		return split[0];
	}
	public void updateUser() {
		if(LoggedInList.getUserByIP(remoteIP)!=null)
		{
			currentUser=LoggedInList.getUserByIP(remoteIP);
		}
	}
	public void run() {
		this.remoteIP=getCleanIP();
		updateUser();
		try {
			String message=null;
			BufferedReader bufferedReader = new BufferedReader ( new InputStreamReader(socket.getInputStream()));
			while((message=bufferedReader.readLine())!=null)
			{
				System.out.println("Acquired message: "+message);
				if(message.contains("get-table")){
					String[] request = message.split(";");
					String tableName = request[1];
					OutputStream os = socket.getOutputStream();
					ObjectOutputStream oos = new ObjectOutputStream(os);
					try {
						
						CachedRowSet crs = dbH.getQueryBuilder().getTable(tableName);
						oos.writeObject(crs);
						oos.close();
						os.close();
						ServerLog.logLine("INFO", "Pobrano tabelę "+tableName+".");
					} catch (SQLException e) {
						System.out.println("SQL error while getting table "+tableName);
						ServerLog.logLine("ERROR", "Nie udało się pobrać tabeli "+tableName+".");
					}
					
				}
				if(message.contains("remove-record")){
					String[] request = message.split(";");
					String tablename = request[1];
					int id = Integer.parseInt(request[2]);
					
					OutputStream os = socket.getOutputStream();
					ObjectOutputStream oos = new ObjectOutputStream(os);
					ServerResponse sr = new ServerResponse();
					sr.setResponseType("boolean");
					//usuwaj wszystkie powiązane anyways
					boolean success;
					try {
						success = dbH.getQueryBuilder().removeFromTable(tablename,id);
						sr.setBoolTypeResponse(success);
						oos.writeObject(sr);
						ServerLog.logLine("INFO", "Usunięto rekord ID "+id+" z tabeli "+tablename+".");
					} catch (SQLException e) {
						if(e.getMessage().contains("foreign key constraint fails"))
						{
							sr.setBoolTypeResponse(false);
							sr.setStringTypeResponse("fk-check");
							oos.writeObject(sr);
							ServerLog.logLine("ERROR", "Nie udało się usunąć rekordu o ID "+id+" z tablicy "+tablename+".");
						}
					}
					oos.close();
					os.close();
				}
				if(message.contains("create-tournament")){
					if(currentUser!=null)
					{
						if(!currentUser.getPermissions().equals("FULL")&&!currentUser.getPermissions().equals("ORGANIZER")){
							System.out.println("Can't create tournament - no user or no permissions");
							ServerLog.logLine("ERROR", "Nie udało się utworzyć turnieju. Brak uprawnień.");
							continue;
						}
					}
					else
						continue;
					String[] request = message.split(";");
					String name = request[1];
					String system = request[2];
					String type = request[3];
					String additional = request[4];
					
					int operator = currentUser.getId();
					
					OutputStream os = socket.getOutputStream();
					ObjectOutputStream oos = new ObjectOutputStream(os);
					ServerResponse sr = new ServerResponse();
					sr.setResponseType("boolean");
					
					boolean success;
					try {
						success = dbH.getQueryBuilder().createTournament(name,system,type,additional,operator);
						sr.setBoolTypeResponse(success);
						oos.writeObject(sr);
						ServerLog.logLine("INFO", "Utworzono turniej "+name+".");
					} catch (SQLException e) {
						sr.setBoolTypeResponse(false);
						oos.writeObject(sr);
						ServerLog.logLine("ERROR", "Nie udało się utworzyć turnieju. Błąd bazy danych.");
						System.out.println("SQLException when adding a tournament.");
						//e.printStackTrace();
					}
					oos.close();
					os.close();
				}
				if(message.contains("create-user")){
					if(currentUser!=null)
					{
						if(!currentUser.getPermissions().equals("FULL")){
							ServerLog.logLine("ERROR", "Nie udało się utworzyć użytkownika. Brak uprawnień.");
							continue;
						}
					}
					else
					{
						ServerLog.logLine("ERROR", "Nie udało się utworzyć użytkownika. Brak uprawnień.");
						continue;
					}
					String[] request = message.split(";");
					String name = request[1];
					String pw = request[2];
					String perms = request[3];
					
					
					
					OutputStream os = socket.getOutputStream();
					ObjectOutputStream oos = new ObjectOutputStream(os);
					ServerResponse sr = new ServerResponse();
					sr.setResponseType("boolean");
					
					boolean success;
					try {
						success = dbH.getQueryBuilder().createUser(name,pw,perms);
						sr.setBoolTypeResponse(success);
						oos.writeObject(sr);
						ServerLog.logLine("INFO", "Utworzono użytkownika "+name+".");
					} catch (SQLException e) {
						sr.setBoolTypeResponse(false);
						oos.writeObject(sr);
						System.out.println("SQLException when adding a user.");
						ServerLog.logLine("ERROR", "Nie udało się utworzyć użytkownika. Błąd bazy danych.");
						//e.printStackTrace();
					}
					oos.close();
					os.close();
				}
				if(message.contains("verify-login")){
					String[] request = message.split(";");
					String login = request[1];
					String pw = request[2];
					
					OutputStream os = socket.getOutputStream();
					ObjectOutputStream oos = new ObjectOutputStream(os);
					ServerResponse sr = new ServerResponse();
					sr.setResponseType("int");
					int uid=-1;
					SystemUser fetchedUser=null;
					try {
						fetchedUser = dbH.getQueryBuilder().verifySystemLogin(login,pw);
						if(fetchedUser==null)
						{
							sr.setBoolTypeResponse(false);
							ServerLog.logLine("ERROR", "Błąd logowania. Błędne hasło lub login.");
						}
						else
						{
							sr.setBoolTypeResponse(true);
							currentUser=fetchedUser;
							LoggedInList.addUser(getCleanIP(), fetchedUser);
							uid = fetchedUser.getId();
							sr.setStringTypeResponse(fetchedUser.getPermissions());
							ServerLog.logLine("INFO", "Zalogowano użytkownika "+login+" o uprawnieniach "+fetchedUser.getPermissions()+".");
						}
						sr.setIntTypeResponse(uid);
						oos.writeObject(sr);
					} catch (SQLException e) {
						sr.setBoolTypeResponse(false);
						sr.setIntTypeResponse(-1);
						oos.writeObject(sr);
						ServerLog.logLine("ERROR", "Błąd logowania.");
					}
					oos.close();
					os.close();
				}
				if(message.contains("add-player")){
					if(currentUser!=null)
					{
						if(!currentUser.getPermissions().equals("FULL")&&!currentUser.getPermissions().equals("ORGANIZER")){
							ServerLog.logLine("ERROR", "Nie udało się utworzyć zawodnika. Brak uprawnień.");
							continue;
						}
					}
					else
					{
						ServerLog.logLine("ERROR", "Nie udało się utworzyć zawodnika. Brak uprawnień.");
						continue;
					}
					String[] request = message.split(";");
					String name = request[1];
					String surname = request[2];
					String nickname = request[3];
					String contact = request[4];
					String language = request[5];
					String additional = request[6];
					String teamid = request[7];
				
					OutputStream os = socket.getOutputStream();
					ObjectOutputStream oos = new ObjectOutputStream(os);
					ServerResponse sr = new ServerResponse();
					sr.setResponseType("boolean");
					
					boolean success;
					try {
						success = dbH.getQueryBuilder().addPlayer(name,nickname,surname,contact,language,additional,teamid);
						sr.setBoolTypeResponse(success);
						oos.writeObject(sr);
						ServerLog.logLine("INFO", "Dodano gracza "+nickname+ ".");
					} catch (SQLException e) {
						sr.setBoolTypeResponse(false);
						oos.writeObject(sr);
						System.out.println("SQLException when adding a user.");
						ServerLog.logLine("ERROR", "Nie udało się dodać gracza. Błąd bazy danych.");
					}
					oos.close();
					os.close();
				}
				if(message.contains("edit-player")){
					if(currentUser!=null)
					{
						if(!currentUser.getPermissions().equals("FULL")&&!currentUser.getPermissions().equals("ORGANIZER")){
							ServerLog.logLine("ERROR", "Nie udało się edytować zawodnika. Brak uprawnień.");
							continue;
						}
					}
					else
					{
						ServerLog.logLine("ERROR", "Nie udało się edytować zawodnika. Brak uprawnień.");
						continue;
					}
					String[] request = message.split(";");
					int id = Integer.valueOf(request[1]);
					String name = request[2];
					String surname = request[3];
					String nickname = request[4];
					String contact = request[5];
					String language = request[6];
					String additional = request[7];
					String teamid = request[8];
				
					OutputStream os = socket.getOutputStream();
					ObjectOutputStream oos = new ObjectOutputStream(os);
					ServerResponse sr = new ServerResponse();
					sr.setResponseType("boolean");
					
					boolean success;
					try {
						success = dbH.getQueryBuilder().editPlayer(id,name,nickname,surname,contact,language,additional,teamid);
						sr.setBoolTypeResponse(success);
						oos.writeObject(sr);
						ServerLog.logLine("INFO", "Pomyślnie edytowano gracza "+id+". "+nickname+".");
					} catch (SQLException e) {
						sr.setBoolTypeResponse(false);
						oos.writeObject(sr);
						System.out.println("SQLException when editing a user.");
						ServerLog.logLine("ERROR", "Nie udało się edytować gracza. Błąd bazy danych.");

						//e.printStackTrace();
					}
					oos.close();
					os.close();
				}
				if(message.contains("log-out")) {
					LoggedInList.removeUserByIP(getCleanIP());
					System.out.println("User logged out");
					ServerLog.logLine("INFO", "Wylogowano użytkownika o IP "+getCleanIP()+".");
				}
				if(message.contains("demote-player")){
					if(currentUser!=null)
					{
						if(!currentUser.getPermissions().equals("FULL")&&!currentUser.getPermissions().equals("ORGANIZER")){
							ServerLog.logLine("ERROR", "Nie udało się degradować zawodnika. Brak uprawnień.");
							continue;
						}
					}
					else
					{
						ServerLog.logLine("ERROR", "Nie udało się degradować zawodnika. Brak uprawnień.");
						continue;
					}
					String[] request = message.split(";");
					int tid = Integer.valueOf(request[1]);
					int pid = Integer.valueOf(request[2]);
				
					OutputStream os = socket.getOutputStream();
					ObjectOutputStream oos = new ObjectOutputStream(os);
					ServerResponse sr = new ServerResponse();
					sr.setResponseType("boolean");
					
					boolean success;
					try {
						success = dbH.getQueryBuilder().demotePlayer(tid,pid);
						sr.setBoolTypeResponse(success);
						oos.writeObject(sr);
						ServerLog.logLine("INFO", "Pomyślnie zdegradowano gracza "+pid+".");
					} catch (SQLException e) {
						sr.setBoolTypeResponse(false);
						oos.writeObject(sr);
						System.out.println("SQLException when demoting a user.");
						ServerLog.logLine("ERROR", "Nie udało się zdegradować gracza. Błąd bazy danych.");

						e.printStackTrace();
					}
					oos.close();
					os.close();
				}
				if(message.contains("promote-player")){
					if(currentUser!=null)
					{
						if(!currentUser.getPermissions().equals("FULL")&&!currentUser.getPermissions().equals("ORGANIZER")){
							ServerLog.logLine("ERROR", "Nie udało się awansować zawodnika. Brak uprawnień.");
							continue;
						}
					}
					else
					{
						ServerLog.logLine("ERROR", "Nie udało się awansować zawodnika. Brak uprawnień.");
						continue;
					}
					String[] request = message.split(";");
					int tid = Integer.valueOf(request[1]);
					int pid = Integer.valueOf(request[2]);
				
					OutputStream os = socket.getOutputStream();
					ObjectOutputStream oos = new ObjectOutputStream(os);
					ServerResponse sr = new ServerResponse();
					sr.setResponseType("boolean");
					
					boolean success;
					try {
						success = dbH.getQueryBuilder().promotePlayer(tid,pid);
						sr.setBoolTypeResponse(success);
						oos.writeObject(sr);
						ServerLog.logLine("INFO", "Pomyślnie awansowano gracza "+pid+".");
					} catch (SQLException e) {
						sr.setBoolTypeResponse(false);
						oos.writeObject(sr);
						System.out.println("SQLException when promoting a user.");
						ServerLog.logLine("ERROR", "Nie udało się awansować gracza. Błąd bazy danych.");
					}
					oos.close();
					os.close();
				}
				if(message.contains("add-team")){
					if(currentUser!=null)
					{
						if(!currentUser.getPermissions().equals("FULL")&&!currentUser.getPermissions().equals("ORGANIZER")){
							ServerLog.logLine("ERROR", "Nie udało się utworzyć drużyny. Brak uprawnień.");
							continue;
						}
					}
					else
					{
						ServerLog.logLine("ERROR", "Nie udało się utworzyć drużyny. Brak uprawnień.");
						continue;
					}
					String[] request = message.split(";");
					String name = request[1];
					String from = request[2];
					int lid = Integer.valueOf(request[3]);

					OutputStream os = socket.getOutputStream();
					ObjectOutputStream oos = new ObjectOutputStream(os);
					ServerResponse sr = new ServerResponse();
					sr.setResponseType("boolean");
					
					boolean success;
					try {
						success = dbH.getQueryBuilder().addTeam(name,from,lid);
						sr.setBoolTypeResponse(success);
						oos.writeObject(sr);
						ServerLog.logLine("INFO", "Utworzono drużynę "+name+".");
					} catch (SQLException e) {
						sr.setBoolTypeResponse(false);
						oos.writeObject(sr);
						System.out.println("SQLException when adding a team.");
						ServerLog.logLine("ERROR", "Nie udało się utworzyć drużyny. Błąd bazy danych.");
						e.printStackTrace();
					}
					oos.close();
					os.close();
				}
				if(message.contains("edit-team")){
					if(currentUser!=null)
					{
						if(!currentUser.getPermissions().equals("FULL")&&!currentUser.getPermissions().equals("ORGANIZER")){
							ServerLog.logLine("ERROR", "Nie udało się edytować drużyny. Brak uprawnień.");
							continue;
						}
					}
					else
					{
						ServerLog.logLine("ERROR", "Nie udało się edytować drużyny. Brak uprawnień.");
						continue;
					}
					String[] request = message.split(";");
					int tid = Integer.valueOf(request[1]);
					String name = request[2];
					String from = request[3];
					int lid = Integer.valueOf(request[4]);

					OutputStream os = socket.getOutputStream();
					ObjectOutputStream oos = new ObjectOutputStream(os);
					ServerResponse sr = new ServerResponse();
					sr.setResponseType("boolean");
					
					boolean success;
					try {
						success = dbH.getQueryBuilder().editTeam(tid,name,from,lid);
						sr.setBoolTypeResponse(success);
						oos.writeObject(sr);
						ServerLog.logLine("INFO", "Edytowano drużynę "+name+".");
					} catch (SQLException e) {
						sr.setBoolTypeResponse(false);
						oos.writeObject(sr);
						System.out.println("SQLException when editing a team.");
						ServerLog.logLine("ERROR", "Nie udało się edytować drużyny. Błąd bazy danych.");
						e.printStackTrace();
					}
					oos.close();
					os.close();
				}
				if(message.contains("new-arena")){
					if(currentUser!=null)
					{
						if(!currentUser.getPermissions().equals("FULL")&&!currentUser.getPermissions().equals("ORGANIZER")){
							ServerLog.logLine("ERROR", "Nie udało się utworzyć areny. Brak uprawnień.");
							continue;
						}
					}
					else
					{
						ServerLog.logLine("ERROR", "Nie udało się utworzyć areny. Brak uprawnień.");
						continue;
					}
					String[] request = message.split(";");
					String name = request[1];
					String location = request[2];
					
					OutputStream os = socket.getOutputStream();
					ObjectOutputStream oos = new ObjectOutputStream(os);
					ServerResponse sr = new ServerResponse();
					sr.setResponseType("boolean");
					
					boolean success;
					try {
						success = dbH.getQueryBuilder().newArena(name,location);
						sr.setBoolTypeResponse(success);
						oos.writeObject(sr);
						ServerLog.logLine("INFO", "Utworzono arenę "+name+".");
					} catch (SQLException e) {
						sr.setBoolTypeResponse(false);
						oos.writeObject(sr);
						System.out.println("SQLException when adding an arena.");
						ServerLog.logLine("ERROR", "Nie udało się utworzyć areny. Błąd bazy danych.");
						e.printStackTrace();
					}
					oos.close();
					os.close();
				}
				if(message.contains("edit-arena")){
					if(currentUser!=null)
					{
						if(!currentUser.getPermissions().equals("FULL")&&!currentUser.getPermissions().equals("ORGANIZER")){
							ServerLog.logLine("ERROR", "Nie udało się edytować areny. Brak uprawnień.");
							continue;
						}
					}
					else
					{
						ServerLog.logLine("ERROR", "Nie udało się edytować areny. Brak uprawnień.");
						continue;
					}
					String[] request = message.split(";");
					int aid = Integer.valueOf(request[1]);
					String name = request[2];
					String location = request[3];
					
					OutputStream os = socket.getOutputStream();
					ObjectOutputStream oos = new ObjectOutputStream(os);
					ServerResponse sr = new ServerResponse();
					sr.setResponseType("boolean");
					
					boolean success;
					try {
						success = dbH.getQueryBuilder().editArena(aid,name,location);
						sr.setBoolTypeResponse(success);
						oos.writeObject(sr);
						ServerLog.logLine("INFO", "Edytowano arenę "+name+".");
					} catch (SQLException e) {
						sr.setBoolTypeResponse(false);
						oos.writeObject(sr);
						System.out.println("SQLException when editing an arena.");
						ServerLog.logLine("ERROR", "Nie udało się edytować areny. Błąd bazy danych.");
						e.printStackTrace();
					}
					oos.close();
					os.close();
				}
				if(message.contains("set-score")){
					if(currentUser!=null)
					{
						if(!currentUser.getPermissions().equals("FULL")&&!currentUser.getPermissions().equals("ORGANIZER")){
							ServerLog.logLine("ERROR", "Nie udało się ustawić wyniku. Brak uprawnień.");
							continue;
						}
					}
					else
					{
						ServerLog.logLine("ERROR", "Nie udało się ustawić wyniku. Brak uprawnień.");
						continue;
					}
					String[] request = message.split(";");
					int mid = Integer.valueOf(request[1]);
					int a = Integer.valueOf(request[2]);
					int b = Integer.valueOf(request[3]);
					
					OutputStream os = socket.getOutputStream();
					ObjectOutputStream oos = new ObjectOutputStream(os);
					ServerResponse sr = new ServerResponse();
					sr.setResponseType("boolean");
					
					boolean success;
					try {
						success = dbH.getQueryBuilder().setScore(mid, a, b);
						sr.setBoolTypeResponse(success);
						oos.writeObject(sr);
						ServerLog.logLine("INFO", "Ustalono wynik meczu "+mid+".");
					} catch (SQLException e) {
						sr.setBoolTypeResponse(false);
						oos.writeObject(sr);
						System.out.println("SQLException when setting score..");
						ServerLog.logLine("ERROR", "Nie udało się ustawić wyniku. Błąd bazy danych.");
						e.printStackTrace();
					}
					oos.close();
					os.close();
				}
				if(message.contains("get-planned-matches")){

					OutputStream os = socket.getOutputStream();
					ObjectOutputStream oos = new ObjectOutputStream(os);
					try {
						
						CachedRowSet crs = dbH.getQueryBuilder().getPlannedMatches();
						oos.writeObject(crs);
						oos.close();
						os.close();
						ServerLog.logLine("INFO", "Pobrano wszystkie zaplanowane mecze.");
					} catch (SQLException e) {
						ServerLog.logLine("ERROR", "Nie udało się pobrać wszystkich zaplanowanych meczy.");
					}
					
				}
				if(message.contains("get-planned-matches-id")){
					String[] request = message.split(";");
					int tid = Integer.valueOf(request[1]);
					OutputStream os = socket.getOutputStream();
					ObjectOutputStream oos = new ObjectOutputStream(os);
					try {
						
						CachedRowSet crs = dbH.getQueryBuilder().getPlannedMatchesId(tid);
						oos.writeObject(crs);
						oos.close();
						os.close();
						ServerLog.logLine("INFO", "Pobrano zaplanowane mecze.");
					} catch (SQLException e) {
						ServerLog.logLine("ERROR", "Nie udało się pobrać zaplanowanych meczy.");
					}
					
				}
				if(message.contains("get-unplanned-matches-id")){
					String[] request = message.split(";");
					int tid = Integer.valueOf(request[1]);
					OutputStream os = socket.getOutputStream();
					ObjectOutputStream oos = new ObjectOutputStream(os);
					try {
						
						CachedRowSet crs = dbH.getQueryBuilder().getUnplannedMatches(tid);
						oos.writeObject(crs);
						oos.close();
						os.close();
						ServerLog.logLine("INFO", "Pobrano niezaplanowane mecze.");
					} catch (SQLException e) {
						ServerLog.logLine("ERROR", "Nie udało się pobrać niezaplanowanych meczy.");
					}
					
				}
				if(message.contains("get-finished-matches-id")){
					String[] request = message.split(";");
					int tid = Integer.valueOf(request[1]);
					OutputStream os = socket.getOutputStream();
					ObjectOutputStream oos = new ObjectOutputStream(os);
					try {
						
						CachedRowSet crs = dbH.getQueryBuilder().getFinishedMatchesById(tid);
						oos.writeObject(crs);
						oos.close();
						os.close();
						ServerLog.logLine("INFO", "Pobrano niezaplanowane mecze.");
					} catch (SQLException e) {
						ServerLog.logLine("ERROR", "Nie udało się pobrać niezaplanowanych meczy.");
					}
					
				}
				if(message.contains("get-tournament-type")){
					String[] request = message.split(";");
					int tid = Integer.valueOf(request[1]);
					OutputStream os = socket.getOutputStream();
					ObjectOutputStream oos = new ObjectOutputStream(os);
					ServerResponse sr = new ServerResponse();
					sr.setResponseType("string");
					try {
						
						String type = dbH.getQueryBuilder().getTournamentType(tid);
						sr.setStringTypeResponse(type);
						sr.setBoolTypeResponse(true);
						oos.writeObject(sr);
						oos.close();
						os.close();
						ServerLog.logLine("INFO", "Pobrano typ turnieju "+tid+".");
					} catch (SQLException e) {
						ServerLog.logLine("ERROR", "Nie udało się pobrać typu turnieju.");
						sr.setBoolTypeResponse(false);
						oos.writeObject(sr);
						oos.close();
						os.close();
					}
					
				}
				if(message.contains("get-tournament-type-from-matchid")){
					String[] request = message.split(";");
					int mid = Integer.valueOf(request[1]);
					OutputStream os = socket.getOutputStream();
					ObjectOutputStream oos = new ObjectOutputStream(os);
					ServerResponse sr = new ServerResponse();
					sr.setResponseType("string");
					try {
						
						String type = dbH.getQueryBuilder().getTournamentTypeByMatchId(mid);
						sr.setStringTypeResponse(type);
						sr.setBoolTypeResponse(true);
						oos.writeObject(sr);
						oos.close();
						os.close();
						ServerLog.logLine("INFO", "Pobrano typ turnieju meczu "+mid+".");
					} catch (SQLException e) {
						ServerLog.logLine("ERROR", "Nie udało się pobrać typu turnieju meczu.");
						sr.setBoolTypeResponse(false);
						oos.writeObject(sr);
						oos.close();
						os.close();
					}
					
				}
				if(message.contains("get-contestant-name")){
					String[] request = message.split(";");
					int id = Integer.valueOf(request[1]);
					OutputStream os = socket.getOutputStream();
					ObjectOutputStream oos = new ObjectOutputStream(os);
					ServerResponse sr = new ServerResponse();
					sr.setResponseType("string");
					try {
						
						String type = dbH.getQueryBuilder().getContestantName(id);
						sr.setStringTypeResponse(type);
						sr.setBoolTypeResponse(true);
						oos.writeObject(sr);
						oos.close();
						os.close();
						ServerLog.logLine("INFO", "Pobrano nazwę gracza "+id+".");
					} catch (SQLException e) {
						e.printStackTrace();
						ServerLog.logLine("ERROR", "Nie udało się pobrać nazwy gracza.");
						sr.setBoolTypeResponse(false);
						oos.writeObject(sr);
						oos.close();
						os.close();
					}
					
				}
				if(message.contains("get-team-name")){
					String[] request = message.split(";");
					int id = Integer.valueOf(request[1]);
					OutputStream os = socket.getOutputStream();
					ObjectOutputStream oos = new ObjectOutputStream(os);
					ServerResponse sr = new ServerResponse();
					sr.setResponseType("string");
					try {
						
						String type = dbH.getQueryBuilder().getTeamName(id);
						System.out.println("typeep: "+type);
						sr.setStringTypeResponse(type);
						sr.setBoolTypeResponse(true);
						oos.writeObject(sr);
						oos.close();
						os.close();
						ServerLog.logLine("INFO", "Pobrano nazwę drużyny "+id+".");
					} catch (SQLException e) {
						ServerLog.logLine("ERROR", "Nie udało się pobrać nazwy drużyny.");
						sr.setBoolTypeResponse(false);
						oos.writeObject(sr);
						oos.close();
						os.close();
					}
					
				}
				if(message.contains("contestant-to-competition")){
					if(currentUser!=null)
					{
						if(!currentUser.getPermissions().equals("FULL")&&!currentUser.getPermissions().equals("ORGANIZER")){
							ServerLog.logLine("ERROR", "Nie udało się dodać gracza. Brak uprawnień.");
							continue;
						}
					}
					else
					{
						ServerLog.logLine("ERROR", "Nie udało się dodać gracza. Brak uprawnień.");
						continue;
					}
					String[] request = message.split(";");
					int cid = Integer.valueOf(request[1]);
					int tid = Integer.valueOf(request[2]);

					
					OutputStream os = socket.getOutputStream();
					ObjectOutputStream oos = new ObjectOutputStream(os);
					ServerResponse sr = new ServerResponse();
					sr.setResponseType("boolean");
					
					boolean success;
					try {
						success = dbH.getQueryBuilder().addContestantToCompetition(cid,tid);
						sr.setBoolTypeResponse(success);
						oos.writeObject(sr);
						ServerLog.logLine("INFO", "Dodano gracza "+cid+" do turnieju "+tid);
					} catch (SQLException e) {
						sr.setBoolTypeResponse(false);
						oos.writeObject(sr);
						ServerLog.logLine("ERROR", "Nie udało się dodać gracza do turnieju. Błąd bazy danych.");
						e.printStackTrace();
					}
					oos.close();
					os.close();
				}
				if(message.contains("team-to-competition")){
					if(currentUser!=null)
					{
						if(!currentUser.getPermissions().equals("FULL")&&!currentUser.getPermissions().equals("ORGANIZER")){
							ServerLog.logLine("ERROR", "Nie udało się dodać drużyny. Brak uprawnień.");
							continue;
						}
					}
					else
					{
						ServerLog.logLine("ERROR", "Nie udało się dodać drużyny. Brak uprawnień.");
						continue;
					}
					String[] request = message.split(";");
					int cid = Integer.valueOf(request[1]);
					int tid = Integer.valueOf(request[2]);

					
					OutputStream os = socket.getOutputStream();
					ObjectOutputStream oos = new ObjectOutputStream(os);
					ServerResponse sr = new ServerResponse();
					sr.setResponseType("boolean");
					
					boolean success;
					try {
						success = dbH.getQueryBuilder().addTeamToCompetition(cid,tid);
						sr.setBoolTypeResponse(success);
						oos.writeObject(sr);
						ServerLog.logLine("INFO", "Dodano drużynę "+cid+" do turnieju "+tid);
					} catch (SQLException e) {
						sr.setBoolTypeResponse(false);
						oos.writeObject(sr);
						ServerLog.logLine("ERROR", "Nie udało się dodać drużyny do turnieju. Błąd bazy danych.");
						e.printStackTrace();
					}
					oos.close();
					os.close();
				}
				if(message.contains("plan-match")){
					if(currentUser!=null)
					{
						if(!currentUser.getPermissions().equals("FULL")&&!currentUser.getPermissions().equals("ORGANIZER")){
							ServerLog.logLine("ERROR", "Nie udało się zaplanować meczu. Brak uprawnień.");
							continue;
						}
					}
					else
					{
						ServerLog.logLine("ERROR", "Nie udało się zaplanować meczu. Brak uprawnień.");
						continue;
					}
					String[] request = message.split(";");
					int cid = Integer.valueOf(request[1]);
					String time = request[2];
					int aid = Integer.valueOf(request[3]);

					
					OutputStream os = socket.getOutputStream();
					ObjectOutputStream oos = new ObjectOutputStream(os);
					ServerResponse sr = new ServerResponse();
					sr.setResponseType("boolean");
					
					boolean success;
					try {
						success = dbH.getQueryBuilder().planMatch(cid,time,aid);
						sr.setBoolTypeResponse(success);
						oos.writeObject(sr);
						ServerLog.logLine("INFO", "Zaplanowano mecz "+cid);
					} catch (SQLException e) {
						sr.setBoolTypeResponse(false);
						oos.writeObject(sr);
						ServerLog.logLine("ERROR", "Nie udało się zaplanować meczu. Błąd bazy danych.");
						e.printStackTrace();
					}
					oos.close();
					os.close();
				}
				
			}
		} catch (IOException e) {
			//System.out.println("Connection closed.");
		}
	}
}
