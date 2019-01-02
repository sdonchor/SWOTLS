package application;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

/**
 * Main controller class for the entire layout.
 */
public class MainController extends VistaContainer  {

    /** Holder of a switchable vista. */
    @FXML
    private StackPane vistaHolder;
    @FXML
    private Label bottomLabel;

    /**
     * Replaces the vista displayed in the vista holder with a new vista.
     *
     * @param node the vista node to be swapped in.
     */
    @Override
    public void setVista(Node node) { vistaHolder.getChildren().setAll(node); }

    @FXML
    private void closeAppAction(ActionEvent event) {
        Platform.exit();
    }

    public void close() { Platform.exit(); }

    public void setBottomLabel(String s){
        bottomLabel.setText(s);
    }
}