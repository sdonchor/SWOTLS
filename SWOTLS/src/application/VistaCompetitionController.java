package application;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;

import java.io.IOException;
import java.util.Collections;
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

    private Map<String, Integer> competitors = new HashMap<>();
    private Map<String, Integer> unplanned = new HashMap<>();
    private Map<String, Integer> planned = new HashMap<>();
    private Map<String, Integer> finished = new HashMap<>();
    private Map<String, Integer> reports = new HashMap<>();
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

        new VistaCompetitionViewerController(newTab("Wydarzenie - " + competition.getName()), competition);

        updateBottomLabel();

        MainController.setTabContainer(this);
        ServertriggeredEvents.addDataUpdateListener(this);

        competitors = ServerData.getListOfCompetitionContestants(competition.getId());
        unplanned = ServerData.getListOfUnplannedMatches(competition.getId());
        planned = ServerData.getListOfPlannedMatches(competition.getId());
        finished = ServerData.getListOfFinishedMatches(competition.getId());
        reports = ServerData.getListOfReports(competition.getId());
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
        if(!VistaLogInController.hasOrganizerPermissions()){
            Dialogs.insufficientPermissions();
            return;
        }

        if(competitors.size()<3){
            Dialogs.error("Dodaj conajmniej 2 uczestników.");
            return;
        }

        if(competition.getStage()==0) {
            int participants = competitors.size() - 1;
            int system = competition.getSystem();
            if (system == 1) {
                //"Pucharowy"
                //Sprawdzenie czy liczba zapisanych do turnieju uczestników jest potęgą liczby 2
                boolean isValidPlayerCount = false;
                for(int i = 2; i<=participants; i*=2){
                    if(i == participants){
                        isValidPlayerCount = true;
                        break;
                    }
                }
                if(!isValidPlayerCount) {
                    Dialogs.error("Ilośc uczestników nie jest potęgą liczby 2!", "Niewłaściwa ilość uczestników");
                    return;
                }
            } else if (system == 2 || system == 4) {
                //"Szwajcarski" i "McMahona"
                if(participants%2==1) {
                    Dialogs.error("Ilość zawodników musi być parzysta!", "Niewłaściwa ilość uczestników");
                    return;
                }
            }
        }

        if(unplanned.size()>1||planned.size()>1)
            Dialogs.error("Nie można przejść do następnego etapu przed zakończeniem aktualnego! Wprowadź wyniki wszystkich meczy.", "Nie można przejść do następnego etapu");
        else {
            if(competition.getStage()==0 && (competition.getSystem()==2 || competition.getSystem()==4)) {
                try {
                    int rounds = Integer.valueOf(Dialogs.inputDialog("Wprowadź ilość rund do rozegrania:"));
                    if(rounds<1){
                        Dialogs.error("Liczba rund musi wynosić conajmniej 1! Spróbuj jeszcze raz.");
                        return;
                    }
                    ServerData.setNumberOfRounds(competition.getId(), rounds);
                }catch (NumberFormatException e){
                    Dialogs.error("Wprowadzono niedozwoloną wartość! Spróbuj jeszcze raz wprowadzająć poprawną liczbę.");
                    return;
                }
            }
            if(ServerData.nextStage(competition.getId()))
                Dialogs.error("Udało się przejść do następnej rundy.", "Sukces");
        }
    }

    private void updateBottomLabel()
    {
        String s;

        if(competition.getStage()>0) {
            if(competition.getSeason()==5)
                s = "Perspektywa wydarzenia: " + competition.getName() + " - Sezon " + competition.getSeason() +  " - Kolejka " + competition.getStage();
            else
                s = "Perspektywa wydarzenia: " + competition.getName() + " - Etap " + competition.getStage();
        }
        else if(competition.getStage()==0) {
            s = "Perspektywa wydarzenia: " + competition.getName() + " - Zapisy";
            if(competition.getSeason()==5)
                s += " przed sezonem " + competition.getSeason();
        }else{
            s = "Perspektywa wydarzenia: " + competition.getName() + " - Zakończony";
        }


        bottomLabel.setText(s);
    }

    @Override
    public TabController newTab(String title){
        TabController tabCtrl = new TabController(title);
        Tab tab = tabCtrl.getTab();
        tbpane.getTabs().add(tab);
        tbpane.getSelectionModel().select(tab);
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
        if(actionString!=null)
            ols.add(actionString);
        for (String key : from.keySet()) {
            ols.add(key);
        }
        if(actionString!=null)
            from.put(actionString, -1);
        Collections.sort(ols);
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
            //competitorsPane.setExpanded(false);
            competitors = ServerData.getListOfCompetitionContestants(competition.getId());
            if(competition.getStage()==0)
                reloadPane(competitors, lvCompetitors, "** Dodaj **");
            else
                reloadPane(competitors, lvCompetitors, null);
            //competitorsPane.setExpanded(true);
        }else if(unplannedPane.isExpanded()){
            //unplannedPane.setExpanded(false);
            unplanned = ServerData.getListOfUnplannedMatches(competition.getId());
            reloadPane(unplanned, lvUnplanned, "** Następny etap **");
            //unplannedPane.setExpanded(true);
        }else if(plannedPane.isExpanded()){
            //plannedPane.setExpanded(false);
            planned = ServerData.getListOfPlannedMatches(competition.getId());
            reloadPane(planned, lvPlanned, "** Następny etap **");
            //plannedPane.setExpanded(true);
        }else if(finishedPane.isExpanded()){
            //finishedPane.setExpanded(false);
            finished = ServerData.getListOfFinishedMatches(competition.getId());
            reloadPane(finished, lvFinished, "** Następny etap **");
            //finishedPane.setExpanded(true);
        }else if(resultsPane.isExpanded()){
            //resultsPane.setExpanded(false);
            reports = ServerData.getListOfReports(competition.getId());
            reloadPane(reports, lvResults, "** Następny etap **");
            //resultsPane.setExpanded(true);
        }
    }

    private void selectMenu(){
        competitorsPane.expandedProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                if(competitorsPane.isExpanded()){
                    competitors = ServerData.getListOfCompetitionContestants(competition.getId());
                    if(competition.getStage()==0)
                        reloadPane(competitors, lvCompetitors, "** Dodaj **");
                    else
                        reloadPane(competitors, lvCompetitors, null);
                }else {
                    lvCompetitors.getSelectionModel().clearSelection();
                }
            }
        });

        lvCompetitors.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                String s = lvCompetitors.getSelectionModel().getSelectedItem();
                if(s==null)
                    return;
                int id = competitors.get(s);
                if(id == -1) {
                    //Dodaj
                    if(VistaLogInController.hasOrganizerPermissions())
                        new VistaCompetitorChooserController(newTab("Dodaj uczestników"), competitors, competition);
                    else{
                        Dialogs.insufficientPermissions();
                    }
                    return;
                }

                if(competition.getType()== Competition.Type.SOLO){
                    //SOLO
                    Player player = ServerData.getContestantById(id);
                    if(player==null)
                        return;

                    if(competition.getSystem()==5) {
                        new VistaPlayerInLeagueViewerController(MainController.newTab("Zawodnik - " + player.displayedName()), player, competition);
                    }else {
                        new VistaPlayerViewerInTournamentController(newTab("Zawodnik - " + player.displayedName()), player, competition);
                    }
                }else {
                    //TEAM
                    Team t = ServerData.getTeamById(id);
                    if(t==null)
                        return;

                    if(competition.getSystem()==5) {
                        new VistaTeamInLeagueViewerController(MainController.newTab("Drużyna - " + t.displayedName()), t, competition);
                    }else {
                        new VistaTeamViewerInTournamentController(newTab("Drużyna - " + t.displayedName()), t, competition);
                    }
                }



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
                new VistaScoreSetterController(newTab("Wprowadź wynik - " + m.toString()), m);
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

                if(id == -1) {
                    //Przejdź
                    nextSage();
                    return;
                }
                Match m = ServerData.getMatchById(id);

                if(m==null)
                    return;

                s = m.toString();
                if(s.isEmpty())
                    s = "Mecz "+id;

                new VistaMatchViewerController(newTab(s), m);
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

        lvResults.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                String s = lvResults.getSelectionModel().getSelectedItem();
                if(s==null)
                    return;
                int id = reports.get(s);

                if(id == -1) {
                    //Przejdź
                    nextSage();
                    return;
                }

                Report report = ServerData.getReportById(id);
                if(report==null)
                    return;
                new VistaReportViewerController(newTab(report.getTitle()), report.getReport());
            }
        });
    }
}