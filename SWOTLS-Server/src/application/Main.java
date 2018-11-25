package application;
	

import java.io.IOException;

import javax.sql.rowset.CachedRowSet;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;


public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			BorderPane root = new BorderPane();
			Scene scene = new Scene(root,400,400);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		//launch(args);
	
		//DB TEST BEGIN
		String address="sql.sdonchor.nazwa.pl";
	    String username="sdonchor_SWOTLS-DB";
	    String password="ZXCasdqwe123";
	    String dbName="sdonchor_SWOTLS-DB";
	    int port=3306;
	    
		DatabaseHandler db = new DatabaseHandler(address,username,password,dbName,port);
		db.connect();
	
		//DB TEST END
		
		//SERVER TEST BEGIN
		Server server = new Server(db);
		try {
			server.runServer();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		//SERVER TEST END
		
		
	}
}
