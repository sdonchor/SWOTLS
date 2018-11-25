package application;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Main controller class for the entire layout.
 */
public class TabController extends VistaContainer {
    private StringProperty name = new SimpleStringProperty();
    private Tab tab;

    public TabController(String tabName) {
        name.set(tabName);
        FXMLLoader loader = new FXMLLoader(getClass().getResource(VistaNavigator.VISTA_HOLDER));
        loader.setController(this);
        Node node = null;
        try {
            node = (StackPane) loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.tab = new Tab(tabName, node);
        //VistaNavigator.loadVista(VistaNavigator.VISTA_LOGIN, this);
    }

    public Tab getTab() {
        return tab;
    }

    /** Holder of a switchable vista. */
    @FXML
    private StackPane vistaHolder;

    /**
     * Replaces the vista displayed in the vista holder with a new vista.
     *
     * @param node the vista node to be swapped in.
     */
    public void setVista(Node node) {
        vistaHolder.getChildren().setAll(node);
    }
}