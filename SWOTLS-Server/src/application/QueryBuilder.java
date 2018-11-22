package application;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class QueryBuilder {
	private String selectionAll = "SELECT * FROM";
	private String ordering = "ORDER BY";
	private String insertion = "INSERT INTO";
	private String insertionValues = "VALUES (";
	private String currQuery="";
	
	private Connection connection=null;
	public QueryBuilder(Connection connection)
	{
		this.connection = connection;
	}
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
