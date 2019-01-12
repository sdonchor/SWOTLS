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
	public int contestantInsertion(Player contestant) throws SQLException
	{
		
		String query=null;
		query = insertion + " contestants VALUES (default, ?, ?, ?, ?, ?, ?, ?, ?)";
		PreparedStatement stmt = connection.prepareStatement(query);
		stmt.setString(1,contestant.getName());
		stmt.setString(2,contestant.getSurname());
		stmt.setString(3,contestant.getNickname());
		stmt.setInt(4,contestant.getElo());
		stmt.setString(5,contestant.getLanguage());
		stmt.setString(6,contestant.getContact_info());
		stmt.setString(7,contestant.getAdditional_info());
		if(contestant.getTeam_id()!=-1)
			stmt.setInt(8,contestant.getTeam_id());
		else
			stmt.setNull(8, contestant.getTeam_id());
		return stmt.executeUpdate();	
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
				return new SystemUser(uid,login,perms);
			else
				return null;
		}
		else
			return null;
	
	}
	public CachedRowSet getTable(String tableName) throws SQLException {
		RowSetFactory factory = RowSetProvider.newFactory();
		CachedRowSet crs = factory.createCachedRowSet();
		String query = "SELECT * FROM $tablename";
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
		String query = "INSERT INTO tournaments(name,type,operator,additional_info,system) VALUES ('$name','$type',$operator,'$additional',$system)";
		query = query.replace("$name",name);
		query = query.replace("$type",type);
		query = query.replace("$operator",Integer.toString(operator));
		query = query.replace("$additional",additional);
		query = query.replace("$system",system);
		
		PreparedStatement stmt = connection.prepareStatement(query);
		int rows = stmt.executeUpdate();
		if(rows==1) return true;
		else
			return false;
	}
	public boolean createUser(String login, String pw, String perms) throws SQLException {
		String query = "INSERT INTO system_users(login,pw_hash,permissions) VALUES ('$login','$pw_hash','$perms')";
		query = query.replace("$login", login);
		query = query.replace("$pw_hash", SystemUser.hashString(pw));
		query = query.replace("$perms", perms);
		PreparedStatement stmt = connection.prepareStatement(query);
		int rows = stmt.executeUpdate();
		if(rows==1) return true;
		else
			return false;
		
	}
}
