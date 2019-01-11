package application;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class VistaMatchViewerController extends VistaEntryViewerController {
    public VistaMatchViewerController(VistaContainer parent, Match m){
        super(parent);
        addEntry("Id", String.valueOf(m.getId()) );
        addEntry("Strona A", m.getSideA().displayedName() );
        addEntry("Strona B", m.getSideB().displayedName() );
        addEntry("Wynik A", String.valueOf(m.getScoreA()) );
        addEntry("Wynik B", String.valueOf(m.getScoreB()) );
        addEntry("Data", String.valueOf(m.getDate()) );

        Competition c = m.getCompetition();
        if(c!=null)
            addEntry("Wydarzenie", c.getName() );
        else
            addEntry("Wydarzenie", "" );

        Arena a = m.getArena();
        if(a!=null)
            addEntry("Arena", a.getName() );
        else
            addEntry("Arena", "" );
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