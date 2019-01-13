package application;

import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;

import java.io.IOException;
import java.util.Optional;

public class Dialogs {

    public static void error(String msg){
        error(msg, "Błąd");
    }

    public static void error(String msg, String title){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    public static void insufficientPermissions(){
        Dialogs.error("Nie posiadasz wystarczających uprawnień!", "Niewystarczające uprawnienia");
        MainController.openLogInTab();
    }
}
