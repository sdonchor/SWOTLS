package application;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;

/**
 * Controller class for the login vista.
 */
public class VistaConnectController implements VistaContainable {
    public VistaConnectController(VistaContainer parent){
        this.init(parent);
    }

    private VistaContainer parent;
    @FXML private TextField addressField;
    @FXML private TextField portField;

    @Override
    public void init(VistaContainer parent){
        this.parent = parent;
        FXMLLoader loader = new FXMLLoader(getClass().getResource(VistaNavigator.VISTA_CONNECT));
        loader.setController(this);
        try {
            parent.setVista(loader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }

        portField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (!newValue.matches("\\d*")) {
                    portField.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });

        addressField.setText("localhost");
        portField.setText("4545");
    }

    @FXML
    void submit(ActionEvent event) {
        if(addressField.getText().isEmpty() || portField.getText().isEmpty()){
            Dialogs.error("Uzupełnij wszystkie pola.");
            return;
        }

        try {
            if (ServerData.initializeServerConnection(addressField.getText(), Integer.valueOf(portField.getText()))) {
                Main.getMainController().goToFederationView();
            } else {
                Dialogs.error("Nie udało się połączyć z serwerem o podanych danych.");
            }
        }catch (NumberFormatException e){
            Dialogs.error("Wprowadzono nieprawidłowy port!");
        }
    }

}