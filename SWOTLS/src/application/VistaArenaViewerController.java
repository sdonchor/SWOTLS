package application;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class VistaArenaViewerController extends VistaEntryViewerController {
    private Arena arena;

    public VistaArenaViewerController(VistaContainer parent, String type, Arena a){
        super(parent, type);
        this.arena = a;
        addEntry("Id", String.valueOf(a.getId()) );
        addEntry("Nazwa", a.getName() );
        addEntry("Lokalizacja", a.getLocation() );
    }

    @FXML
    private void buttonEdit(ActionEvent event) {
        if(VistaLogInController.hasOrganizerPermissions()){
            new VistaArenaCreatorController(getParent(), arena);
        }else {
            Dialogs.insufficientPermissions();
        }
    }

}