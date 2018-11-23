package application;

import java.sql.Connection;
import java.sql.PreparedStatement;
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
	 * @param score
	 * @param language
	 * @param contact_info
	 * @param additional_info
	 * @param team_id
	 * @return
	 * @throws SQLException
	 */
	public int contestantInsertion(String name, String surname, String nickname, int score, String language, String contact_info, String additional_info,int team_id) throws SQLException
	{
		String query=null;
		query = insertion + " contestants VALUES (default, ?, ?, ?, ?, ?, ?, ?, ?)";
		PreparedStatement stmt = connection.prepareStatement(query);
		stmt.setString(1,name);
		stmt.setString(2,surname);
		stmt.setString(3,nickname);
		stmt.setInt(4,score);
		stmt.setString(5,language);
		stmt.setString(6,contact_info);
		stmt.setString(7,additional_info);
		if(team_id!=-1)
			stmt.setInt(8,team_id);
		else
			stmt.setNull(8, team_id);
		return stmt.executeUpdate();	
	}
}
