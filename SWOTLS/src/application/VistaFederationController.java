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
    private Map<String, Integer> teams;
    private Map<String, Integer> events;
    private Map<String, Integer> matches;
    private Map<String, Integer> arenas;
    private Map<String, Integer> users;
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
    private ListView<String> lvTeams;
    @FXML
    private ListView<String> lvEvents;
    @FXML
    private ListView<String> lvMatches;
    @FXML
    private ListView<String> lvArenas;
    @FXML
    private ListView<String> lvUsers;
    @FXML
    private TabPane tbpane;
    @FXML
    private TitledPane contestantsPane;
    @FXML
    private TitledPane teamsPane;
    @FXML
    private TitledPane eventsPane;
    @FXML
    private TitledPane matchesPane;
    @FXML
    private TitledPane arenasPane;
    @FXML
    private TitledPane usersPane;

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
                Team t = c.getTeam();
                if(t!=null)
                    entryViewer.addEntry("Drużyna", t.displayedName() );
                else
                    entryViewer.addEntry("Drużyna", "" );
            }
        });

        teamsPane.expandedProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                if(teamsPane.isExpanded()){
                    teams = ServerData.getListOfAllTeams();
                    ObservableList<String> ols = FXCollections.observableArrayList();
                    for (String key : teams.keySet()) {
                        ols.add(key);
                    }
                    lvTeams.setItems(ols);
                }else {
                    lvTeams.getSelectionModel().clearSelection();
                }
            }
        });

        lvTeams.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                String s = lvTeams.getSelectionModel().getSelectedItem();
                if(s==null)
                    return;
                Team t = ServerData.getTeamById(teams.get(s));
                if(t==null)
                    return;
                TabController tabCtrl = new TabController("Drużyna - " + t.displayedName());
                tbpane.getTabs().add(tabCtrl.getTab());

                VistaEntryViewerController entryViewer = new VistaEntryViewerController(tabCtrl);
                entryViewer.addEntry("Id", String.valueOf(t.getId()) );
                entryViewer.addEntry("Nazwa", t.getName() );
                entryViewer.addEntry("Skąd", t.getWhereFrom() );
                Player p = t.getLeader();
                if(p!=null)
                    entryViewer.addEntry("Lider", p.displayedName() );
                else
                    entryViewer.addEntry("Lider", "" );
            }
        });

        eventsPane.expandedProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                if(eventsPane.isExpanded()){
                    events = ServerData.getListOfAllTournaments();
                    ObservableList<String> ols = FXCollections.observableArrayList();
                    for (String key : events.keySet()) {
                        ols.add(key);
                    }
                    lvEvents.setItems(ols);
                }else {
                    lvEvents.getSelectionModel().clearSelection();
                }
            }
        });

        lvEvents.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                String s = lvEvents.getSelectionModel().getSelectedItem();
                if(s==null)
                    return;
                Competition c = ServerData.getTournamentById(events.get(s));
                if(c==null)
                    return;
                TabController tabCtrl = new TabController("Wydarzenie - " + c.getName());
                tbpane.getTabs().add(tabCtrl.getTab());

                VistaEntryViewerController entryViewer = new VistaEntryViewerController(tabCtrl);
                entryViewer.addEntry("Id", String.valueOf(c.getId()) );
                entryViewer.addEntry("Nazwa", c.getName() );
                entryViewer.addEntry("Typ", c.getType().toString() );
                entryViewer.addEntry("Informacje dodatkowe", c.getAdditionalInfo() );
                User u = c.getCreator();
                if(u!=null)
                    entryViewer.addEntry("Założyciel", u.getLogin() );
                else
                    entryViewer.addEntry("Założyciel", "" );
            }
        });

        matchesPane.expandedProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                if(matchesPane.isExpanded()){
                    matches = ServerData.getListOfAllMatches();
                    ObservableList<String> ols = FXCollections.observableArrayList();
                    for (String key : matches.keySet()) {
                        ols.add(key);
                    }
                    lvMatches.setItems(ols);
                }else {
                    lvMatches.getSelectionModel().clearSelection();
                }
            }
        });

        lvMatches.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                String s = lvMatches.getSelectionModel().getSelectedItem();
                if(s==null)
                    return;
                Match m = ServerData.getMatchById(matches.get(s));
                if(m==null)
                    return;
                TabController tabCtrl = new TabController(m.toString());
                tbpane.getTabs().add(tabCtrl.getTab());

                VistaEntryViewerController entryViewer = new VistaEntryViewerController(tabCtrl);
                entryViewer.addEntry("Id", String.valueOf(m.getId()) );
                entryViewer.addEntry("Strona A", m.getSideA().displayedName() );
                entryViewer.addEntry("Strona B", m.getSideB().displayedName() );
                entryViewer.addEntry("Wynik A", String.valueOf(m.getScoreA()) );
                entryViewer.addEntry("Wynik B", String.valueOf(m.getScoreB()) );
                entryViewer.addEntry("Data", String.valueOf(m.getDate()) );

                Competition c = m.getCompetition();
                if(c!=null)
                    entryViewer.addEntry("Wydarzenie", c.getName() );
                else
                    entryViewer.addEntry("Wydarzenie", "" );

                Arena a = m.getArena();
                if(a!=null)
                    entryViewer.addEntry("Arena", a.getName() );
                else
                    entryViewer.addEntry("Arena", "" );
            }
        });

        arenasPane.expandedProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                if(arenasPane.isExpanded()){
                    arenas = ServerData.getListOfAllArenas();
                    ObservableList<String> ols = FXCollections.observableArrayList();
                    for (String key : arenas.keySet()) {
                        ols.add(key);
                    }
                    lvArenas.setItems(ols);
                }else {
                    lvArenas.getSelectionModel().clearSelection();
                }
            }
        });

        lvArenas.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                String s = lvArenas.getSelectionModel().getSelectedItem();
                if(s==null)
                    return;
                Arena a = ServerData.getArenaById(arenas.get(s));
                if(a==null)
                    return;
                TabController tabCtrl = new TabController("Arena - " + a.getName());
                tbpane.getTabs().add(tabCtrl.getTab());

                VistaEntryViewerController entryViewer = new VistaEntryViewerController(tabCtrl);
                entryViewer.addEntry("Id", String.valueOf(a.getId()) );
                entryViewer.addEntry("Nazwa", a.getName() );
                entryViewer.addEntry("Lokalizacja", a.getLocation() );
            }
        });

        usersPane.expandedProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                if(usersPane.isExpanded()){
                    users = ServerData.getListOfAllUsers();
                    ObservableList<String> ols = FXCollections.observableArrayList();
                    for (String key : users.keySet()) {
                        ols.add(key);
                    }
                    lvUsers.setItems(ols);
                }else {
                    lvUsers.getSelectionModel().clearSelection();
                }
            }
        });

        lvUsers.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                String s = lvUsers.getSelectionModel().getSelectedItem();
                if(s==null)
                    return;
                User u = ServerData.getUserById(users.get(s));
                if(u==null)
                    return;
                TabController tabCtrl = new TabController("Organizator - " + u.getLogin());
                tbpane.getTabs().add(tabCtrl.getTab());

                VistaEntryViewerController entryViewer = new VistaEntryViewerController(tabCtrl);
                entryViewer.addEntry("Id", String.valueOf(u.getId()) );
                entryViewer.addEntry("Login", u.getLogin() );
                entryViewer.addEntry("Uprawnienia", u.getPermissions() );
            }
        });
    }
}