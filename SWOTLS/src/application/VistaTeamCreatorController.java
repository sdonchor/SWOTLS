package application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
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
    private int editId = -1;
    private int leaderId = -1;

    public VistaTeamCreatorController(VistaContainer parent){
        this.init(parent);
    }

    public VistaTeamCreatorController(VistaContainer parent, Team t){
        this.init(parent);
        this.editId = t.getId();
        Player leader = t.getLeader();
        if(leader!=null)
            this.leaderId = leader.getId();
        actionButton.setText("Zapisz");
        nameField.setText(t.getName());
        fromField.setText(t.getWhereFrom());

        Player p = t.getLeader();
        if(p!=null)
            leaderBox.setValue(p.displayedName());
    }

    @FXML private TextField nameField;
    @FXML private TextField fromField;
    @FXML private ComboBox<String> leaderBox;
    @FXML private Button actionButton;

    @Override
    public void init(VistaContainer parent){
        this.parent = parent;
        FXMLLoader loader = new FXMLLoader(getClass().getResource(VistaNavigator.VISTA_TEAM_CREATOR));
        loader.setController(this);
        try {
            parent.setVista(loader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }

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

        if(editId==-1)
            ServerData.newTeam(nameField.getText(), fromField.getText(), players.getOrDefault(leaderBox.getValue(), leaderId));
        else
            ServerData.editTeam(editId, nameField.getText(), fromField.getText(), players.getOrDefault(leaderBox.getValue(), leaderId));

        parent.close();
    }
}