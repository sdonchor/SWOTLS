package application;

import javafx.fxml.FXMLLoader;

import java.io.IOException;

/**
 * Utility class for controlling navigation between vistas.
 *
 * All methods on the navigator are static to facilitate
 * simple access from anywhere in the application.
 */
public class VistaNavigator {

    /**
     * Convenience constants for fxml layouts managed by the navigator.
     */
    public static final String MAIN    = "/application/main.fxml";
    public static final String VISTA_LOGIN = "/application/vistaLogIn.fxml";
    public static final String VISTA_REGISTER = "/application/vistaRegistration.fxml";
    public static final String VISTA_FEDERATION = "/application/vistaFederation.fxml";
    public static final String VISTA_HOLDER = "/application/vistaHolder.fxml";
    public static final String VISTA_ENTRY = "/application/vistaEntryViewer.fxml";
    public static final String VISTA_COMPETITION_CREATOR = "/application/vistaCompetitionCreator.fxml";
    public static final String VISTA_COMPETITION = "/application/vistaCompetition.fxml";
    public static final String VISTA_COMPETITOR_CHOOSER = "/application/vistaCompetitorChooser.fxml";
    public static final String VISTA_PLAYER_CREATOR = "/application/vistaPlayerCreator.fxml";
    public static final String VISTA_TEAM_CREATOR = "/application/vistaTeamCreator.fxml";
    public static final String VISTA_ARENA_CREATOR = "/application/vistaArenaCreator.fxml";

    /**
     * Loads the vista specified by the fxml file into the vistaHolder pane of the main application layout.
     * @param fxml the fxml file to be loaded.
     */
    /*public static void loadVista(String fxml, VistaContainer container) {
        try {
            FXMLLoader loader = new FXMLLoader(VistaNavigator.class.getResource(fxml));
            container.setVista( loader.load() );

            VistaContainable child = loader.getController();
            child.init(container);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/
}