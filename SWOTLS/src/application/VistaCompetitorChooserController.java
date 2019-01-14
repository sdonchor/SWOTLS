package application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ComboBox;

import java.io.IOException;
import java.util.Map;

/**
 * Controller class for the login vista.
 */
public class VistaCompetitorChooserController implements VistaContainable {
    private VistaContainer parent;
    private Map<String, Integer> addedContestants;
    private Map<String, Integer> contestants;
    private Competition competition;

    public VistaCompetitorChooserController(VistaContainer parent, Map<String, Integer> addedContestants, Competition competition){
        this.addedContestants = addedContestants;
        this.competition = competition;
        this.init(parent);
    }

    @FXML private ComboBox<String> competitorBox;

    @Override
    public void init(VistaContainer parent){
        this.parent = parent;
        FXMLLoader loader = new FXMLLoader(getClass().getResource(VistaNavigator.VISTA_COMPETITOR_CHOOSER));
        loader.setController(this);
        try {
            parent.setVista(loader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }

        init();
    }

    private void init(){
        ObservableList<String> options = FXCollections.observableArrayList();
        if(competition.getType()==Competition.Type.SOLO)
            contestants = ServerData.getListOfAllContestants();
        else
            contestants = ServerData.getListOfAllTeams();

        for(String s : contestants.keySet()){
            if(!addedContestants.containsKey(s))
                options.add(s);
        }
        competitorBox.setItems(options);
    }

    @FXML
    void submit(ActionEvent event) {
        if(VistaLogInController.hasOrganizerPermissions()){
            String s = competitorBox.getValue();
            if(s==null){
                Dialogs.error("Nie wybrano uczestnika do dodania!");
                return;
            }
            int id = contestants.get(s);
            ServerData.addCompetitorToCompetition(id, competition.getId());
            addedContestants.put(s, id);
            init();
        }else {
            Dialogs.insufficientPermissions();
        }
    }

}