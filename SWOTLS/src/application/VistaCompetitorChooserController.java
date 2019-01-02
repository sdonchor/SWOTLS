package application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

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
        this.parent = parent;
        this.addedContestants = addedContestants;
        this.competition = competition;
        FXMLLoader loader = new FXMLLoader(getClass().getResource(VistaNavigator.VISTA_COMPETITOR_CHOOSER));
        loader.setController(this);
        try {
            parent.setVista(loader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.init();
    }

    @FXML private ComboBox<String> competitorBox;

    @Override
    public void setParent(VistaContainer parent) {
        this.parent = parent;
    }
    @Override
    public void init(){
        ObservableList<String> options = FXCollections.observableArrayList();
        contestants = ServerData.getListOfAllContestants();
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
            Dialogs.error("Niewystarczające uprawnienia.");
            VistaNavigator.loadVista(VistaNavigator.VISTA_LOGIN, parent);
        }
    }

}