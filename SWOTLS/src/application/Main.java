package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Main application class.
 */
public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception{
        stage.setTitle("SWOTLS");

        stage.setScene(
            createScene(
                loadMainPane()
            )
        );

        stage.show();
    }

    /**
     * Loads the main fxml layout.
     * Sets up the vista switching VistaNavigator.
     * Loads the first vista into the fxml layout.
     *
     * @return the loaded pane.
     * @throws IOException if the pane could not be loaded.
     */
    private Pane loadMainPane() throws IOException {
        FXMLLoader loader = new FXMLLoader();

        Pane mainPane = (Pane) loader.load(
            getClass().getResourceAsStream(
                VistaNavigator.MAIN
            )
        );

        MainController mainController = loader.getController();
        VistaNavigator.loadVista(VistaNavigator.VISTA_FEDERATION, mainController);

        return mainPane;
    }

    /**
     * Creates the main application scene.
     *
     * @param mainPane the main application layout.
     *
     * @return the created scene.
     */
    private Scene createScene(Pane mainPane) {
        Scene scene = new Scene(
            mainPane
        );

        scene.getStylesheets().setAll(
            getClass().getResource("/application/vista.css").toExternalForm()
        );

        return scene;
    }

    public static void main(String[] args) {
    	try {
			ServerConnection sc = new ServerConnection("localhost",4545);
			ServerData.convertContestants(sc.getTable("contestants"));
			ServerData.convertTournaments(sc.getTable("tournaments"));
			ServerData.convertTeams(sc.getTable("teams"));
			ServerData.convertMatches(sc.getTable("matches"));
			ServerData.convertArenas(sc.getTable("arenas"));
			ServerData.convertSysUsrs(sc.getTable("system_users"));
			sc.socketClose();
			
		} catch (IOException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
        launch(args);
        
    }
}
