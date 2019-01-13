package server;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseHandler {
	private Connection connect = null;
    private String address="sql.sdonchor.nazwa.pl";
    private String username="sdonchor_SWOTLS-DB";
    private String password="";
    private String dbName="sdonchor_SWOTLS-DB";
    private int port=3306;
    private QueryBuilder queryBuilder = null;
    
	public DatabaseHandler(String address,String username,String password,String dbName,int port) {
		this.address=address;
		this.username=username;
		this.password=password;
		this.dbName=dbName;
		this.port=port;	
	}
	/**
	 * Returns handler's query builder object
	 * @return
	 */
	public QueryBuilder getQueryBuilder() {
		if(!isConnected())
		{
			System.out.println("Database disconnected, attempting reconnection...");
			connect();
		}
		return queryBuilder;
	}
	public boolean isConnected() {
		try {
			queryBuilder.connectionExceptionTest();
			return true;
		} catch (SQLException e) {
			
			return false;
		}
	}
	/**
	 * Connects to the database.
	 */
	public void connect() {
		try {
			MysqlDataSource dataSource = new MysqlDataSource();
			dataSource.setUser(username);
			dataSource.setPassword(password);
			dataSource.setServerName(address);
			dataSource.setPort(port);
			dataSource.setDatabaseName(dbName);
			
			connect = dataSource.getConnection();
			//sql=connect.createStatement();
			queryBuilder = new QueryBuilder(connect);
			
			
				
		} catch (SQLException e) {
			System.out.println("Failed to connect to DB.");
		//	e.printStackTrace();
		}
	}
	/**
	 * Debug method, prints contents of contestants table.
	 */
	public void printContestants() {
		try {
			Statement stmt = connect.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM contestants");
			while(rs.next()){
				System.out.println("cID="+rs.getInt("contestant_id")+", Name="+rs.getString("name"));
			}
		} catch (SQLException e) {
			System.out.println("DB ERROR");
		}
	}
	/**
	 * Prepares the database for system's use. Removes all data if exists.
	 */
	public void formatDatabase() {
		ServerLog.logLine("INFO", "Formatowanie bazy danych.");
		try {
			getQueryBuilder().dropAllTables();
			getQueryBuilder().createTables();
			ServerLog.logLine("INFO","Utworzono pustą bazę danych na podstawie szablonu.");
		} catch (SQLException e) {
			System.out.println("Couldn't format the database.");
			ServerLog.logLine("ERROR", "Formatowanie nie udało się. Błąd bazy danych.");
			//e.printStackTrace();
		}
	}
	/**
	 * Closes database connection.
	 */
	public void closeConnection() {
		try {
			connect.close();
		} catch (SQLException e) {
			System.out.println("Failed to close DB connection.");
		//	e.printStackTrace();
		}
	}
}
