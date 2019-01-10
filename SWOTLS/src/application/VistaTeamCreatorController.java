package application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.util.Map;

/**
 * Controller class for the login vista.
 */
public class VistaTeamCreatorController implements VistaContainable {
    private VistaContainer parent;
    private Map<String, Integer> players;

    public VistaTeamCreatorController(VistaContainer parent){
        this.parent = parent;
        FXMLLoader loader = new FXMLLoader(getClass().getResource(VistaNavigator.VISTA_TEAM_CREATOR));
        loader.setController(this);
        try {
            parent.setVista(loader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.init();
    }

    @FXML private TextField nameField;
    @FXML private TextField fromField;
    @FXML private ComboBox<String> leaderBox;

    @Override
    public void setParent(VistaContainer parent) {
        this.parent = parent;
    }
    @Override
    public void init(){
        ObservableList<String> options = FXCollections.observableArrayList();
        players = ServerData.getListOfAllContestants();
        for(String s : players.keySet()){
            options.add(s);
        }
        leaderBox.setItems(options);
    }

    @FXML
    void submit(ActionEvent event) {
        if(!VistaLogInController.hasOrganizerPermissions()){
            Dialogs.insufficientPermissions();
            return;
        }

        if(nameField.getText().isEmpty()){
            Dialogs.error("Pole 'Nazwa' jest wymagane!");
            return;
        }

        if(leaderBox.getValue()==null){
            Dialogs.error("Pole 'Lider' nie może być puste! Wybierz lidera drużyny.");
            return;
        }

        ServerData.newTeam(nameField.getText(), fromField.getText(), players.get(leaderBox.getValue()));
        parent.close();
    }

}