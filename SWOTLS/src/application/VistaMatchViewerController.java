package application;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class VistaMatchViewerController extends VistaEntryViewerController {
    public VistaMatchViewerController(VistaContainer parent){
        super(parent);
    }

    @FXML
    private void buttonEdit(ActionEvent event) {
        Dialogs.error("Nie można edytować meczów z widoku federacji. Przejdź do odpowiedniego wydarzenia.");
    }

    @FXML
    private void buttonDelete(ActionEvent event) {
        Dialogs.error("Nie można usuwać pojedynczych meczy - musisz usunąć cały turniej.");
    }
}