package application;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class VistaMatchViewerController extends VistaEntryViewerController {
    @FXML private Button actionButton;
    @FXML private Button deleteButton;

    public VistaMatchViewerController(VistaContainer parent, Match m){
        super(parent);
        addEntry("Id", String.valueOf(m.getId()) );
        if(m.getSideA()!=null)
            addEntry("Strona A", m.getSideA().displayedName() );
        if(m.getSideB()!=null)
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

        actionButton.setVisible(false);
        deleteButton.setVisible(false);
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