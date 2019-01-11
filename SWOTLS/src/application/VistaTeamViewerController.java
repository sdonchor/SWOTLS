package application;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class VistaTeamViewerController extends VistaEntryViewerController {
    private Team team;

    public VistaTeamViewerController(VistaContainer parent, Team t){
        super(parent);
        this.team = t;
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

        ServerData.deleteTeam(team.getId());
        getParent().close();
    }

}