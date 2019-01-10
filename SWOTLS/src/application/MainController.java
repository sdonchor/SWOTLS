package application;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

import java.util.ArrayList;
import java.util.List;

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
        tabContainer = toSet;
    }

    public static void openLogInTab(){
        VistaNavigator.loadVista(VistaNavigator.VISTA_LOGIN, tabContainer.newTab("Zaloguj się"));
    }

    @FXML
    private void openLogInTab(ActionEvent event){
        VistaNavigator.loadVista(VistaNavigator.VISTA_LOGIN, tabContainer.newTab("Zaloguj się"));
    }

    @FXML
    private void openTimetable(ActionEvent event){
        //TODO VistaNavigator.loadVista(VistaNavigator.TIMETABLE, tabContainer.newTab("Terminarz"));
    }

    @FXML
    private void goToFederationView(ActionEvent event){
        VistaNavigator.loadVista(VistaNavigator.VISTA_FEDERATION, this);
    }

    public void close() { Platform.exit(); }
}