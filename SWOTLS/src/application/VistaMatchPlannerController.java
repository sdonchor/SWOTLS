package application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * Controller class for the login vista.
 */
public class VistaMatchPlannerController implements VistaContainable {
    private VistaContainer parent;
    private Map<String, Integer> arenas;
    private int editId = -1;

    public VistaMatchPlannerController(VistaContainer parent, int matchId){
        this.init(parent);
        this.editId = matchId;
    }

    public VistaMatchPlannerController(VistaContainer parent, Match match){
        this.init(parent);
        this.editId = match.getId();
        actionButton.setText("Zapisz");

        Arena a = match.getArena();
        if(a!=null)
            arenaBox.setValue(a.getName());
        else
            arenaBox.setValue("** Nieokreślona **");
    }

    @FXML private ComboBox<String> arenaBox;
    @FXML private DateTimePicker datePicker;
    @FXML private Button actionButton;

    @Override
    public void init(VistaContainer parent){
        this.parent = parent;
        FXMLLoader loader = new FXMLLoader(getClass().getResource(VistaNavigator.VISTA_MATCH_PLANNER));
        loader.setController(this);
        try {
            parent.setVista(loader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }

        ObservableList<String> options = FXCollections.observableArrayList();
        arenas = ServerData.getListOfAllArenas();
        for(String s : arenas.keySet()){
            options.add(s);
        }
        arenaBox.setItems(options);

    }

    @FXML
    void submit(ActionEvent event) {
        if(!VistaLogInController.hasOrganizerPermissions()){
            Dialogs.insufficientPermissions();
            return;
        }

        int arenaId;
        if(arenaBox.getValue()!=null)
            arenaId = arenas.get(arenaBox.getValue());
        else{
            Dialogs.error("Wybierz arene!");
            return;
        }

        LocalDateTime localDate = datePicker.getDateTimeValue();
        ServerData.planMatch(editId, localDate, arenaId);

        parent.close();
    }
}