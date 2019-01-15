package application;

import server.ServerResponse;

import javax.sql.rowset.CachedRowSet;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class ServerConnection {
	private Socket socket = null;
	private String address = null;
	private int port;
	private boolean newRequest = false;
	public ServerConnection(String address, int port) throws Exception {
		this.address=address;
		this.port=port;
		this.socket=new Socket(address,port);
	}
	public void sendServerRequest(String request) throws IOException {
		socketOpen();
		PrintWriter printWriter = new PrintWriter(socket.getOutputStream(),true);
		printWriter.println(request);
		socketClose();
	}
	public boolean getNewRequest() {
		return newRequest;
	}
	public CachedRowSet getTable(String tableName) throws IOException, ClassNotFoundException {
		socketOpen();
		PrintWriter printWriter = new PrintWriter(socket.getOutputStream(),true);
		String request = "get-table;"+tableName;
		printWriter.println(request);
		
		InputStream is = socket.getInputStream();
		ObjectInputStream ois = new ObjectInputStream(is);
		CachedRowSet crs = (CachedRowSet)ois.readObject();
		if(crs!=null) {
			System.out.println("Successfully downloaded table "+tableName);
			ClientLog.logLine("INFO", "Pobrano tabelę "+tableName+".");
		}

		socket.close();
		return crs;
	}
	public void socketClose() {
		try {
			socket.close();
		} catch (IOException e) {
			System.out.println("Couldn't close socket");
		}
	}
	public void socketOpen() {
		try {
			socket=new Socket(address,port);
		} catch (Exception e) {
			System.out.println("Couldn't connect to server.");
		}
	}
	/**
	 * Wysyła do serwera prośbę o usunięcie rekordu o podanym id z podanej tabeli
	 * @param id
	 * @param type (matches/contestants/tournaments/arenas/teams)
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public boolean entryRemoval(int id, String type) throws IOException, ClassNotFoundException {
		
		socketOpen();
		PrintWriter printWriter = new PrintWriter(socket.getOutputStream(),true);
		String request = "remove-record;"+type+";"+id;
		printWriter.println(request);
		
		InputStream is = socket.getInputStream();
		ObjectInputStream ois = new ObjectInputStream(is);
		ServerResponse sr = (ServerResponse)ois.readObject();
		if(sr!=null && sr.getResponseType().equals("boolean") && sr.getBoolTypeResponse())
		{
			return true;
		}
		else
		{
			if(sr!=null&& sr.getStringTypeResponse()!=null &&sr.getStringTypeResponse().equals("fk-check"))
			{
				ServertriggeredEvents.error("Rekord powiązany z innym rekordem. Proszę najpierw usunąć powiązany rekord.");
				System.out.println("Can't delete entry because of foreign key constraints.");
			}
			return false;
		}

	}

	public boolean verifyLogin(String id, String pw) throws IOException, ClassNotFoundException {
		
		socketOpen();
		PrintWriter printWriter = new PrintWriter(socket.getOutputStream(),true);
		String request = "verify-login;"+id+";"+pw;
		printWriter.println(request);
		
		InputStream is = socket.getInputStream();
		ObjectInputStream ois = new ObjectInputStream(is);
		ServerResponse sr = (ServerResponse)ois.readObject();
		if(sr!=null && sr.getResponseType().equals("int") && sr.getBoolTypeResponse())
		{
			int uid = sr.getIntTypeResponse();
			String perms = sr.getStringTypeResponse();
			ServerData.setCurrentUser(new User(uid,id,perms));
			System.out.println("Successfully logged in "+ServerData.getCurrentUser());
			return true;
		}
		else
		{
			return false;
		}
	}
	public boolean createNewUser(String id, String pw, Permission perm) throws IOException, ClassNotFoundException {
		String perm_string=perm.name();
		socketOpen();
		PrintWriter printWriter = new PrintWriter(socket.getOutputStream(),true);
		String request = "create-user;"+id+";"+pw+";"+perm_string;
		printWriter.println(request);
		
		InputStream is = socket.getInputStream();
		ObjectInputStream ois = new ObjectInputStream(is);
		ServerResponse sr = (ServerResponse)ois.readObject();
		if(sr!=null && sr.getResponseType().equals("boolean") && sr.getBoolTypeResponse())
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	public boolean createNewTournament(String name, int system, String type, String additional) throws IOException, ClassNotFoundException {
		socketOpen();
		PrintWriter printWriter = new PrintWriter(socket.getOutputStream(),true);
		String request = "create-tournament;"+name+";"+system+";"+type+";"+additional;
		printWriter.println(request);
		
		InputStream is = socket.getInputStream();
		ObjectInputStream ois = new ObjectInputStream(is);
		ServerResponse sr = (ServerResponse)ois.readObject();
		if(sr!=null && sr.getResponseType().equals("boolean") && sr.getBoolTypeResponse())
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	public boolean addNewPlayer(String name, String surname, String nickname, String contact, String language,
			String additional, int teamid) throws IOException, ClassNotFoundException {
		socketOpen();
		PrintWriter printWriter = new PrintWriter(socket.getOutputStream(),true);
		String request = "add-player;"+name+";"+surname+";"+nickname+";"+contact+";"+language+";"+additional+";"+teamid;
		printWriter.println(request);
		
		InputStream is = socket.getInputStream();
		ObjectInputStream ois = new ObjectInputStream(is);
		ServerResponse sr = (ServerResponse)ois.readObject();
		if(sr!=null && sr.getResponseType().equals("boolean") && sr.getBoolTypeResponse())
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	public void logOut() throws IOException {
		socketOpen();
		PrintWriter printWriter = new PrintWriter(socket.getOutputStream(),true);
		String request = "log-out";
		printWriter.println(request);
		
	}
	public boolean editContestant(int playerId, String name, String surname, String nickname, String contact,
			String language, String additional, int teamid) throws IOException, ClassNotFoundException {
		socketOpen();
		PrintWriter printWriter = new PrintWriter(socket.getOutputStream(),true);
		String request = "edit-player;"+playerId+";"+name+";"+surname+";"+nickname+";"+contact+";"+language+";"+additional+";"+teamid;
		printWriter.println(request);
		
		InputStream is = socket.getInputStream();
		ObjectInputStream ois = new ObjectInputStream(is);
		ServerResponse sr = (ServerResponse)ois.readObject();
		if(sr!=null && sr.getResponseType().equals("boolean") && sr.getBoolTypeResponse())
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	public boolean demotePlayer(int tid, int pid) throws IOException, ClassNotFoundException {
		socketOpen();
		PrintWriter printWriter = new PrintWriter(socket.getOutputStream(),true);
		String request = "demote-player;"+tid+";"+pid;
		printWriter.println(request);
		
		InputStream is = socket.getInputStream();
		ObjectInputStream ois = new ObjectInputStream(is);
		ServerResponse sr = (ServerResponse)ois.readObject();
		if(sr!=null && sr.getResponseType().equals("boolean") && sr.getBoolTypeResponse())
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	public boolean promotePlayer(int tid, int pid) throws IOException, ClassNotFoundException {
		socketOpen();
		PrintWriter printWriter = new PrintWriter(socket.getOutputStream(),true);
		String request = "promote-player;"+tid+";"+pid;
		printWriter.println(request);
		
		InputStream is = socket.getInputStream();
		ObjectInputStream ois = new ObjectInputStream(is);
		ServerResponse sr = (ServerResponse)ois.readObject();
		if(sr!=null && sr.getResponseType().equals("boolean") && sr.getBoolTypeResponse())
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	public boolean newTeam(String name, String from, int leaderId) throws IOException, ClassNotFoundException  {
		socketOpen();
		PrintWriter printWriter = new PrintWriter(socket.getOutputStream(),true);
		String request = "add-team;"+name+";"+from+";"+leaderId;
		printWriter.println(request);
		
		InputStream is = socket.getInputStream();
		ObjectInputStream ois = new ObjectInputStream(is);
		ServerResponse sr = (ServerResponse)ois.readObject();
		if(sr!=null && sr.getResponseType().equals("boolean") && sr.getBoolTypeResponse())
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	public boolean editTeam(int teamId, String name, String from, int leaderId)  throws IOException, ClassNotFoundException {
		socketOpen();
		PrintWriter printWriter = new PrintWriter(socket.getOutputStream(),true);
		String request = "edit-team;"+teamId+";"+name+";"+from+";"+leaderId;
		printWriter.println(request);
		
		InputStream is = socket.getInputStream();
		ObjectInputStream ois = new ObjectInputStream(is);
		ServerResponse sr = (ServerResponse)ois.readObject();
		if(sr!=null && sr.getResponseType().equals("boolean") && sr.getBoolTypeResponse())
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	public boolean newArena(String name, String location) throws IOException, ClassNotFoundException  {
		socketOpen();
		PrintWriter printWriter = new PrintWriter(socket.getOutputStream(),true);
		String request = "new-arena;"+name+";"+location;
		printWriter.println(request);
		
		InputStream is = socket.getInputStream();
		ObjectInputStream ois = new ObjectInputStream(is);
		ServerResponse sr = (ServerResponse)ois.readObject();
		if(sr!=null && sr.getResponseType().equals("boolean") && sr.getBoolTypeResponse())
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	public boolean editArena(int arenaId, String name, String location) throws IOException, ClassNotFoundException  {
		socketOpen();
		PrintWriter printWriter = new PrintWriter(socket.getOutputStream(),true);
		String request = "edit-arena;"+arenaId+";"+name+";"+location;
		printWriter.println(request);
		
		InputStream is = socket.getInputStream();
		ObjectInputStream ois = new ObjectInputStream(is);
		ServerResponse sr = (ServerResponse)ois.readObject();
		if(sr!=null && sr.getResponseType().equals("boolean") && sr.getBoolTypeResponse())
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	public boolean setScore(int matchId, int scoreA, int scoreB) throws IOException, ClassNotFoundException {
		socketOpen();
		PrintWriter printWriter = new PrintWriter(socket.getOutputStream(),true);
		String request = "set-score;"+matchId+";"+scoreA+";"+scoreB;
		printWriter.println(request);
		
		InputStream is = socket.getInputStream();
		ObjectInputStream ois = new ObjectInputStream(is);
		ServerResponse sr = (ServerResponse)ois.readObject();
		if(sr!=null && sr.getResponseType().equals("boolean") && sr.getBoolTypeResponse())
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	public boolean setRounds(int tournamentId, int numberOfRounds)  throws IOException, ClassNotFoundException{
		socketOpen();
		PrintWriter printWriter = new PrintWriter(socket.getOutputStream(),true);
		String request = "set-rounds;"+tournamentId+";"+numberOfRounds;
		printWriter.println(request);
		
		InputStream is = socket.getInputStream();
		ObjectInputStream ois = new ObjectInputStream(is);
		ServerResponse sr = (ServerResponse)ois.readObject();
		if(sr!=null && sr.getResponseType().equals("boolean") && sr.getBoolTypeResponse())
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	public CachedRowSet getPlannedMatches() throws IOException, ClassNotFoundException {
		socketOpen();
		PrintWriter printWriter = new PrintWriter(socket.getOutputStream(),true);
		String request = "get-planned-matches";
		printWriter.println(request);
		
		InputStream is = socket.getInputStream();
		ObjectInputStream ois = new ObjectInputStream(is);
		CachedRowSet crs = (CachedRowSet)ois.readObject();
		if(crs!=null) {
			ClientLog.logLine("INFO", "Pobrano wszystkie zaplanowane mecze.");
		}

		socket.close();
		return crs;
	}
	public CachedRowSet getPlannedMatchesById(int competitionId) throws ClassNotFoundException, IOException {
		socketOpen();
		PrintWriter printWriter = new PrintWriter(socket.getOutputStream(),true);
		String request = "get-planned-matches-id;"+competitionId;
		printWriter.println(request);
		
		InputStream is = socket.getInputStream();
		ObjectInputStream ois = new ObjectInputStream(is);
		CachedRowSet crs = (CachedRowSet)ois.readObject();
		if(crs!=null) {
			ClientLog.logLine("INFO", "Pobrano zaplanowane mecze dla id "+competitionId+".");
		}

		socket.close();
		return crs;
	}
	public CachedRowSet getUnplannedMatchesById(int competitionId) throws IOException, ClassNotFoundException {
		socketOpen();
		PrintWriter printWriter = new PrintWriter(socket.getOutputStream(),true);
		String request = "get-unplanned-matches-id;"+competitionId;
		printWriter.println(request);
		
		InputStream is = socket.getInputStream();
		ObjectInputStream ois = new ObjectInputStream(is);
		CachedRowSet crs = (CachedRowSet)ois.readObject();
		if(crs!=null) {
			ClientLog.logLine("INFO", "Pobrano niezaplanowane mecze dla id "+competitionId+".");
		}

		socket.close();
		return crs;
	}
	public CachedRowSet getFinishedMatchesById(int competitionId) throws IOException, ClassNotFoundException {
		socketOpen();
		PrintWriter printWriter = new PrintWriter(socket.getOutputStream(),true);
		String request = "get-finished-matches-id;"+competitionId;
		printWriter.println(request);
		
		InputStream is = socket.getInputStream();
		ObjectInputStream ois = new ObjectInputStream(is);
		CachedRowSet crs = (CachedRowSet)ois.readObject();
		if(crs!=null) {
			ClientLog.logLine("INFO", "Pobrano zakończone mecze dla id "+competitionId+".");
		}

		socket.close();
		return crs;
	}
	public String getTournamentType(int competitionId) throws IOException, ClassNotFoundException {
		socketOpen();
		PrintWriter printWriter = new PrintWriter(socket.getOutputStream(),true);
		String request = "get-tournament-type;"+competitionId;
		printWriter.println(request);
		
		InputStream is = socket.getInputStream();
		ObjectInputStream ois = new ObjectInputStream(is);
		ServerResponse sr = (ServerResponse)ois.readObject();
		if(sr!=null && sr.getResponseType().equals("string") && sr.getBoolTypeResponse())
		{
			return sr.getStringTypeResponse();
		}
		else
		{
			return "fail";
		}		
	}
	public String getTournamentTypeByMatchId(int mid) throws IOException, ClassNotFoundException {
		socketOpen();
		PrintWriter printWriter = new PrintWriter(socket.getOutputStream(),true);
		String request = "get-tournament-type-from-matchid;"+mid;
		printWriter.println(request);
		
		InputStream is = socket.getInputStream();
		ObjectInputStream ois = new ObjectInputStream(is);
		ServerResponse sr = (ServerResponse)ois.readObject();
		if(sr!=null && sr.getResponseType().equals("string") && sr.getBoolTypeResponse())
		{
			return sr.getStringTypeResponse();
		}
		else
		{
			return "fail";
		}		
	}
	public String getContestantName(int id) throws IOException, ClassNotFoundException {
		socketOpen();
		PrintWriter printWriter = new PrintWriter(socket.getOutputStream(),true);
		String request = "get-contestant-name;"+id;
		printWriter.println(request);
		
		InputStream is = socket.getInputStream();
		ObjectInputStream ois = new ObjectInputStream(is);
		ServerResponse sr = (ServerResponse)ois.readObject();
		if(sr!=null && sr.getResponseType().equals("string") && sr.getBoolTypeResponse())
		{
			return sr.getStringTypeResponse();
		}
		else
		{
			return null;
		}
	}
	public String getTeamName(int id) throws IOException, ClassNotFoundException {
		socketOpen();
		PrintWriter printWriter = new PrintWriter(socket.getOutputStream(),true);
		String request = "get-team-name;"+id;
		printWriter.println(request);
		
		InputStream is = socket.getInputStream();
		ObjectInputStream ois = new ObjectInputStream(is);
		ServerResponse sr = (ServerResponse)ois.readObject();
		if(sr!=null && sr.getResponseType().equals("string") && sr.getBoolTypeResponse())
		{
			System.out.println("response: "+sr.getResponseType() + sr.getStringTypeResponse());
			return sr.getStringTypeResponse();
		}
		else
		{
			
			return null;
		}
	}
	public boolean addContestantToCompetition(int competitorId, int competitionId) throws IOException, ClassNotFoundException {
		socketOpen();
		PrintWriter printWriter = new PrintWriter(socket.getOutputStream(),true);
		String request = "contestant-to-competition;"+competitorId+";"+competitionId;
		printWriter.println(request);
		
		InputStream is = socket.getInputStream();
		ObjectInputStream ois = new ObjectInputStream(is);
		ServerResponse sr = (ServerResponse)ois.readObject();
		if(sr!=null && sr.getResponseType().equals("boolean") && sr.getBoolTypeResponse())
		{
			return true;
		}
		else
		{
			return false;
		}
	}
		
	
	public boolean addTeamToCompetition(int competitorId, int competitionId) throws IOException, ClassNotFoundException{
		socketOpen();
		PrintWriter printWriter = new PrintWriter(socket.getOutputStream(),true);
		String request = "team-to-competition;"+competitorId+";"+competitionId;
		printWriter.println(request);
		
		InputStream is = socket.getInputStream();
		ObjectInputStream ois = new ObjectInputStream(is);
		ServerResponse sr = (ServerResponse)ois.readObject();
		if(sr!=null && sr.getResponseType().equals("boolean") && sr.getBoolTypeResponse())
		{
			return true;
		}
		else
		{
			return false;
		}
		
	}
	public boolean planMatch(int matchId, String time, int arenaId) throws IOException, ClassNotFoundException {
		socketOpen();
		PrintWriter printWriter = new PrintWriter(socket.getOutputStream(),true);
		String request = "plan-match;"+matchId+";"+time+";"+arenaId;
		printWriter.println(request);
		
		InputStream is = socket.getInputStream();
		ObjectInputStream ois = new ObjectInputStream(is);
		ServerResponse sr = (ServerResponse)ois.readObject();
		if(sr!=null && sr.getResponseType().equals("boolean") && sr.getBoolTypeResponse())
		{
			return true;
		}
		else
		{
			return false;
		}
		
	}
	public CachedRowSet getTournamentCompetitors(int competitionId)throws IOException, ClassNotFoundException {
		socketOpen();
		PrintWriter printWriter = new PrintWriter(socket.getOutputStream(),true);
		String request = "get-competitors;"+competitionId;
		printWriter.println(request);
		
		InputStream is = socket.getInputStream();
		ObjectInputStream ois = new ObjectInputStream(is);
		CachedRowSet crs = (CachedRowSet)ois.readObject();
		if(crs!=null) {
			ClientLog.logLine("INFO", "Pobrano zawodników dla id "+competitionId+".");
		}

		socket.close();
		return crs;
	}
	public CachedRowSet getReports(int competitionId) throws IOException, ClassNotFoundException{
		socketOpen();
		PrintWriter printWriter = new PrintWriter(socket.getOutputStream(),true);
		String request = "get-reports;"+competitionId;
		printWriter.println(request);
		
		InputStream is = socket.getInputStream();
		ObjectInputStream ois = new ObjectInputStream(is);
		CachedRowSet crs = (CachedRowSet)ois.readObject();
		if(crs!=null) {
			ClientLog.logLine("INFO", "Pobrano raporty dla id "+competitionId+".");
		}

		socket.close();
		return crs;
	}
	public CachedRowSet getReportById(int reportId) throws IOException, ClassNotFoundException{
		socketOpen();
		PrintWriter printWriter = new PrintWriter(socket.getOutputStream(),true);
		String request = "get-report-id;"+reportId;
		printWriter.println(request);
		
		InputStream is = socket.getInputStream();
		ObjectInputStream ois = new ObjectInputStream(is);
		CachedRowSet crs = (CachedRowSet)ois.readObject();
		if(crs!=null) {
			ClientLog.logLine("INFO", "Pobrano raport "+reportId+".");
		}

		socket.close();
		return crs;
	}
	public boolean nextStage(int cid) throws IOException, ClassNotFoundException {
		socketOpen();
		PrintWriter printWriter = new PrintWriter(socket.getOutputStream(),true);
		String request = "next-stage;"+cid;
		printWriter.println(request);
		
		InputStream is = socket.getInputStream();
		ObjectInputStream ois = new ObjectInputStream(is);
		ServerResponse sr = (ServerResponse)ois.readObject();
		if(sr!=null && sr.getResponseType().equals("boolean") && sr.getBoolTypeResponse())
		{
			return true;
		}
		else
		{
			return false;
		}
		
	}
	public boolean demoteTeam(int tid, int pid) throws IOException, ClassNotFoundException {
		socketOpen();
		PrintWriter printWriter = new PrintWriter(socket.getOutputStream(),true);
		String request = "demote-team;"+tid+";"+pid;
		printWriter.println(request);
		
		InputStream is = socket.getInputStream();
		ObjectInputStream ois = new ObjectInputStream(is);
		ServerResponse sr = (ServerResponse)ois.readObject();
		if(sr!=null && sr.getResponseType().equals("boolean") && sr.getBoolTypeResponse())
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	public boolean promoteTeam(int tid, int pid) throws IOException, ClassNotFoundException {
		socketOpen();
		PrintWriter printWriter = new PrintWriter(socket.getOutputStream(),true);
		String request = "promote-team;"+tid+";"+pid;
		printWriter.println(request);
		
		InputStream is = socket.getInputStream();
		ObjectInputStream ois = new ObjectInputStream(is);
		ServerResponse sr = (ServerResponse)ois.readObject();
		if(sr!=null && sr.getResponseType().equals("boolean") && sr.getBoolTypeResponse())
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	public int getLeague(int tid, int pid) throws IOException, ClassNotFoundException {
		socketOpen();
		PrintWriter printWriter = new PrintWriter(socket.getOutputStream(),true);
		String request = "get-league;"+tid+";"+pid;
		printWriter.println(request);
		
		InputStream is = socket.getInputStream();
		ObjectInputStream ois = new ObjectInputStream(is);
		ServerResponse sr = (ServerResponse)ois.readObject();
		if(sr!=null && sr.getResponseType().equals("int") && sr.getBoolTypeResponse())
		{
			return sr.getIntTypeResponse();
		}
		else
		{
			return -1;
		}		
	}
	public boolean removeFromTournament(int tid, int pid) throws IOException, ClassNotFoundException {
		socketOpen();
		PrintWriter printWriter = new PrintWriter(socket.getOutputStream(),true);
		String request = "remove-from-tournament;"+tid+";"+pid;
		printWriter.println(request);
		
		InputStream is = socket.getInputStream();
		ObjectInputStream ois = new ObjectInputStream(is);
		ServerResponse sr = (ServerResponse)ois.readObject();
		if(sr!=null && sr.getResponseType().equals("boolean") && sr.getBoolTypeResponse())
		{
			return sr.getBoolTypeResponse();
		}
		else
		{
			return false;
		}	
	}

}
