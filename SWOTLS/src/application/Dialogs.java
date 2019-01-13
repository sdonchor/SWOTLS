package application;

import javafx.scene.control.Alert;
import javafx.scene.control.TextInputDialog;

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

    public static String inputDialog(String msg){
        return inputDialog(msg, "Wprowadź oczekiwaną wartość");
    }

    public static String inputDialog(String msg, String title){
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle(title);
        dialog.setHeaderText(msg);

        Optional<String> result = dialog.showAndWait();
        String entered = "none.";

        if (result.isPresent())
            entered = result.get();

        return entered;
    }
}
