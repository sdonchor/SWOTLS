package application;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;

/**
 * Controller class for the login vista.
 */
public class VistaRegistrationController implements VistaContainable {
    public VistaRegistrationController(VistaContainer parent){
        this.init(parent);
    }

    private VistaContainer parent;
    @FXML private TextField idField;
    @FXML private PasswordField pwField;
    @FXML private PasswordField pwConfirm;

    @Override
    public void init(VistaContainer parent){
        this.parent = parent;
        FXMLLoader loader = new FXMLLoader(getClass().getResource(VistaNavigator.VISTA_REGISTER));
        loader.setController(this);
        try {
            parent.setVista(loader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void submit(ActionEvent event) {
        if(pwField.getText().equals(pwConfirm.getText())) {
            ServerData.register(idField.getText(), pwField.getText(), Permission.FULL);
            parent.close();
        }else
            Dialogs.error("Hasła się nie zgadzają. Pola \"Hasło\" i \"Potwierdź\" muszą być takie same!", "Pola z hasłem się różnią!");
    }

}