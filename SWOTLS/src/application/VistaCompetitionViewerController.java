package application;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class VistaCompetitionViewerController extends VistaEntryViewerController {
    @FXML private Button actionButton;
    private int competitionId;

    public VistaCompetitionViewerController(VistaContainer parent, Competition competition){
        super(parent);
        this.competitionId = competition.getId();
        addEntry("Id", String.valueOf(competition.getId()) );
        addEntry("Nazwa", competition.getName() );
        addEntry("Typ", competition.getType().toString() );
        addEntry("Informacje dodatkowe", competition.getAdditionalInfo() );
        User u = competition.getCreator();
        if(u!=null)
            addEntry("Założyciel", u.getLogin() );
        else
            addEntry("Założyciel", "" );

        String system = "Nieokreślony";

        int iSystem = competition.getSystem();
        if(iSystem==1)
            system = "Pucharowy";
        else if(iSystem==2)
            system = "Szwajcarski";
        else if(iSystem==3)
            system = "Kołowy";
        else if(iSystem==4)
            system = "McMahona";
        else if(iSystem==5)
            system = "Wieloklasowa liga";

        addEntry("System", system);

        actionButton.setVisible(false);
    }

    @FXML
    private void buttonEdit(ActionEvent event) {
        Dialogs.error("Nie można edytować turnieju.");
    }

    @FXML
    private void buttonDelete(ActionEvent event) {
        if(!VistaLogInController.hasOrganizerPermissions()){
            Dialogs.insufficientPermissions();
            return;
        }

        application.Main.getMainController().goToFederationView();
        ServerData.deleteTournament(competitionId);
    }
}