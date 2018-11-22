package application;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

/**
 * Controller class for the first vista.
 */
public class Vista1Controller implements VistaContainable {
    VistaContainer parent;

    public void setParent(VistaContainer parent) {
        this.parent = parent;
    }
    public void init(){}

    /**
     * Event handler fired when the user requests a new vista.
     *
     * @param event the event that triggered the handler.
     */
    @FXML
    void nextPane(ActionEvent event) {
        VistaNavigator.loadVista(VistaNavigator.VISTA_2, parent);
    }

}