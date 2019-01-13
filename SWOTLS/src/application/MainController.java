package application;

import com.sun.security.ntlm.Server;
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
        if(tabContainer==null)
            Platform.exit();
        else{
            ServerData.logOut();
            new VistaConnectController(this);
            setTabContainer(null);
        }
    }

    public static void setTabContainer(TabContainer toSet) {
        ServertriggeredEvents.clearDataUpdateListeners();
        tabContainer = toSet;
    }

    public static TabController newTab(String title){
        if(tabContainer==null){
            Dialogs.error("Nie udało się otworzyć karty. Prawdopodobnie brak połączenia z serwerem.");
            new VistaConnectController(Main.getMainController());
            return new TabController(title);
        }
        return tabContainer.newTab(title);
    }

    public static void openLogInTab(){
        new VistaLogInController(newTab("Zaloguj się"));
    }

    @FXML
    private void openLogInTab(ActionEvent event){
        new VistaLogInController(newTab("Zaloguj się"));
    }

    @FXML
    private void openTimetable(ActionEvent event){
        String s = "Wszystkie niezakończone, ale zaplanowane mecze w federacji:\n";
        Map<String, Integer> matches = ServerData.getListOfAllPlannedMatches();
        for(String match : matches.keySet()){
            s += match + "\n";
        }
        s +="\nAby zaplanować mecz, przejdź do perspektywy wybranego wydarzenia i wybierz niezaplanowany mecz z listy (rozwijanego panelu) po lewej stronie.";
        new VistaReportViewerController(newTab("Terminarz"), s);
    }

    @FXML
    public void goToFederationView(){
        if(ServerData.getServerConnection()==null) {
            Dialogs.error("Brak połączenia z serwerem.");
            return;
        }

        new VistaFederationController(this);
    }

    public void close() {
        Platform.exit();
    }
}