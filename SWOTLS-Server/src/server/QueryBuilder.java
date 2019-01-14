package server;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetFactory;
import javax.sql.rowset.RowSetProvider;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class QueryBuilder {
	private String selectionAll = "SELECT * FROM";
	private String ordering = "ORDER BY";
	private String insertion = "INSERT INTO";
	private String[] tableNames = new String[] {"arenas","contestants","matches","system_users","teams","tournaments"};
	private Connection connection=null;
	public QueryBuilder(Connection connection)
	{
		this.connection = connection;
	}
	/**
	 * Drops all of system's tables
	 * @throws SQLException
	 */
	public void dropAllTables() throws SQLException {
		String query="SET FOREIGN_KEY_CHECKS = 0";
		PreparedStatement stmt=connection.prepareStatement(query);
		stmt.execute();
		for(String name : tableNames)
		{
			query="DROP TABLE IF EXISTS "+name;
			stmt=connection.prepareStatement(query);
			stmt.execute();
		}
		query="SET FOREIGN_KEY_CHECKS = 1";
		stmt=connection.prepareStatement(query);
		stmt.execute();
	}
	/**
	 * Creates tables required by the system.
	 * @throws SQLException
	 */
	public void createTables() throws SQLException {
		String [] sql = DatabaseTemplate.GetScript();
		PreparedStatement stmt=null;
		for(String query : sql) {
			stmt = connection.prepareStatement(query);
			stmt.execute();
		}
		
	}
	/**
	 * Inserts given contestant into the database.
	 * @param name
	 * @param surname
	 * @param nickname
	 * @param score contestant's ranking
	 * @param language
	 * @param contact_info
	 * @param additional_info optional
	 * @param team_id set to -1 if null
	 * @return
	 * @throws SQLException
	 */
	public void connectionExceptionTest() throws SQLException{
		String query = "SELECT 1";
		PreparedStatement stmt = connection.prepareStatement(query);
		stmt.executeQuery();
		
	}
	/**
	 * Returns true if login and password hash match a record in the database.
	 * Returns false if password hash doesn't match or there is no user with such login.
	 * @param sysusr system user to verify
	 * @return
	 * @throws SQLException
	 */
	public SystemUser verifySystemLogin(String login, String pw) throws SQLException
	{
		String query = "SELECT * FROM system_users WHERE login = ?";
		PreparedStatement stmt = connection.prepareStatement(query);
		stmt.setString(1, login);
		ResultSet result = stmt.executeQuery();
		if(result.next())
		{
			String savedHash = result.getString("pw_hash");
			int uid = result.getInt("sys_usr_id");
			String perms = result.getString("permissions");
			if(SystemUser.hashString(pw).equals(savedHash))
			{
				return new SystemUser(uid,login,perms);
			}
			else
			{
				return null;
			}
		}
		else
		{
			return null;
		}
	
	}
	public CachedRowSet getTable(String tableName) throws SQLException {
		
		RowSetFactory factory = RowSetProvider.newFactory();
		CachedRowSet crs = factory.createCachedRowSet();
		String query = "SELECT * FROM $tablename ORDER BY 1";
		query =query.replace("$tablename",tableName);
		PreparedStatement stmt = connection.prepareStatement(query);
		ResultSet rs = stmt.executeQuery();
		crs.populate(rs);
		
		return crs;
	}//
	public boolean removeFromTable(String tablename, int id) throws SQLException {
		String idType;
		switch(tablename) {
		case "contestants":
			idType="contestant_id";
			break;
		case "tournaments":
			idType="tournament_id";
			break;
		case "teams":
			idType="team_id";
			break;
		case "arenas":
			idType="arena_id";
			break;
		case "matches":
			idType="matches_id";
			break;
		case "system_users":
			idType="sys_usr_id";
			break;
		default:
			idType="unrecognized";
			break;
		}
		if(idType.equals("unrecognized")) return false;
		String query = "DELETE FROM "+tablename+" WHERE "+idType+"="+id;
		PreparedStatement stmt = connection.prepareStatement(query);
		int rows = stmt.executeUpdate();
		if(rows==1) return true;
		else
			return false;
	}
	public boolean createTournament(String name, String system, String type, String additional, int operator) throws SQLException{
		String query = "INSERT INTO tournaments(name,type,operator,additional_info,system) VALUES (?,?,?,?,?)";
		PreparedStatement stmt = connection.prepareStatement(query);
		stmt.setString(1, name);
		stmt.setString(2, type);
		stmt.setInt(3,operator);
		stmt.setString(4, additional);
		stmt.setInt(5, Integer.valueOf(system));
		System.out.println(type);
		int rows = stmt.executeUpdate();
		if(rows==1) return true;
		else
			return false;
	}
	public boolean createUser(String login, String pw, String perms) throws SQLException {
		String query = "INSERT INTO system_users(login,pw_hash,permissions) VALUES (?,?,?)";
		PreparedStatement stmt = connection.prepareStatement(query);
		stmt.setString(1, login);
		stmt.setString(2, SystemUser.hashString(pw));
		stmt.setString(3, perms);
		int rows = stmt.executeUpdate();
		if(rows==1) return true;
		else
			return false;
		
	}
	public boolean addPlayer(String name, String nickname, String surname, String contact, String language,
			String additional, String teamid) throws SQLException {
		String query;
		if(!teamid.equals("-1"))
		{
			 query = "INSERT INTO contestants(name,nickname,surname,language,contact_info,additional_info,team_id) "
					+ "VALUES(?,?,?,?,?,?,?)";
		}
		else
		{
			 query = "INSERT INTO contestants(name,nickname,surname,language,contact_info,additional_info) "
					+ "VALUES(?,?,?,?,?,?)";
		}
		PreparedStatement stmt = connection.prepareStatement(query);
		stmt.setString(1, name);
		stmt.setString(2, nickname);
		stmt.setString(3, surname);
		stmt.setString(4, language);
		stmt.setString(5, contact);
		if(additional==null||additional.equals(""))
			stmt.setNull(6,java.sql.Types.VARCHAR);
		else
			stmt.setString(6, additional);
		if(!teamid.equals("-1"))
			stmt.setInt(7, Integer.parseInt(teamid));
		int rows = stmt.executeUpdate();
		if(rows==1)
			return true;
		else
			return false;
	}
	public boolean editPlayer(int id, String name, String nickname, String surname, String contact, String language,
			String additional, String teamid) throws SQLException {
		String query = "UPDATE contestants SET name=?,surname=?, nickname=?, language=?, contact_info=?,additional_info=?, team_id=? WHERE contestant_id = "+id;
		PreparedStatement stmt = connection.prepareStatement(query);
		stmt.setString(1, name);
		stmt.setString(2, nickname);
		stmt.setString(3, surname);
		stmt.setString(4, contact);
		stmt.setString(5, language);
		stmt.setString(6, additional);
		if(teamid.equals("-1"))
			stmt.setNull(7, java.sql.Types.INTEGER);
		else
			stmt.setInt(7, Integer.valueOf(teamid));
		int rows = stmt.executeUpdate();
		if(rows==1)
			return true;
		else
			return false;
	}
	public boolean addTeam(String name, String from, int leaderID) throws SQLException {
		String query = "INSERT INTO teams(name,where_from,leader_id) VALUES (?,?,?)";
		PreparedStatement stmt = connection.prepareStatement(query);
		stmt.setString(1, name);
		stmt.setString(2, from);
		stmt.setInt(3, leaderID);
		int rows = stmt.executeUpdate();
		if(rows==1)
			return true;
		else
			return false;
	}
	public boolean editTeam(int id, String name, String from, int leaderID) throws SQLException {
		String query = "UPDATE teams SET name = ?, where_from = ?, leader_id = ? WHERE team_id=?";
		PreparedStatement stmt = connection.prepareStatement(query);
		stmt.setString(1, name);
		stmt.setString(2, from);
		stmt.setInt(3, leaderID);
		stmt.setInt(4, id);
		int rows = stmt.executeUpdate();
		if(rows==1)
			return true;
		else
			return false;
	}
	public boolean newArena(String name, String location) throws SQLException {
		String query = "INSERT INTO arenas(name,location) VALUES (?,?)";
		PreparedStatement stmt = connection.prepareStatement(query);
		stmt.setString(1, name);
		stmt.setString(2, location);
		int rows = stmt.executeUpdate();
		if(rows==1)
			return true;
		else
			return false;
	}
	public boolean editArena(int id,String name, String location) throws SQLException {
		String query = "UPDATE arenas SET name=?,location=? WHERE arena_id=?";
		PreparedStatement stmt = connection.prepareStatement(query);
		stmt.setString(1, name);
		stmt.setString(2, location);
		stmt.setInt(3, id);
		int rows = stmt.executeUpdate();
		if(rows==1)
			return true;
		else
			return false;
	}
	public boolean setScore(int id, int sideA, int sideB) throws SQLException{
		int system = getSystem(getTournamentIdOfMatch(id));
		if(system==1)
		{
			KnockoutTournament.saveResult(id, sideA, sideB);
		}
		else if(system==2)
		{
			SwissTournament.saveResult(id, sideA, sideB);
		}
		else if(system==3)
		{
			 RoundRobinTournament.saveResult(id, sideA, sideB);
		}
		else if(system==4)
		{
			 McMahonTournament.saveResult(id, sideA, sideB);
		}
		else if(system==5)
		{
			 LeagueTournament.saveResult(id, sideA, sideB);
		}
		else
			System.out.println("Nie rozpoznano systemu");
		String query = "UPDATE matches SET sideA_score = ? , sideB_score = ? WHERE match_id = ?";
		PreparedStatement stmt = connection.prepareStatement(query);
		stmt.setInt(1, sideA);
		stmt.setInt(2, sideB);
		stmt.setInt(3, id);
		int rows = stmt.executeUpdate();
		if(rows==1)
			return true;
		else
			return false;
	}
	
	public boolean demotePlayer(int tid, int cid) throws SQLException {
		String query = "SELECT * FROM `contestant-tournament` WHERE contestant_id=? AND tournament_id=?";
		PreparedStatement stmt=connection.prepareStatement(query);
		stmt.setInt(1, cid);
		stmt.setInt(2, tid);
		ResultSet rs = stmt.executeQuery();
		int currentLeague;
		if(rs.next()) {
			currentLeague = rs.getInt("league");
			query = "UPDATE `contestant-tournament` SET league=? WHERE contestant_id=? AND tournament_id=?";
			PreparedStatement update=connection.prepareStatement(query);
			update.setInt(1, currentLeague+1);
			update.setInt(2, cid);
			update.setInt(3, tid);
			int rows=update.executeUpdate();
			if(rows==1)
				return true;
			else
				return false;
		}
		else
			return false;
		
	}
	public boolean promotePlayer(int tid, int cid) throws SQLException {
		String query = "SELECT * FROM `contestant-tournament` WHERE contestant_id=? AND tournament_id=?";
		PreparedStatement stmt=connection.prepareStatement(query);
		stmt.setInt(1, cid);
		stmt.setInt(2, tid);
		ResultSet rs = stmt.executeQuery();
		int currentLeague;
		if(rs.next()) {
			currentLeague = rs.getInt("league");
			if(currentLeague-1<1) return false; // 1 to najwyższa liga
			query = "UPDATE `contestant-tournament` SET league=? WHERE contestant_id=? AND tournament_id=?";
			PreparedStatement update=connection.prepareStatement(query);
			update.setInt(1, currentLeague-1);
			update.setInt(2, cid);
			update.setInt(3, tid);
			int rows=update.executeUpdate();
			if(rows==1)
				return true;
			else
				return false;
		}
		else
			return false;
		
	}
	public CachedRowSet getPlannedMatches() throws SQLException {
		String query = "SELECT * FROM matches WHERE time IS NOT NULL AND sideA_score IS NULL";
		PreparedStatement stmt=connection.prepareStatement(query);
		ResultSet rs = stmt.executeQuery();
		RowSetFactory factory = RowSetProvider.newFactory();
		CachedRowSet crs = factory.createCachedRowSet();
		crs.populate(rs);
		return crs;
	}
	public CachedRowSet getUnplannedMatches(int tid) throws SQLException {
		String query = "SELECT * FROM matches WHERE time IS NULL AND tournament=?";
		PreparedStatement stmt=connection.prepareStatement(query);
		stmt.setInt(1, tid);
		ResultSet rs = stmt.executeQuery();
		RowSetFactory factory = RowSetProvider.newFactory();
		CachedRowSet crs = factory.createCachedRowSet();
		crs.populate(rs);
		return crs;
	}
	public CachedRowSet getFinishedMatches() throws SQLException {
		String query = "SELECT * FROM matches WHERE time IS NOT NULL AND sideA_score IS NOT NULL";
		PreparedStatement stmt=connection.prepareStatement(query);
		ResultSet rs = stmt.executeQuery();
		RowSetFactory factory = RowSetProvider.newFactory();
		CachedRowSet crs = factory.createCachedRowSet();
		crs.populate(rs);
		return crs;
	}
	public CachedRowSet getFinishedMatchesById(int tid) throws SQLException {
		String query = "SELECT * FROM matches WHERE time IS NOT NULL AND sideA_score IS NOT NULL AND tournament = ?";
		PreparedStatement stmt=connection.prepareStatement(query);
		stmt.setInt(1, tid);
		ResultSet rs = stmt.executeQuery();
		RowSetFactory factory = RowSetProvider.newFactory();
		CachedRowSet crs = factory.createCachedRowSet();
		crs.populate(rs);
		return crs;
	}
	public CachedRowSet getReportsList(int tid) throws SQLException {
		String query = "SELECT * FROM reports WHERE tournament_id=?";
		PreparedStatement stmt=connection.prepareStatement(query);
		stmt.setInt(1, tid);
		ResultSet rs = stmt.executeQuery();
		RowSetFactory factory = RowSetProvider.newFactory();
		CachedRowSet crs = factory.createCachedRowSet();
		crs.populate(rs);
		return crs;
	}
	public CachedRowSet getReport(int rid) throws SQLException {
		String query = "SELECT * FROM reports WHERE report_id=?";
		PreparedStatement stmt=connection.prepareStatement(query);
		stmt.setInt(1, rid);
		ResultSet rs = stmt.executeQuery();
		RowSetFactory factory = RowSetProvider.newFactory();
		CachedRowSet crs = factory.createCachedRowSet();
		crs.populate(rs);
		return crs;
	}
	public boolean addToTournament(int pid, int tid) throws SQLException{
		String query = "INSERT INTO `contestant-tournament`(contestant_id,tournament_id) VALUES (?,?)";
		PreparedStatement stmt = connection.prepareStatement(query);
		stmt.setInt(1, pid);
		stmt.setInt(2, tid);
		int rows = stmt.executeUpdate();
		if(rows==1)
			return true;
		else
			return false;
	}
	public CachedRowSet getPlayerTournamentStats(int tid, int pid) throws SQLException {
		String query = "SELECT * FROM `contestant-tournament` WHERE contestant_id=? AND tournament_id=?";
		PreparedStatement stmt=connection.prepareStatement(query);
		stmt.setInt(1, pid);
		stmt.setInt(2, tid);
		ResultSet rs = stmt.executeQuery();
		RowSetFactory factory = RowSetProvider.newFactory();
		CachedRowSet crs = factory.createCachedRowSet();
		crs.populate(rs);
		return crs;
	}
	public boolean setNumberOfRounds(int tid, int rounds) throws SQLException{
		String query = "UPDATE tournaments SET rounds=? WHERE tournament_id=?";
		PreparedStatement stmt = connection.prepareStatement(query);
		stmt.setInt(1, rounds);
		stmt.setInt(2, tid);

		int rows = stmt.executeUpdate();
		if(rows==1)
			return true;
		else
			return false;
	}
	public CachedRowSet getPlannedMatchesId(int tid) throws SQLException {
		String query = "SELECT * FROM matches WHERE time IS NOT NULL AND sideA_score IS NULL AND tournament=?";
		PreparedStatement stmt=connection.prepareStatement(query);
		stmt.setInt(1, tid);
		ResultSet rs = stmt.executeQuery();
		RowSetFactory factory = RowSetProvider.newFactory();
		CachedRowSet crs = factory.createCachedRowSet();
		crs.populate(rs);
		return crs;
	}
	public String getTournamentType(int tid) throws SQLException {
		String query = "SELECT * FROM tournaments WHERE tournament_id =?";
		PreparedStatement stmt = connection.prepareStatement(query);
		stmt.setInt(1, tid);
		ResultSet rs = stmt.executeQuery();
		if(rs.next()) {
			return rs.getString("type");
		}
		else
			return "fail";
	}
	public String getContestantName(int id) throws SQLException{
		String query = "SELECT * FROM contestants WHERE contestant_id = ?";
		PreparedStatement stmt = connection.prepareStatement(query);
		stmt.setInt(1, id);
		ResultSet rs = stmt.executeQuery();
		if(rs.next()) {
			String name = rs.getString("name");
			String nickname = rs.getString("nickname");
			String lastname = rs.getString("surname");
			String result = name +" '"+nickname+"' "+lastname;
			return result;
		}
		else
		{
			
			return null;
		}
	}
	public String getTeamName(int id) throws SQLException{
		String query = "SELECT * FROM teams WHERE team_id = ?";
		PreparedStatement stmt = connection.prepareStatement(query);
		stmt.setInt(1, id);
		ResultSet rs = stmt.executeQuery();
		if(rs.next()) {
			return rs.getString("name");
		}
		else
			return null;
	}
	public String getTournamentTypeByMatchId(int id) throws SQLException {
		String query = "SELECT * FROM matches WHERE match_id = ?";
		PreparedStatement stmt = connection.prepareStatement(query);
		stmt.setInt(1, id);
		int tid = -1;
		ResultSet rs = stmt.executeQuery();
		if(rs.next()) {
			tid=rs.getInt("tournament");
		}
		else
			return null;
		query = "SELECT * FROM tournaments WHERE tournament_id = ?";
		stmt = connection.prepareStatement(query);
		stmt.setInt(1, tid);
		rs= stmt.executeQuery();
		if(rs.next()) {
			return rs.getString("type");
		}
		else
			return null;
	}
	public boolean addContestantToCompetition(int cid, int tid) throws SQLException {
		String query = "INSERT INTO `contestant-tournament`(contestant_id,tournament_id) VALUES (?,?)";
		PreparedStatement stmt = connection.prepareStatement(query);
		stmt.setInt(1, cid);
		stmt.setInt(2, tid);
		int rows = stmt.executeUpdate();
		if(rows==1)
			return true;
		else
			return false;
	}
	public boolean addTeamToCompetition(int cid, int tid) throws SQLException {
		String query = "INSERT INTO `contestant-tournament`(team_id,tournament_id) VALUES (?,?)";
		PreparedStatement stmt = connection.prepareStatement(query);
		stmt.setInt(1, cid);
		stmt.setInt(2, tid);
		int rows = stmt.executeUpdate();
		if(rows==1)
			return true;
		else
			return false;
	}
	public boolean planMatch(int cid, String time, int aid) throws SQLException {
		String query = "UPDATE matches SET time=?, arena_id=? WHERE match_id=?";
		PreparedStatement stmt = connection.prepareStatement(query);
		stmt.setString(1, time);
		stmt.setInt(2, aid);
		stmt.setInt(3, cid);
		int rows = stmt.executeUpdate();
		if(rows==1)
			return true;
		else
			return false;
	}
	public CachedRowSet getCompetitors(int tid) throws SQLException {
		String query = "SELECT * FROM `contestant-tournament` WHERE tournament_id=?";
		PreparedStatement stmt = connection.prepareStatement(query);
		stmt.setInt(1, tid);
		ResultSet rs = stmt.executeQuery();
		RowSetFactory factory = RowSetProvider.newFactory();
		CachedRowSet crs = factory.createCachedRowSet();
		crs.populate(rs);
		return crs;
	}
	public int getTournamentSystem(int tid) throws SQLException {
		String query = "SELECT * FROM tournaments WHERE tournament_id =?";
		PreparedStatement stmt = connection.prepareStatement(query);
		stmt.setInt(1, tid);
		ResultSet rs = stmt.executeQuery();
		if(rs.next()) {
			return rs.getInt("system");
		}
		else
			return -1;
	}

	
	public boolean setElo(int pid, int elo) throws SQLException {
		String query = "UPDATE contestants SET score = ? WHERE contestant_id = ?";
		PreparedStatement stmt = connection.prepareStatement(query);
		stmt.setInt(1, elo);
		stmt.setInt(2, pid);
		int rows = stmt.executeUpdate();
		if(rows==1)
			return true;
		else
			return false;
	}
	public boolean setTeamPoints(int team, int tid, int score) throws SQLException {
		String query = "UPDATE `contestant-tournament` SET score = ? WHERE tournament_id = ? AND team_id = ?";
		PreparedStatement stmt = connection.prepareStatement(query);
		stmt.setInt(1, score);
		stmt.setInt(2, tid);
		stmt.setInt(3, team);
		int rows = stmt.executeUpdate();
		if(rows==1)
			return true;
		else
			return false;
	}
	public int getTeamPoints(int team, int tid) throws SQLException{
		String query = "SELECT * FROM `contestant-tournament` WHERE tournament_id = ? AND team_id = ?";
		PreparedStatement stmt = connection.prepareStatement(query);
		stmt.setInt(1,tid);
		stmt.setInt(2,team);
		ResultSet rs = stmt.executeQuery();
		if(rs.next())
		{
			return rs.getInt("score");
		}
		else
			return -1;
	}
	public boolean setPlayerPoints(int pid, int tid, int score) throws SQLException {
		String query = "UPDATE `contestant-tournament` SET score = ? WHERE tournament_id = ? AND contestant_id = ?";
		PreparedStatement stmt = connection.prepareStatement(query);
		stmt.setInt(1, score);
		stmt.setInt(2, tid);
		stmt.setInt(3, pid);
		int rows = stmt.executeUpdate();
		if(rows==1)
			return true;
		else
			return false;
	}
	public int getPlayerPoints(int pid, int tid) throws SQLException{
		String query = "SELECT * FROM `contestant-tournament` WHERE tournament_id = ? AND contestant_id = ?";
		PreparedStatement stmt = connection.prepareStatement(query);
		stmt.setInt(1,tid);
		stmt.setInt(2,pid);
		ResultSet rs = stmt.executeQuery();
		if(rs.next())
		{
			return rs.getInt("score");
		}
		else
			return -1;
	}
	public boolean setStage(int tid, int stage) throws SQLException {
		String query = "UPDATE `tournament` SET stage = ? WHERE tournament_id = ?";
		PreparedStatement stmt = connection.prepareStatement(query);
		stmt.setInt(1, stage);
		stmt.setInt(2, tid);
		int rows = stmt.executeUpdate();
		if(rows==1)
			return true;
		else
			return false;
	}
	public int getStage(int tid) throws SQLException{
		String query = "SELECT * FROM `tournaments` WHERE tournament_id = ?";
		PreparedStatement stmt = connection.prepareStatement(query);
		stmt.setInt(1,tid);
		ResultSet rs = stmt.executeQuery();
		if(rs.next())
		{
			return rs.getInt("stage");
		}
		else
			return -1;
	}
	public boolean setSeason(int tid, int season) throws SQLException {
		String query = "UPDATE `tournament` SET season = ? WHERE tournament_id = ?";
		PreparedStatement stmt = connection.prepareStatement(query);
		stmt.setInt(1, season);
		stmt.setInt(2, tid);
		int rows = stmt.executeUpdate();
		if(rows==1)
			return true;
		else
			return false;
	}
	public int getSeason(int tid) throws SQLException{
		String query = "SELECT * FROM `tournaments` WHERE tournament_id = ?";
		PreparedStatement stmt = connection.prepareStatement(query);
		stmt.setInt(1,tid);
		ResultSet rs = stmt.executeQuery();
		if(rs.next())
		{
			return rs.getInt("season");
		}
		else
			return -1;
	}
	public boolean createReport(int tid, String title, String content) throws SQLException {
		String query = "INSERT INTO `reports`(title,content,tournament_id,report_time) VALUES (?,?,?,NOW())";
		PreparedStatement stmt = connection.prepareStatement(query);
		
		stmt.setString(1, title);
		stmt.setString(2, content);
		stmt.setInt(3, tid);
		int rows = stmt.executeUpdate();
		if(rows==1)
			return true;
		else
			return false;
		
	}
	public boolean promoteTeam(int tid, int cid) throws SQLException {
		String query = "SELECT * FROM `contestant-tournament` WHERE team_id=? AND tournament_id=?";
		PreparedStatement stmt=connection.prepareStatement(query);
		stmt.setInt(1, cid);
		stmt.setInt(2, tid);
		ResultSet rs = stmt.executeQuery();
		int currentLeague;
		if(rs.next()) {
			currentLeague = rs.getInt("league");
			if(currentLeague-1<1) return false; // 1 to najwyższa liga
			query = "UPDATE `contestant-tournament` SET league=? WHERE team_id=? AND tournament_id=?";
			PreparedStatement update=connection.prepareStatement(query);
			update.setInt(1, currentLeague-1);
			update.setInt(2, cid);
			update.setInt(3, tid);
			int rows=update.executeUpdate();
			if(rows==1)
				return true;
			else
				return false;
		}
		else
			return false;
		
	}
	public boolean demoteTeam(int tid, int cid) throws SQLException {
		String query = "SELECT * FROM `contestant-tournament` WHERE team_id=? AND tournament_id=?";
		PreparedStatement stmt=connection.prepareStatement(query);
		stmt.setInt(1, cid);
		stmt.setInt(2, tid);
		ResultSet rs = stmt.executeQuery();
		int currentLeague;
		if(rs.next()) {
			currentLeague = rs.getInt("league");
			query = "UPDATE `contestant-tournament` SET league=? WHERE team_id=? AND tournament_id=?";
			PreparedStatement update=connection.prepareStatement(query);
			update.setInt(1, currentLeague+1);
			update.setInt(2, cid);
			update.setInt(3, tid);
			int rows=update.executeUpdate();
			if(rows==1)
				return true;
			else
				return false;
		}
		else
			return false;
		
	}
	public int getLeague(int tid, int pid) throws SQLException{
		String query = "SELECT * FROM `contestant-tournament` WHERE contestant_id = ? AND tournament_id =?";
		PreparedStatement stmt = connection.prepareStatement(query);
		stmt.setInt(1, pid);
		stmt.setInt(2, tid);
		ResultSet rs = stmt.executeQuery();
		if(rs.next()) {
			int league = rs.getInt("league");
			return league;
		}
		else
			return -1;
	}
	public CachedRowSet getWinners(int tid) throws SQLException {
		String query = "SELECT * FROM `contestant-tournament` WHERE tournament_id=? AND score <> -1";
		PreparedStatement stmt = connection.prepareStatement(query);
		stmt.setInt(1, tid);
		ResultSet rs = stmt.executeQuery();
		RowSetFactory factory = RowSetProvider.newFactory();
		CachedRowSet crs = factory.createCachedRowSet();
		crs.populate(rs);
		return crs;
	}
	public CachedRowSet getLosers(int tid) throws SQLException {
		String query = "SELECT * FROM `contestant-tournament` WHERE tournament_id=? AND score = -1";
		PreparedStatement stmt = connection.prepareStatement(query);
		stmt.setInt(1, tid);
		ResultSet rs = stmt.executeQuery();
		RowSetFactory factory = RowSetProvider.newFactory();
		CachedRowSet crs = factory.createCachedRowSet();
		crs.populate(rs);
		return crs;
	}
	public Player getPlayer(int pid) throws SQLException {
		String query = "SELECT * FROM contestants WHERE contestant_id=?";
		PreparedStatement stmt = connection.prepareStatement(query);
		stmt.setInt(1, pid);
		ResultSet rs = stmt.executeQuery();
		if(rs.next()) {
			int id = rs.getInt("contestant_id");
			String name = rs.getString("name");
			String surname = rs.getString("surname");
			String nickname = rs.getString("nickname");
			int score = rs.getInt("score");
			String language = rs.getString("language");
			String contact_info=rs.getString("contact_info");
			String additional_info=rs.getString("additional_info");
			int team_id = rs.getInt("team_id");
			return new Player(id,name,surname,nickname,score,language,contact_info,additional_info,team_id);
		}
		else
			return null;
	}
	public Team getTeam(int tid) throws SQLException {
		String query = "SELECT * FROM teams WHERE team_id=?";
		PreparedStatement stmt = connection.prepareStatement(query);
		stmt.setInt(1, tid);
		ResultSet rs = stmt.executeQuery();
		if(rs.next()) {
			int id = rs.getInt("team_id");
			String name = rs.getString("name");
			String where_from = rs.getString("where_from");
			int leader_id = rs.getInt("leader_id");
			ArrayList<Player> members = getTeamPlayers(id);
			Player leader = getPlayer(leader_id);
			return new Team(id, name, where_from,leader,members);
		}
		else
			return null;
	}
	public ArrayList<Player> getTeamPlayers(int id) throws SQLException {
		ArrayList<Player> list =new ArrayList<Player>();
		String query = "SELECT * FROM contestants WHERE team_id=?";
		PreparedStatement stmt = connection.prepareStatement(query);
		stmt.setInt(1, id);
		ResultSet rs = stmt.executeQuery();
		while(rs.next()) {
			int pid = rs.getInt("contestant_id");
			String name = rs.getString("name");
			String surname = rs.getString("surname");
			String nickname = rs.getString("nickname");
			int score = rs.getInt("score");
			String language = rs.getString("language");
			String contact_info=rs.getString("contact_info");
			String additional_info=rs.getString("additional_info");
			int team_id = rs.getInt("team_id");
			list.add(new Player(pid,name,surname,nickname,score,language,contact_info,additional_info,team_id));
		}
		return list;
	}
	public boolean allFinished(int tid) throws SQLException {
		boolean plannedExist=true;
		boolean unplannedExist = true;
		CachedRowSet planned = getPlannedMatchesId(tid);
		if(!planned.next())
			plannedExist=false;
		CachedRowSet unplanned = getUnplannedMatches(tid);
		if(!unplanned.next())
			unplannedExist=false;
		if(plannedExist || unplannedExist) {
			return false;
		}
		else
			return true;
	}
	public boolean nextStage(int tid) throws SQLException {
		int system = getSystem(tid);
		if(system==1)
		{
			return KnockoutTournament.nextStage(tid);
		}
		else if(system==2)
		{
			return SwissTournament.nextStage(tid);
		}
		else if(system==3)
		{
			return RoundRobinTournament.nextStage(tid);
		}
		else if(system==4)
		{
			return McMahonTournament.nextStage(tid);
		}
		else if(system==5)
		{
			return LeagueTournament.nextStage(tid);
		}
		else
			return false;
	}
	public int getSystem(int tid) throws SQLException{
		String query = "SELECT * FROM tournaments WHERE tournament_id = ?";
		PreparedStatement stmt = connection.prepareStatement(query);
		stmt.setInt(1, tid);
		ResultSet rs = stmt.executeQuery();
		if(rs.next()) {
			int system = rs.getInt("system");
			return system;
		}
		else
			return -1;
	}
	public int getTournamentIdOfMatch(int mid) throws SQLException{
		String query = "SELECT * FROM matches WHERE match_id = ?";
		PreparedStatement stmt = connection.prepareStatement(query);
		stmt.setInt(1, mid);
		ResultSet rs = stmt.executeQuery();
		if(rs.next()) {
			int tournament = rs.getInt("tournament");
			return tournament;
		}
		else
			return -1;
	}
	public Player getLeaderOfTeam(int tid) throws SQLException{
		String query = "SELECT * FROM contestants WHERE team_id = ?";
		PreparedStatement stmt = connection.prepareStatement(query);
		stmt.setInt(1, tid);
		ResultSet rs = stmt.executeQuery();
		if(rs.next()) {
			return getPlayer(rs.getInt("contestant_id"));
		}
		else return null;
	}
	public Match getMatchById(int mid) throws SQLException{
		String query = "SELECT * FROM matches WHERE match_id = ?";
		PreparedStatement stmt = connection.prepareStatement(query);
		String type = getTournamentTypeByMatchId(mid);
		stmt.setInt(1, mid);
		ResultSet rs = stmt.executeQuery();
		if(rs.next()) {
			if(type.equals("solo")) {
				int tournament = rs.getInt("tournament");
				int sideA = rs.getInt("sideA");
				int sideB = rs.getInt("sideB");
				int sideA_score = rs.getInt("sideA_score");
				int sideB_score=rs.getInt("sideB_score");
				Player a = getPlayer(sideA);
				Player b = getPlayer(sideB);
				return new Match(mid, a, b, tournament);
			}
			else if(type.equals("team")) {
				int tournament = rs.getInt("tournament");
				int sideA = rs.getInt("teamA");
				int sideB = rs.getInt("teamB");;
				int sideA_score = rs.getInt("sideA_score");
				int sideB_score=rs.getInt("sideB_score");
				Team teamA = getTeam(sideA);
				Team teamB = getTeam(sideB);
				
				return new Match(mid, teamA, teamB, tournament);
			}
		}
		else
			return null;
		return null;
	}
}
