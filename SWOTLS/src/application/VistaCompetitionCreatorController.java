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

/**
 * Controller class for the login vista.
 */
public class VistaCompetitionCreatorController implements VistaContainable {
    private VistaContainer parent;
    private int editId = -1;

    public VistaCompetitionCreatorController(VistaContainer parent){
        this.init(parent);
    }

    @FXML private TextField nameField;
    @FXML private TextField additionalField;
    @FXML private ComboBox<String> typeBox;
    @FXML private ComboBox<String> systemBox;
    @FXML private Button actionButton;

    public void init(VistaContainer parent){
        this.parent = parent;
        FXMLLoader loader = new FXMLLoader(getClass().getResource(VistaNavigator.VISTA_COMPETITION_CREATOR));
        loader.setController(this);
        try {
            parent.setVista(loader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }

        ObservableList<String> options =
                FXCollections.observableArrayList(
                        "Pucharowy",
                        "Szwajcarski",
                        "Kołowy",
                        "McMahona",
                        "Wieloklasowa liga"
                );
        systemBox.setItems(options);

        ObservableList<String> options2 =
                FXCollections.observableArrayList(
                        "Indywidualny",
                        "Drużynowy"
                );
        typeBox.setItems(options2);
    }

    @FXML
    void submit(ActionEvent event) {
        if(typeBox.getValue()==null || systemBox.getValue()==null){
            Dialogs.error("Wybierz typ turnieju.", "Nie wybrano typu turnieju!");
        }else {
            if(!VistaLogInController.hasOrganizerPermissions()){
                Dialogs.insufficientPermissions();
                return;
            }

            if(editId==-1)
                ServerData.newTournament(nameField.getText(), systemBox.getValue(), typeBox.getValue(), additionalField.getText());
            else
                ServerData.editTournament(editId, nameField.getText(), additionalField.getText());

            parent.close();
        }
    }

}