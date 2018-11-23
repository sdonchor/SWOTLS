package application;

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
 * Controller class for the first vista.
 */
public class VistaEventController implements VistaContainable{
    VistaContainer parent;
    @Override
    public void setParent(VistaContainer parent) {
        this.parent = parent;
    }
    @Override
    public void init(){
        loadListView();
        selectMenu();
    }

    @FXML
    private ListView<String> lvmaster;
    @FXML
    private TabPane tbpane;

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
                    TabController controller = new TabController("Profil - Pranica");
                    tbpane.getTabs().add(controller.getTab());
                }else if(i==1){
                    TabController controller = new TabController("Profil - Donchór");
                    tbpane.getTabs().add(controller.getTab());
                }

            }
        });
    }
}