package application;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TabPane;
import javafx.scene.control.TitledPane;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

/**
 * Controller class for the first vista.
 */
public class VistaFederationController implements VistaContainable, Refreshable, TabContainer{
    private Map<String, Integer> contestants;
    private Map<String, Integer> teams;
    private Map<String, Integer> events;
    private Map<String, Integer> matches;
    private Map<String, Integer> arenas;
    private Map<String, Integer> users;
    private VistaContainer parent;

    public VistaFederationController(VistaContainer parent){
        init(parent);
    }

    @Override
    public void init(VistaContainer parent){
        this.parent = parent;
        FXMLLoader loader = new FXMLLoader(getClass().getResource(VistaNavigator.VISTA_FEDERATION));
        loader.setController(this);
        try {
            parent.setVista(loader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }

        selectMenu();

        ObservableList<String> ols = FXCollections.observableArrayList();
        lvContestants.setItems(ols);

        MainController.setTabContainer(this);
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
    @FXML
    private Label bottomLabel;

    public void setBottomLabel(String s){
        bottomLabel.setText(s);
    }

    @Override
    public TabController newTab(String title){
        TabController tabCtrl = new TabController(title);
        tbpane.getTabs().add(tabCtrl.getTab());
        return tabCtrl;
    }

    private void reloadPane(Map<String, Integer> from, ListView<String> to, String actionString){
        to.getSelectionModel().clearSelection();
        ObservableList<String> ols = FXCollections.observableArrayList();
        if(actionString!=null)
            ols.add(actionString);
        for (String key : from.keySet()) {
            ols.add(key);
        }
        if(actionString!=null)
            from.put(actionString, -1);
        to.setItems(ols);
    }

    @Override
    public void refresh(){
        if(contestantsPane.isExpanded()){
            contestants = ServerData.getListOfAllContestants();
            reloadPane(contestants, lvContestants, "** Dodaj **");
        }else if(teamsPane.isExpanded()){
            teams = ServerData.getListOfAllTeams();
            reloadPane(teams, lvTeams, "** Dodaj **");
        }else if(eventsPane.isExpanded()){
            events = ServerData.getListOfAllTournaments();
            reloadPane(events, lvEvents, "** Dodaj **");
        }else if(matchesPane.isExpanded()){
            matches = ServerData.getListOfAllMatches();
            reloadPane(matches, lvMatches, null);
        }else if(arenasPane.isExpanded()){
            arenas = ServerData.getListOfAllArenas();
            reloadPane(arenas, lvArenas, "** Dodaj **");
        }else if(usersPane.isExpanded()){
            users = ServerData.getListOfAllUsers();
            reloadPane(users, lvUsers, "** Dodaj **");
        }
    }

    private void selectMenu(){
        contestantsPane.expandedProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                if(contestantsPane.isExpanded()){
                    contestants = ServerData.getListOfAllContestants();
                    reloadPane(contestants, lvContestants, "** Dodaj **");
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

                if(id == -1) {
                    //Dodaj
                    if(VistaLogInController.hasOrganizerPermissions()){
                        new VistaPlayerCreatorController(newTab("Dodaj zawodnika"));
                    }else {
                        Dialogs.insufficientPermissions();
                    }
                    return;
                }

                Player p = ServerData.getContestantById(id);
                if(p==null)
                    return;
                new VistaPlayerViewerController(newTab("Profil - " + p.displayedName()), p);
            }
        });

        teamsPane.expandedProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                if(teamsPane.isExpanded()){
                    teams = ServerData.getListOfAllTeams();
                    reloadPane(teams, lvTeams, "** Dodaj **");
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
                if(id == -1){
                    //Dodaj
                    if(VistaLogInController.hasOrganizerPermissions()){
                        new VistaTeamCreatorController(newTab("Dodaj drużynę"));
                    }else {
                        Dialogs.insufficientPermissions();
                    }

                    return;
                }

                Team t = ServerData.getTeamById(id);
                if(t==null)
                    return;
                new VistaTeamViewerController(newTab("Drużyna - " + t.displayedName()), t);
            }
        });

        eventsPane.expandedProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                if(eventsPane.isExpanded()){
                    events = ServerData.getListOfAllTournaments();
                    reloadPane(events, lvEvents, "** Dodaj **");
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
                        Dialogs.insufficientPermissions();
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
                    reloadPane(matches, lvMatches, null);
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

                VistaEntryViewerController entryViewer = new VistaMatchViewerController(newTab(m.toString()));
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
                    reloadPane(arenas, lvArenas, "** Dodaj **");
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
                if(id == -1) {
                    //Dodaj
                    if(VistaLogInController.hasOrganizerPermissions()){
                        new VistaArenaCreatorController(newTab("Dodaj arenę"));
                    }else {
                        Dialogs.insufficientPermissions();
                    }

                    return;
                }

                Arena a = ServerData.getArenaById(id);
                if(a==null)
                    return;
                new VistaArenaViewerController(newTab("Arena - " + a.getName()), a);
            }
        });

        usersPane.expandedProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                if(usersPane.isExpanded()){
                    users = ServerData.getListOfAllUsers();
                    reloadPane(users, lvUsers, "** Dodaj **");
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
                        Dialogs.insufficientPermissions();
                    }
                    return;
                }

                new VistaLogInController(newTab("Zaloguj się"), s);
            }
        });
    }
}