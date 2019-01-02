package application;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListView;
import javafx.scene.control.TabPane;
import javafx.scene.control.TitledPane;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

/**
 * Controller class for the first vista.
 */
public class VistaCompetitionController implements VistaContainable, Refreshable{
    private VistaContainer parent;
    private Competition competition;

    public VistaCompetitionController(VistaContainer parent, Competition competition){
        this.parent = parent;
        this.competition = competition;

        FXMLLoader loader = new FXMLLoader(getClass().getResource(VistaNavigator.VISTA_COMPETITION));
        loader.setController(this);
        try {
            parent.setVista(loader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.init();
    }

    private Map<String, Integer> competitors;
    private Map<String, Integer> unplanned;
    private Map<String, Integer> planned;
    private Map<String, Integer> finished;
    @Override
    public void setParent(VistaContainer parent) {
        this.parent = parent;
    }
    @Override
    public void init(){
        selectMenu();

        ObservableList<String> ols = FXCollections.observableArrayList();
        lvCompetitors.setItems(ols);

        ServertriggeredEvents.addDataUpdateListener(this);

        VistaEntryViewerController entryViewer = new VistaEntryViewerController(newTab("Wydarzenie - " + competition.getName()), "Competition");
        entryViewer.addEntry("Id", String.valueOf(competition.getId()) );
        entryViewer.addEntry("Nazwa", competition.getName() );
        entryViewer.addEntry("Typ", competition.getType().toString() );
        entryViewer.addEntry("Informacje dodatkowe", competition.getAdditionalInfo() );
        User u = competition.getCreator();
        if(u!=null)
            entryViewer.addEntry("Założyciel", u.getLogin() );
        else
            entryViewer.addEntry("Założyciel", "" );
    }

    @FXML
    private ListView<String> lvPlanned;
    @FXML
    private ListView<String> lvUnplanned;
    @FXML
    private ListView<String> lvFinished;
    @FXML
    private ListView<String> lvCompetitors;
    @FXML
    private TabPane tbpane;
    @FXML
    private TitledPane plannedPane;
    @FXML
    private TitledPane unplannedPane;
    @FXML
    private TitledPane finishedPane;
    @FXML
    private TitledPane competitorsPane;

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
        if(competitorsPane.isExpanded()){
            competitors = ServerData.getListOfCompetitionContestants(competition.getId());
            reloadPane(competitors, lvCompetitors);
        }else if(unplannedPane.isExpanded()){
            unplanned = ServerData.getListOfUnplannedMatches(competition.getId());
            reloadPane(unplanned, lvUnplanned);
        }else if(plannedPane.isExpanded()){
            planned = ServerData.getListOfPlannedMatches(competition.getId());
            reloadPane(planned, lvPlanned);
        }else if(finishedPane.isExpanded()){
            finished = ServerData.getListOfFinishedMatches(competition.getId());
            reloadPane(finished, lvFinished);
        }
    }

    private void selectMenu(){
        competitorsPane.expandedProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                if(competitorsPane.isExpanded()){
                    competitors = ServerData.getListOfCompetitionContestants(competition.getId());
                    reloadPane(competitors, lvCompetitors);
                }else {
                    lvCompetitors.getSelectionModel().clearSelection();
                }
            }
        });

        lvCompetitors.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                //int i = lvContestants.getSelectionModel().getSelectedIndex();

                String s = lvCompetitors.getSelectionModel().getSelectedItem();
                if(s==null)
                    return;
                int id = competitors.get(s);
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

        plannedPane.expandedProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                if(plannedPane.isExpanded()){
                    planned = ServerData.getListOfPlannedMatches(competition.getId());
                    reloadPane(planned, lvPlanned);
                }else {
                    lvPlanned.getSelectionModel().clearSelection();
                }
            }
        });

        lvPlanned.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                String s = lvPlanned.getSelectionModel().getSelectedItem();
                if(s==null)
                    return;
                int id = planned.get(s);
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

        unplannedPane.expandedProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                if(unplannedPane.isExpanded()){
                    unplanned = ServerData.getListOfUnplannedMatches(competition.getId());
                    reloadPane(unplanned, lvUnplanned);
                }else {
                    lvUnplanned.getSelectionModel().clearSelection();
                }
            }
        });

        lvUnplanned.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                String s = lvUnplanned.getSelectionModel().getSelectedItem();
                if(s==null)
                    return;
                int id = unplanned.get(s);

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

        finishedPane.expandedProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                if(finishedPane.isExpanded()){
                    finished = ServerData.getListOfFinishedMatches(competition.getId());
                    reloadPane(finished, lvFinished);
                }else {
                    lvFinished.getSelectionModel().clearSelection();
                }
            }
        });

        lvFinished.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                String s = lvFinished.getSelectionModel().getSelectedItem();
                if(s==null)
                    return;
                int id = finished.get(s);
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
    }
}