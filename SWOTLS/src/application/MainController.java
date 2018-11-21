package application;

import javafx.application.Platform;
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
public class MainController implements Initializable {

    /** Holder of a switchable vista. */
    @FXML
    private StackPane vistaHolder;
    @FXML
    private ListView<String> lvmaster;
    @FXML
    private TabPane tbpane;

    @Override
    public void initialize(URL url, ResourceBundle rb){
        loadListView();
        selectMenu();
    }

    /**
     * Replaces the vista displayed in the vista holder with a new vista.
     *
     * @param node the vista node to be swapped in.
     */
    public void setVista(Node node) {
        vistaHolder.getChildren().setAll(node);
    }

    @FXML
    private void closeAppAction(ActionEvent event) {
        Platform.exit();
    }

    private void loadListView(){
        ObservableList<String> ols = FXCollections.observableArrayList();
        ols.add("Jakub Pranica");
        ols.add("Sebastian Donchór");
        lvmaster.setItems(ols);
    }

    private void selectMenu(){
        lvmaster.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                int i = lvmaster.getSelectionModel().getSelectedIndex();
                if(i==0) {
                    Node node = null;
                    try {
                        node = (StackPane) FXMLLoader.load(getClass().getResource(VistaNavigator.VISTA_1));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Tab tb = new Tab("Profil", node);
                    tbpane.getTabs().add(tb);
                }

            }
        });
    }
}