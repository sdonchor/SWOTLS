package application;

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
	public int contestantInsertion(Contestant contestant) throws SQLException
	{
		
		String query=null;
		query = insertion + " contestants VALUES (default, ?, ?, ?, ?, ?, ?, ?, ?)";
		PreparedStatement stmt = connection.prepareStatement(query);
		stmt.setString(1,contestant.getName());
		stmt.setString(2,contestant.getSurname());
		stmt.setString(3,contestant.getNickname());
		stmt.setInt(4,contestant.getScore());
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
	public boolean verifySystemLogin(SystemUser sysusr) throws SQLException
	{
		String query = "SELECT * FROM system_users WHERE login = ?";
		PreparedStatement stmt = connection.prepareStatement(query);
		stmt.setString(1, sysusr.getLogin());
		ResultSet result = stmt.executeQuery();
		if(result.next())
		{
			String savedHash = result.getString("pw_hash");
			if(sysusr.getPw_hash().equals(savedHash))
				return true;
			else
				return false;
		}
		else
			return false;
	
	}
}
