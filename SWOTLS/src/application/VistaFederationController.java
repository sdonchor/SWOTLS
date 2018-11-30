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

import java.util.Date;
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
                    contestants.put("** Dodaj **", -1);
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
                int id = contestants.get(s);
                Player c;
                if(id == -1)
                    c = new Player(-1, "Jan", "jkowalski", "Kowalski", 1200, "pl", "", "", null);
                else
                    c = ServerData.getContestantById(id);

                TabController tabCtrl = new TabController("Profil - " + c.displayedName());
                tbpane.getTabs().add(tabCtrl.getTab());

                VistaEntryViewerController entryViewer = new VistaEntryViewerController(tabCtrl, "Contestant");
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

                if(id == -1)
                    entryViewer.setEditing(true);
            }
        });

        teamsPane.expandedProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                if(teamsPane.isExpanded()){
                    teams = ServerData.getListOfAllTeams();
                    teams.put("** Dodaj **", -1);
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
                int id = teams.get(s);
                Team t;
                if(id == -1)
                    t = new Team(-1, "Smoki PK", "Kraków", null);
                else
                    t = ServerData.getTeamById(id);

                if(t==null)
                    return;
                TabController tabCtrl = new TabController("Drużyna - " + t.displayedName());
                tbpane.getTabs().add(tabCtrl.getTab());

                VistaEntryViewerController entryViewer = new VistaEntryViewerController(tabCtrl, "Team");
                entryViewer.addEntry("Id", String.valueOf(t.getId()) );
                entryViewer.addEntry("Nazwa", t.getName() );
                entryViewer.addEntry("Skąd", t.getWhereFrom() );
                Player p = t.getLeader();
                if(p!=null)
                    entryViewer.addEntry("Lider", p.displayedName() );
                else
                    entryViewer.addEntry("Lider", "" );

                if(id == -1)
                    entryViewer.setEditing(true);
            }
        });

        eventsPane.expandedProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                if(eventsPane.isExpanded()){
                    events = ServerData.getListOfAllTournaments();
                    events.put("** Dodaj **", -1);
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
                int id = events.get(s);
                Competition c;
                if(id == -1) {
                    c = new Competition(-1, "Turniej szachowy", Competition.Type.SOLO, "", null);
                } else
                    c = ServerData.getTournamentById(id);

                if(c==null)
                    return;
                TabController tabCtrl = new TabController("Wydarzenie - " + c.getName());
                tbpane.getTabs().add(tabCtrl.getTab());

                VistaEntryViewerController entryViewer = new VistaEntryViewerController(tabCtrl, "Competition");
                entryViewer.addEntry("Id", String.valueOf(c.getId()) );
                entryViewer.addEntry("Nazwa", c.getName() );
                entryViewer.addEntry("Typ", c.getType().toString() );
                entryViewer.addEntry("Informacje dodatkowe", c.getAdditionalInfo() );
                User u = c.getCreator();
                if(u!=null)
                    entryViewer.addEntry("Założyciel", u.getLogin() );
                else
                    entryViewer.addEntry("Założyciel", "" );

                if(id == -1)
                    entryViewer.setEditing(true);
            }
        });

        matchesPane.expandedProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                if(matchesPane.isExpanded()){
                    matches = ServerData.getListOfAllMatches();
                    matches.put("** Dodaj **", -1);
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
                int id = matches.get(s);
                Match m;
                if(id == -1) {
                    m = new Match(-1, new Team(-1, "Smoki PK", "Kraków", null), new Team(-1, "AGHenci", "Kraków", null), 2, 0, null, new Date(), null);
                } else
                    m = ServerData.getMatchById(id);

                if(m==null)
                    return;
                TabController tabCtrl = new TabController(m.toString());
                tbpane.getTabs().add(tabCtrl.getTab());

                VistaEntryViewerController entryViewer = new VistaEntryViewerController(tabCtrl, "Match");
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

                if(id == -1)
                    entryViewer.setEditing(true);
            }
        });

        arenasPane.expandedProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                if(arenasPane.isExpanded()){
                    arenas = ServerData.getListOfAllArenas();
                    arenas.put("** Dodaj **", -1);
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
                int id = arenas.get(s);
                Arena a;
                if(id == -1) {
                    a = new Arena(-1, "", "");
                } else
                    a = ServerData.getArenaById(id);

                if(a==null)
                    return;
                TabController tabCtrl = new TabController("Arena - " + a.getName());
                tbpane.getTabs().add(tabCtrl.getTab());

                VistaEntryViewerController entryViewer = new VistaEntryViewerController(tabCtrl, "Arena");
                entryViewer.addEntry("Id", String.valueOf(a.getId()) );
                entryViewer.addEntry("Nazwa", a.getName() );
                entryViewer.addEntry("Lokalizacja", a.getLocation() );

                if(id == -1)
                    entryViewer.setEditing(true);
            }
        });

        usersPane.expandedProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                if(usersPane.isExpanded()){
                    users = ServerData.getListOfAllUsers();
                    users.put("** Dodaj **", -1);
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
                int id = users.get(s);
                User u;
                if(id == -1) {
                    u = new User(-1, "admin", "full");
                } else
                    u = ServerData.getUserById(id);

                if(u==null)
                    return;
                TabController tabCtrl = new TabController("Organizator - " + u.getLogin());
                tbpane.getTabs().add(tabCtrl.getTab());

                VistaEntryViewerController entryViewer = new VistaEntryViewerController(tabCtrl, "User");
                entryViewer.addEntry("Id", String.valueOf(u.getId()) );
                entryViewer.addEntry("Login", u.getLogin() );
                entryViewer.addEntry("Uprawnienia", u.getPermissions() );

                if(id == -1)
                    entryViewer.setEditing(true);
            }
        });
    }
}