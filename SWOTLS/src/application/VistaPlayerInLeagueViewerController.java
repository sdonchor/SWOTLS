package application;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class VistaPlayerInLeagueViewerController extends VistaEntryViewerController {
    private Player player;
    private Competition league;
    @FXML private Button actionButton;
    @FXML private Button deleteButton;

    public VistaPlayerInLeagueViewerController(VistaContainer parent, Player player, Competition league){
        super(parent);
        this.player = player;
        this.league = league;
        addEntry("Id", String.valueOf(player.getId()) );
        addEntry("Imię", player.getName() );
        addEntry("Nazwisko", player.getSurname() );
        addEntry("Pseudonim", player.getNickname() );
        addEntry("Ranking Elo", String.valueOf(player.getElo()) );
        addEntry("Język", player.getLanguage() );
        addEntry("Informacje kontaktowe", player.getContactInfo() );
        int leagueClass = ServerData.getCompetitorsLeagueClass(league.getId(), player.getId());
        player.setLeagueClass(leagueClass);
        addEntry("Klasa rozgrywkowa (\"Liga\")", Integer.toString(leagueClass) );
        addEntry("Informacje dodatkowe", player.getAdditionalInfo() );
        Team t = player.getTeam();
        if(t!=null)
            addEntry("Drużyna", t.displayedName() );
        else
            addEntry("Drużyna", "" );

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

        if(player.getLeagueClass()<=1){
            Dialogs.error("Uczestnik już znajduje się w najwyższej klasie ligowej.");
            return;
        }

        ServerData.promotePlayer(league.getId(), player.getId());
        getParent().close();
        new VistaPlayerInLeagueViewerController(MainController.newTab("Zawodnik - " + player.displayedName()), player, league);
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

        ServerData.demotePlayer(league.getId(), player.getId());
        getParent().close();
        new VistaPlayerInLeagueViewerController(MainController.newTab("Zawodnik - " + player.displayedName()), player, league);
    }

}