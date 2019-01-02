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
public class VistaFederationController implements VistaContainable, Refreshable{
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

        ServertriggeredEvents.addDataUpdateListener(this);
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

    private TabController newTab(String title){
        TabController tabCtrl = new TabController(title);
        tbpane.getTabs().add(tabCtrl.getTab());
        return tabCtrl;
    }

    private void reloadPane(Map<String, Integer> from, ListView<String> to){
        to.getSelectionModel().clearSelection();
        from.put("** Dodaj **", -1);
        ObservableList<String> ols = FXCollections.observableArrayList();
        for (String key : from.keySet()) {
            ols.add(key);
        }
        to.setItems(ols);
    }

    @Override
    public void refresh(){
        if(contestantsPane.isExpanded()){
            contestants = ServerData.getListOfAllContestants();
            reloadPane(contestants, lvContestants);
        }else if(teamsPane.isExpanded()){
            teams = ServerData.getListOfAllTeams();
            reloadPane(teams, lvTeams);
        }else if(eventsPane.isExpanded()){
            events = ServerData.getListOfAllTournaments();
            reloadPane(events, lvEvents);
        }else if(matchesPane.isExpanded()){
            matches = ServerData.getListOfAllMatches();
            reloadPane(matches, lvMatches);
        }else if(arenasPane.isExpanded()){
            arenas = ServerData.getListOfAllArenas();
            reloadPane(arenas, lvArenas);
        }else if(usersPane.isExpanded()){
            users = ServerData.getListOfAllUsers();
            reloadPane(users, lvUsers);
        }
    }

    private void selectMenu(){
        contestantsPane.expandedProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                if(contestantsPane.isExpanded()){
                    contestants = ServerData.getListOfAllContestants();
                    reloadPane(contestants, lvContestants);
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
                if(id == -1) {
                    //Dodaj
                    c = new Player(-1, "Jan", "jkowalski", "Kowalski", 1200, "pl", "", "", null);
                }else
                    c = ServerData.getContestantById(id);

                VistaEntryViewerController entryViewer = new VistaEntryViewerController(newTab("Profil - " + c.displayedName()), "Contestant");
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
                    reloadPane(teams, lvTeams);
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
                if(id == -1){
                    //Dodaj
                    t = new Team(-1, "Smoki PK", "Kraków", null);
                }else
                    t = ServerData.getTeamById(id);

                if(t==null)
                    return;

                VistaEntryViewerController entryViewer = new VistaEntryViewerController(newTab("Drużyna - " + t.displayedName()), "Team");
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
                    reloadPane(events, lvEvents);
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

                if(id == -1) {
                    //Dodaj
                    if(VistaLogInController.hasOrganizerPermissions()){
                        new VistaCompetitionCreatorController(newTab("Stwórz turniej"));
                    }else {
                        Dialogs.error("Niewystarczające uprawnienia.");
                        VistaNavigator.loadVista(VistaNavigator.VISTA_LOGIN, newTab("Zaloguj się"));
                    }
                    return;
                    //c = new Competition(-1, "Turniej szachowy", Competition.Type.SOLO, "", null);
                }

                Competition c = ServerData.getTournamentById(id);
                if(c==null)
                    return;
                new VistaCompetitionController(parent, c);
            }
        });

        matchesPane.expandedProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                if(matchesPane.isExpanded()){
                    matches = ServerData.getListOfAllMatches();
                    reloadPane(matches, lvMatches);
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
                    //Dodaj
                    m = new Match(-1, new Team(-1, "Smoki PK", "Kraków", null), new Team(-1, "AGHenci", "Kraków", null), 2, 0, null, new Date(), null);
                } else
                    m = ServerData.getMatchById(id);

                if(m==null)
                    return;

                VistaEntryViewerController entryViewer = new VistaEntryViewerController(newTab(m.toString()), "Match");
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
                    reloadPane(arenas, lvArenas);
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
                    //Dodaj
                    a = new Arena(-1, "", "");
                } else
                    a = ServerData.getArenaById(id);

                if(a==null)
                    return;

                VistaEntryViewerController entryViewer = new VistaEntryViewerController(newTab("Arena - " + a.getName()), "Arena");
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
                    reloadPane(users, lvUsers);
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

                if(id == -1) {
                    //Dodaj - Sprawdzenie uprawnień i Rejestracja
                    if(VistaLogInController.hasFullPermissions()){
                        new VistaRegistrationController(newTab("Rejestracja organizatora"));
                    }else {
                        Dialogs.error("Niewystarczające uprawnienia.");
                        VistaNavigator.loadVista(VistaNavigator.VISTA_LOGIN, newTab("Zaloguj się"));
                    }
                    return;
                }

                User u = ServerData.getUserById(id);
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