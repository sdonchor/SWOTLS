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
    private static Permission permission = Permission.GUEST;

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

    /*public VistaLogInController(VistaContainer parent){
        this.parent = parent;
        FXMLLoader loader = new FXMLLoader(getClass().getResource(VistaNavigator.VISTA_LOGIN));
        loader.setController(this);
        try {
            parent.setVista(loader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.init();
    }*/

    private VistaContainer parent;
    @FXML private TextField idField;
    @FXML private PasswordField pwField;

    @Override
    public void setParent(VistaContainer parent) {
        this.parent = parent;
    }
    @Override
    public void init(){}

    @FXML
    void submit(ActionEvent event) {
        //VistaNavigator.loadVista(VistaNavigator.VISTA_2, parent);
        permission = ServerData.logIn(idField.getText(), pwField.getText());
    }

}