package application;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;

import java.util.Map;

/**
 * Main controller class for the entire layout.
 */
public class MainController extends VistaContainer  {
    private static TabContainer tabContainer;
    /** Holder of a switchable vista. */
    @FXML
    private StackPane vistaHolder;

    /**
     * Replaces the vista displayed in the vista holder with a new vista.
     *
     * @param node the vista node to be swapped in.
     */
    @Override
    public void setVista(Node node) { vistaHolder.getChildren().setAll(node); }

    @FXML
    private void closeAppAction(ActionEvent event) {
        Platform.exit();
    }

    public static void setTabContainer(TabContainer toSet) {
        ServertriggeredEvents.clearDataUpdateListeners();
        tabContainer = toSet;
    }

    public static TabController newTab(String title){
        return tabContainer.newTab(title);
    }

    public static void openLogInTab(){
        new VistaLogInController(tabContainer.newTab("Zaloguj się"));
    }

    @FXML
    private void openLogInTab(ActionEvent event){
        new VistaLogInController(tabContainer.newTab("Zaloguj się"));
    }

    @FXML
    private void openTimetable(ActionEvent event){
        String s = "Wszystkie niezakończone, ale zaplanowane mecze w federacji:\n";
        Map<String, Integer> matches = ServerData.getListOfAllPlannedMatches();
        for(String match : matches.keySet()){
            s += match + "\n";
        }
        new VistaReportViewerController(tabContainer.newTab("Terminarz"), s);
    }

    @FXML
    private void goToFederationView(ActionEvent event){
        new VistaFederationController(this);
    }

    public void close() { Platform.exit(); }
}