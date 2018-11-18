package application;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

public class DatabaseHandler {
	private Connection connect = null;
    private Statement statement = null;
    private PreparedStatement preparedStatement = null;
    private ResultSet resultSet = null;
    
    private String address="sql.sdonchor.nazwa.pl";
    private String username="sdonchor_SWOTLS-DB";
    private String password="";
    private String dbName="sdonchor_SWOTLS-DB";
    private int port=3306;
    
    
	public DatabaseHandler() {
		try {
			MysqlDataSource dataSource = new MysqlDataSource();
			dataSource.setUser(username);
			dataSource.setPassword(password);
			dataSource.setServerName(address);
			dataSource.setPort(port);
			dataSource.setDatabaseName(dbName);
			
			Connection conn = dataSource.getConnection();
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM contestants");
			while(rs.next()){
				System.out.println("cID="+rs.getInt("contestant_id")+", Name="+rs.getString("name"));
			}
				
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}
}
