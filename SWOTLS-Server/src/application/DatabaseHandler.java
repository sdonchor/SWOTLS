package application;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseHandler {
	private Connection connect = null;
    private Statement statement = null;
    private PreparedStatement preparedStatement = null;
    private ResultSet resultSet = null;
    private String address;
    private String username;
    private String password;
    private String port;
    
    
	public DatabaseHandler() {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			String connectionString="jdbc:mysql://"+address+":"+port+"/feedback?user="+username+"&password="+password;
			connect = DriverManager
	                .getConnection(connectionString);
		} catch (ClassNotFoundException | SQLException | InstantiationException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}
}
