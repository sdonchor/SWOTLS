package application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class VistaEntryViewerController implements VistaContainable {
    private VistaContainer parent;
    private ObservableList<Entry> data;
    @FXML private TableView<Entry> table;
    @FXML private TableColumn<Entry, String> attributeColumn;
    @FXML private TableColumn<Entry, String> valueColumn;

    public VistaEntryViewerController(VistaContainer parent){
        this.parent = parent;
        FXMLLoader loader = new FXMLLoader(getClass().getResource(VistaNavigator.VISTA_ENTRY));
        loader.setController(this);
        try {
            parent.setVista(loader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.init();
    }

    @Override
    public void setParent(VistaContainer parent) {
        this.parent = parent;
    }
    @Override
    public void init(){
        data = FXCollections.observableArrayList();

        // Ustawienie danych dla tabeli
        table.itemsProperty().setValue(data);

        // Powiązanie pierwszej kolumny z polem nazwa obiektu typu Seria
        attributeColumn.setCellValueFactory(
                new PropertyValueFactory<Entry, String>("attribute")
        );

        // Powiązanie drugiej kolumny z polem nazwa obiektu typu Seria
        valueColumn.setCellValueFactory(
                new PropertyValueFactory<Entry, String>("value")
        );
    }

    public void addEntry(String attribute, String value){
        data.add(new Entry(attribute, value));
    }

    /**
     * Event handler fired when the user requests a new vista.
     *
     * @param event the event that triggered the handler.
     */
    @FXML
    void nextPane(ActionEvent event) {
        VistaNavigator.loadVista(VistaNavigator.VISTA_LOGIN, parent);
    }

}