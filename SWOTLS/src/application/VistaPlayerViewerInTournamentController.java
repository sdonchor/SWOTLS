package application;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class VistaPlayerViewerInTournamentController extends VistaEntryViewerController {
    private Player player;
    private Competition tournament;

    public VistaPlayerViewerInTournamentController(VistaContainer parent, Player player, Competition tournament){
        super(parent);
        this.player = player;
        this.tournament = tournament;
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

    @FXML
    private void buttonDelete(ActionEvent event) {
        if(!VistaLogInController.hasOrganizerPermissions()){
            Dialogs.insufficientPermissions();
            return;
        }

        if(tournament.getStage()!=0){
            Dialogs.error("Etap zapisów już się zakończył - nie można usunąć uczsetnika");
            return;
        }

        ServerData.removeFromTournament(player.getId(), tournament.getId());
        getParent().close();
    }

}