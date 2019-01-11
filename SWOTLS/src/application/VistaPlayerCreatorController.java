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
public class VistaPlayerCreatorController implements VistaContainable {
    private VistaContainer parent;
    private Map<String, Integer> teams;
    private int editId = -1;

    public VistaPlayerCreatorController(VistaContainer parent){
        this.init(parent);
    }

    public VistaPlayerCreatorController(VistaContainer parent, Player player){
        this.init(parent);
        this.editId = player.getId();
        actionButton.setText("Zapisz");
        nameField.setText(player.getName());
        surnameField.setText(player.getSurname());
        nicknameField.setText(player.getNickname());
        contactField.setText(player.getContactInfo());
        languageField.setText(player.getLanguage());
        additionalField.setText(player.getAdditionalInfo());
        Team t = player.getTeam();
        if(t!=null)
            teamBox.setValue(player.getTeam().getName());
        else
            teamBox.setValue("** Brak **");

    }

    @FXML private TextField nameField;
    @FXML private TextField surnameField;
    @FXML private TextField nicknameField;
    @FXML private TextField contactField;
    @FXML private TextField languageField;
    @FXML private TextField additionalField;
    @FXML private ComboBox<String> teamBox;
    @FXML private Button actionButton;

    @Override
    public void init(VistaContainer parent){
        this.parent = parent;
        FXMLLoader loader = new FXMLLoader(getClass().getResource(VistaNavigator.VISTA_PLAYER_CREATOR));
        loader.setController(this);
        try {
            parent.setVista(loader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }

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

        if(editId==-1)
            ServerData.newPlayer(nameField.getText(), surnameField.getText(), nicknameField.getText(), contactField.getText(), languageField.getText(), additionalField.getText(), teamid);
        else
            ServerData.editPlayer(editId, nameField.getText(), surnameField.getText(), nicknameField.getText(), contactField.getText(), languageField.getText(), additionalField.getText(), teamid);

        parent.close();
    }

}