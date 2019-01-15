package application;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class VistaTeamViewerInTournamentController extends VistaEntryViewerController {
    private Team team;
    private Competition tournament;

    public VistaTeamViewerInTournamentController(VistaContainer parent, Team t, Competition tournament){
        super(parent);
        this.team = t;
        this.tournament = tournament;
        addEntry("Id", String.valueOf(t.getId()) );
        addEntry("Nazwa", t.getName() );
        addEntry("Skąd", t.getWhereFrom() );
        Player p = t.getLeader();
        if(p!=null)
            addEntry("Lider", p.displayedName() );
        else
            addEntry("Lider", "" );
    }

    @FXML
    private void buttonEdit(ActionEvent event) {
        if(VistaLogInController.hasOrganizerPermissions()){
            new VistaTeamCreatorController(getParent(), team);
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

        ServerData.removeFromTournament(team.getId(), tournament.getId());
        getParent().close();
    }

}