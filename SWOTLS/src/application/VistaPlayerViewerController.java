package application;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class VistaPlayerViewerController extends VistaEntryViewerController {
    private Player player;

    public VistaPlayerViewerController(VistaContainer parent, String type, Player player){
        super(parent, type);
        this.player = player;
        addEntry("Id", String.valueOf(player.getId()) );
        addEntry("Imię", player.getName() );
        addEntry("Nazwisko", player.getSurname() );
        addEntry("Pseudonim", player.getNickname() );
        addEntry("Ranking Elo", String.valueOf(player.getElo()) );
        addEntry("Język", player.getLanguage() );
        addEntry("Informacje kontaktowe", player.getContactInfo() );
        addEntry("Informacje dodatkowe", player.getAdditionalInfo() );
        Team t = player.getTeam();
        if(t!=null)
            addEntry("Drużyna", t.displayedName() );
        else
            addEntry("Drużyna", "" );
    }

    @FXML
    private void buttonEdit(ActionEvent event) {
        if(VistaLogInController.hasOrganizerPermissions()){
            new VistaPlayerCreatorController(getParent(), player);
        }else {
            Dialogs.insufficientPermissions();
        }
    }

}