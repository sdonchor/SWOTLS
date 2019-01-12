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

        actionButton.setVisible(false);
    }

    @FXML
    private void buttonEdit(ActionEvent event) {
        Dialogs.error("Nie można edytować turnieju.");
    }

    @FXML
    private void buttonDelete(ActionEvent event) {
        ServerData.deleteTournament(competitionId);
    }
}