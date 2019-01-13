package server;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetFactory;
import javax.sql.rowset.RowSetProvider;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
		String query = "UPDATE teams SET name = ?, from = ?, leaderID = ? WHERE team_id=?";
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
		String query = "INSERT INTO teams(name,location) VALUES (?,?)";
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
	public CachedRowSet getUnplannedMatches() throws SQLException {
		String query = "SELECT * FROM matches WHERE time IS NULL";
		PreparedStatement stmt=connection.prepareStatement(query);
		ResultSet rs = stmt.executeQuery();
		RowSetFactory factory = RowSetProvider.newFactory();
		CachedRowSet crs = factory.createCachedRowSet();
		crs.populate(rs);
		return crs;
	}
	public boolean demotePlayer(int tid, int cid) throws SQLException {
		String query = "SELECT * FROM contestant-tournament WHERE contestant_id=? AND tournament_id=?";
		PreparedStatement stmt=connection.prepareStatement(query);
		stmt.setInt(1, cid);
		stmt.setInt(2, tid);
		ResultSet rs = stmt.executeQuery();
		int currentLeague;
		if(rs.next()) {
			currentLeague = rs.getInt("league");
			query = "UPDATE contestant-tournament SET league=? WHERE contestant_id=? AND tournament_id=?";
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
}
