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
public class VistaLogInController implements VistaContainable {
    private static Permission permission = Permission.FULL;

    public static boolean hasFullPermissions(){
        if(permission == Permission.FULL)
            return true;
        return false;
    }

    public static boolean hasOrganizerPermissions(){
        if(permission == Permission.FULL || permission == Permission.ORGANIZER)
            return true;
        return false;
    }

    public static boolean isLoggedIn(){
        if(permission != Permission.GUEST)
            return true;
        return false;
    }

    public static void setPermission(Permission permission) {
        VistaLogInController.permission = permission;
    }

    public VistaLogInController(VistaContainer parent){
        this.init(parent);
    }

    public VistaLogInController(VistaContainer parent, String loginId){
        this.init(parent);
        idField.setText(loginId);
    }

    private VistaContainer parent;
    @FXML private TextField idField;
    @FXML private PasswordField pwField;

    @Override
    public void init(VistaContainer parent){
        this.parent = parent;
        FXMLLoader loader = new FXMLLoader(getClass().getResource(VistaNavigator.VISTA_LOGIN));
        loader.setController(this);
        try {
            parent.setVista(loader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void submit(ActionEvent event) {
        if(idField.getText().isEmpty() || pwField.getText().isEmpty()){
            Dialogs.error("Uzupełnij wszystkie pola.");
            return;
        }

        ServerData.logIn(idField.getText(), pwField.getText());
        parent.close();
    }

}