package application;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TabPane;
import javafx.scene.control.TitledPane;

import java.util.Map;

/**
 * Controller class for the first vista.
 */
public class VistaFederationController implements VistaContainable{
    private Map<String, Integer> contestants;
    private VistaContainer parent;
    @Override
    public void setParent(VistaContainer parent) {
        this.parent = parent;
    }
    @Override
    public void init(){
        selectMenu();

        ObservableList<String> ols = FXCollections.observableArrayList();
        lvContestants.setItems(ols);
    }

    @FXML
    private ListView<String> lvContestants;
    @FXML
    private TabPane tbpane;
    @FXML
    private TitledPane contestantsPane;

    private void selectMenu(){
        contestantsPane.expandedProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                if(contestantsPane.isExpanded()){
                    contestants = ServerData.getListOfAllContestants();
                    ObservableList<String> ols = FXCollections.observableArrayList();
                    for (String key : contestants.keySet()) {
                        ols.add(key);
                    }
                    lvContestants.setItems(ols);
                }else {
                    lvContestants.getSelectionModel().clearSelection();
                }
            }
        });

        lvContestants.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                //int i = lvContestants.getSelectionModel().getSelectedIndex();
                String s = lvContestants.getSelectionModel().getSelectedItem();
                if(s==null)
                    return;
                Player c = ServerData.getContestantById(contestants.get(s));
                TabController tabCtrl = new TabController("Profil - " + c.displayedName());
                tbpane.getTabs().add(tabCtrl.getTab());

                VistaEntryViewerController entryViewer = new VistaEntryViewerController(tabCtrl);
                entryViewer.addEntry("Id", String.valueOf(c.getId()) );
                entryViewer.addEntry("Imię", c.getName() );
                entryViewer.addEntry("Nazwisko", c.getSurname() );
                entryViewer.addEntry("Pseudonim", c.getNickname() );
                entryViewer.addEntry("Ranking Elo", String.valueOf(c.getElo()) );
                entryViewer.addEntry("Język", c.getLanguage() );
                entryViewer.addEntry("Informacje kontaktowe", c.getContactInfo() );
                entryViewer.addEntry("Informacje dodatkowe", c.getAdditionalInfo() );
                entryViewer.addEntry("Id drużyny", String.valueOf(c.getTeamId()) );
            }
        });
    }
}