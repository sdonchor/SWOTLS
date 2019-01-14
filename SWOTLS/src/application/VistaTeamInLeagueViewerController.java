package application;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class VistaTeamInLeagueViewerController extends VistaEntryViewerController {
    private Team team;
    private Competition league;
    @FXML private Button actionButton;
    @FXML private Button deleteButton;

    public VistaTeamInLeagueViewerController(VistaContainer parent, Team t, Competition league){
        super(parent);
        this.team = t;
        this.league = league;

        addEntry("Id", String.valueOf(t.getId()) );
        addEntry("Nazwa", t.getName() );
        addEntry("Skąd", t.getWhereFrom() );
        Player p = t.getLeader();
        if(p!=null)
            addEntry("Lider", p.displayedName() );
        else
            addEntry("Lider", "" );

        int leagueClass = ServerData.getCompetitorsLeagueClass(league.getId(), team.getId());
        team.setLeagueClass(leagueClass);
        addEntry("Klasa rozgrywkowa (\"Liga\")", Integer.toString(leagueClass) );


        actionButton.setText("Awansuj");
        deleteButton.setText("Degraduj");
    }

    @FXML
    private void buttonEdit(ActionEvent event) {
        if(!VistaLogInController.hasOrganizerPermissions()){
            Dialogs.insufficientPermissions();
            return;
        }

        if(league.getStage()!=0){
            Dialogs.error("Awansować uczestników można tylko podczas fazy zapisów - na etapie pomiędzy sezonami.");
            return;
        }

        if(team.getLeagueClass()<=1){
            Dialogs.error("Uczestnik już znajduje się w najwyższej klasie ligowej.");
            return;
        }

        ServerData.promoteTeam(league.getId(), team.getId());
        getParent().close();
        new VistaTeamInLeagueViewerController(MainController.newTab("Drużyna - " + team.displayedName()), team, league);
    }

    @FXML
    private void buttonDelete(ActionEvent event) {
        if(!VistaLogInController.hasOrganizerPermissions()){
            Dialogs.insufficientPermissions();
            return;
        }

        if(league.getStage()!=0){
            Dialogs.error("Degradować uczestników można tylko podczas fazy zapisów - na etapie pomiędzy sezonami.");
            return;
        }

        ServerData.demoteTeam(league.getId(), team.getId());
        getParent().close();
        new VistaTeamInLeagueViewerController(MainController.newTab("Drużyna - " + team.displayedName()), team, league);
    }

}