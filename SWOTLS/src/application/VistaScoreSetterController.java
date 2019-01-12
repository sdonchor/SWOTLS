package application;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.util.Map;

/**
 * Controller class for the login vista.
 */
public class VistaScoreSetterController implements VistaContainable {
    private VistaContainer parent;
    private Map<String, Integer> players;
    private int matchId = -1;

    public VistaScoreSetterController(VistaContainer parent, Match match){
        this.init(parent);
        this.matchId = match.getId();
        labelA.setText("Punkty " + match.getSideA().displayedName());
        labelB.setText("Punkty " + match.getSideB().displayedName());
    }

    @FXML private Label labelA;
    @FXML private Label labelB;
    @FXML private TextField scoreA;
    @FXML private TextField scoreB;
    @FXML private Button actionButton;

    @Override
    public void init(VistaContainer parent){
        this.parent = parent;
        FXMLLoader loader = new FXMLLoader(getClass().getResource(VistaNavigator.VISTA_SCORE_SETTER));
        loader.setController(this);
        try {
            parent.setVista(loader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }

        scoreA.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (!newValue.matches("\\d*")) {
                    scoreA.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });

        scoreB.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (!newValue.matches("\\d*")) {
                    scoreB.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });
    }

    @FXML
    void submit(ActionEvent event) {
        if(!VistaLogInController.hasOrganizerPermissions()){
            Dialogs.insufficientPermissions();
            return;
        }

        if(scoreA.getText().isEmpty() || scoreB.getText().isEmpty()){
            Dialogs.error("Musisz wprowadzić punkty obu uczestników meczu!");
            return;
        }

        ServerData.setScore(matchId, Integer.parseInt(scoreA.getText()), Integer.parseInt(scoreB.getText()));

        parent.close();
    }
}