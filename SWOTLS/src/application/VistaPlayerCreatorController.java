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
public class VistaPlayerCreatorController implements VistaContainable {
    private VistaContainer parent;
    private Map<String, Integer> teams;

    public VistaPlayerCreatorController(VistaContainer parent){
        this.parent = parent;
        FXMLLoader loader = new FXMLLoader(getClass().getResource(VistaNavigator.VISTA_PLAYER_CREATOR));
        loader.setController(this);
        try {
            parent.setVista(loader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.init();
    }

    @FXML private TextField nameField;
    @FXML private TextField surnameField;
    @FXML private TextField nicknameField;
    @FXML private TextField contactField;
    @FXML private TextField languageField;
    @FXML private TextField additionalField;
    @FXML private ComboBox<String> teamBox;

    @Override
    public void setParent(VistaContainer parent) {
        this.parent = parent;
    }
    @Override
    public void init(){
        ObservableList<String> options = FXCollections.observableArrayList();
        teams = ServerData.getListOfAllTeams();
        options.add("** Brak **");
        for(String s : teams.keySet()){
            options.add(s);
        }
        teams.put("** Brak **", -1);
        teamBox.setItems(options);
    }

    @FXML
    void submit(ActionEvent event) {
        if(!VistaLogInController.hasOrganizerPermissions()){
            Dialogs.insufficientPermissions();
            return;
        }

        if(nameField.getText().isEmpty()){
            Dialogs.error("Pole 'Imię' jest wymagane!");
            return;
        }else if(surnameField.getText().isEmpty()) {
            Dialogs.error("Pole 'Nazwisko' jest wymagane!");
            return;
        }
        else if(nicknameField.getText().isEmpty()) {
            Dialogs.error("Pole 'Pseudonim' jest wymagane!");
            return;
        }


        int teamid;
        if(teamBox.getValue()==null){
            teamid = -1;
        }else teamid = teams.get(teamBox.getValue());

        ServerData.newPlayer(nameField.getText(), surnameField.getText(), nicknameField.getText(), contactField.getText(), languageField.getText(), additionalField.getText(), teamid);
        parent.close();
    }

}