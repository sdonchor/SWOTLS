package application;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.io.IOException;

/**
 * Controller class for the login vista.
 */
public class VistaArenaCreatorController implements VistaContainable {
    private VistaContainer parent;
    private int editId = -1;

    public VistaArenaCreatorController(VistaContainer parent){
        this.init(parent);
    }

    public VistaArenaCreatorController(VistaContainer parent, Arena a){
        this.init(parent);
        this.editId = a.getId();
        this.actionButton.setText("Zapisz");
        this.nameField.setText(a.getName());
        this.locationField.setText(a.getLocation());
    }

    @FXML private TextField nameField;
    @FXML private TextField locationField;
    @FXML private Button actionButton;

    @Override
    public void init(VistaContainer parent){
        this.parent = parent;
        FXMLLoader loader = new FXMLLoader(getClass().getResource(VistaNavigator.VISTA_ARENA_CREATOR));
        loader.setController(this);
        try {
            parent.setVista(loader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
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

        if(editId==-1)
            ServerData.newArena(nameField.getText(), locationField.getText());
        else
            ServerData.editArena(editId, nameField.getText(), locationField.getText());

        parent.close();
    }

}