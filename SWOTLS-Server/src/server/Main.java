package server;


import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;


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
		Tournament.setDbh(db);
		
		//DB TEST END
		
		//SERVER TEST BEGIN
		Server server = new Server(db);
		try {
			server.runServer();
		} catch (IOException e1) {
			System.out.println("Couldn't run server.");
			ServerLog.logLine("ERROR", "Nie udało się uruchomić serwera.");
		}
		
		//SERVER TEST END
		
		
	}
}
