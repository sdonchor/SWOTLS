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
import java.util.HashMap;
import java.util.Map;

/**
 * Controller class for the first vista.
 */
public class VistaCompetitionController implements VistaContainable, Refreshable, TabContainer{
    private VistaContainer parent;
    private Competition competition;

    public VistaCompetitionController(VistaContainer parent, Competition competition){
        this.competition = competition;
        this.init(parent);
    }

    private Map<String, Integer> competitors;
    private Map<String, Integer> unplanned = new HashMap<>();
    private Map<String, Integer> planned = new HashMap<>();
    private Map<String, Integer> finished;
    private Map<String, Integer> reports;
    @Override
    public void init(VistaContainer parent){
        this.parent = parent;
        FXMLLoader loader = new FXMLLoader(getClass().getResource(VistaNavigator.VISTA_COMPETITION));
        loader.setController(this);
        try {
            parent.setVista(loader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }

        selectMenu();

        ObservableList<String> ols = FXCollections.observableArrayList();
        lvCompetitors.setItems(ols);

        VistaEntryViewerController entryViewer = new VistaEntryViewerController(newTab("Wydarzenie - " + competition.getName()));
        entryViewer.addEntry("Id", String.valueOf(competition.getId()) );
        entryViewer.addEntry("Nazwa", competition.getName() );
        entryViewer.addEntry("Typ", competition.getType().toString() );
        entryViewer.addEntry("Informacje dodatkowe", competition.getAdditionalInfo() );
        User u = competition.getCreator();
        if(u!=null)
            entryViewer.addEntry("Założyciel", u.getLogin() );
        else
            entryViewer.addEntry("Założyciel", "" );

        updateBottomLabel();

        MainController.setTabContainer(this);
        ServertriggeredEvents.addDataUpdateListener(this);
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
    private ListView<String> lvResults;
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
    @FXML
    private TitledPane resultsPane;
    @FXML
    private Label bottomLabel;

    private void nextSage(){
        if(unplanned.size()>1&&planned.size()>1)
            Dialogs.error("Nie można przejść do następnego etapu przed zakończeniem aktualnego! Wprowadź wyniki wszystkich meczy.", "Nie można przejść do następnego etapu");
        else
            ServerData.nextStage(competition.getId());
    }

    private void updateBottomLabel()
    {
        if(competition.getStage()>0)
            bottomLabel.setText("Perspektywa wydarzenia: " + competition.getName() + " - Etap " + competition.getStage());
        else
            bottomLabel.setText("Perspektywa wydarzenia: " + competition.getName() + " - Zapisy");
    }

    public TabController newTab(String title){
        TabController tabCtrl = new TabController(title);
        tbpane.getTabs().add(tabCtrl.getTab());
        return tabCtrl;
    }

    /**
     * Przepisuje klucze z danej mapy do ListView dodając na jego sczycie dodatkowy wpis określony jako "actionString". (Metoda wywoływana przy rozwijaniu panelu)
     * @param from Mapa, której klucze zostają przepisane do podanej listy. Dodatkowo doztanie do niej dodany wpis z kluczem "actionString" i wartością -1, co oznacza akcję, którą można wybrać w kontekście listy.
     * @param to Lista, do której załadować dane. (Przekazujemy listę, której zawartość będzie wyświetlana przez panel)
     * @param actionString Wpis umieszczony na szczycie listy oznaczający akcję, którą można wybrać w kontekście listy. (np. "** Dodaj **")
     */
    private void reloadPane(Map<String, Integer> from, ListView<String> to, String actionString){
        to.getSelectionModel().clearSelection();
        ObservableList<String> ols = FXCollections.observableArrayList();
        ols.add(actionString);
        for (String key : from.keySet()) {
            ols.add(key);
        }
        from.put(actionString, -1);
        to.setItems(ols);
    }

    @Override
    /**
     * Odświeża dane turnieju.
     */
    public void refresh(){
        competition = ServerData.getTournamentById(competition.getId());
        updateBottomLabel();

        //Sprawdza, który panel jest otwarty i przeładowywuje w nim dane.
        if(competitorsPane.isExpanded()){
            competitors = ServerData.getListOfCompetitionContestants(competition.getId());
            reloadPane(competitors, lvCompetitors, "** Dodaj **");
        }else if(unplannedPane.isExpanded()){
            unplanned = ServerData.getListOfUnplannedMatches(competition.getId());
            reloadPane(unplanned, lvUnplanned, "** Następny etap **");
        }else if(plannedPane.isExpanded()){
            planned = ServerData.getListOfPlannedMatches(competition.getId());
            reloadPane(planned, lvPlanned, "** Następny etap **");
        }else if(finishedPane.isExpanded()){
            finished = ServerData.getListOfFinishedMatches(competition.getId());
            reloadPane(finished, lvFinished, "** Następny etap **");
        }
    }

    private void selectMenu(){
        competitorsPane.expandedProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                if(competitorsPane.isExpanded()){
                    competitors = ServerData.getListOfCompetitionContestants(competition.getId());
                    reloadPane(competitors, lvCompetitors, "** Dodaj **");
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
                    if(VistaLogInController.hasOrganizerPermissions())
                        new VistaCompetitorChooserController(newTab("Dodaj uczestników"), competitors, competition);
                    else{
                        Dialogs.insufficientPermissions();
                    }
                    return;
                }else
                    c = ServerData.getContestantById(id);

                VistaEntryViewerController entryViewer = new VistaEntryViewerController(newTab("Profil - " + c.displayedName()));
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
                    reloadPane(planned, lvPlanned, "** Następny etap **");
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

                if(id == -1) {
                    //Przejdź
                    nextSage();
                    return;
                }

                Match m = ServerData.getMatchById(id);
                if(m==null)
                    return;
                VistaEntryViewerController entryViewer = new VistaEntryViewerController(newTab(m.toString()));
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
                    reloadPane(unplanned, lvUnplanned, "** Następny etap **");
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

                if(id == -1) {
                    //Przejdź
                    nextSage();
                    return;
                }

                Match m = ServerData.getMatchById(id);
                if(m==null)
                    return;
                new VistaMatchPlannerController(newTab("Zaplanuj mecz - " + m.toString()), m);
            }
        });

        finishedPane.expandedProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                if(finishedPane.isExpanded()){
                    finished = ServerData.getListOfFinishedMatches(competition.getId());
                    reloadPane(finished, lvFinished, "** Następny etap **");
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
                    //Przejdź
                    nextSage();
                    return;
                } else
                    m = ServerData.getMatchById(id);

                if(m==null)
                    return;

                VistaEntryViewerController entryViewer = new VistaEntryViewerController(newTab(m.toString()));
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

        resultsPane.expandedProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                if(resultsPane.isExpanded()){
                    reports = ServerData.getListOfReports(competition.getId());
                    reloadPane(reports, lvResults, "** Następny etap **");
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
                    //Przejdź
                    nextSage();
                    return;
                } else
                    m = ServerData.getMatchById(id);

                if(m==null)
                    return;

                VistaEntryViewerController entryViewer = new VistaEntryViewerController(newTab(m.toString()));
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