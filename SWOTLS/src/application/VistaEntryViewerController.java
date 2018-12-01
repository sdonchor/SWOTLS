package application;

import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.io.IOException;

public class VistaEntryViewerController implements VistaContainable {
    private VistaContainer parent;
    private ObservableList<Entry> data;
    @FXML private TableView<Entry> table;
    @FXML private TableColumn<Entry, String> attributeColumn;
    @FXML private TableColumn<Entry, String> valueColumn;
    @FXML private Button button;
    private boolean editing = false;
    private String type;

    public VistaEntryViewerController(VistaContainer parent, String type){
        this.parent = parent;
        this.type = type;
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

        valueColumn.setCellFactory(col -> new EditingCell());
        valueColumn.setEditable(false);
        /*valueColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Entry, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<Entry, String> t) {
                ((Entry) t.getTableView().getItems().get(t.getTablePosition().getRow())).setValue(t.getNewValue());
            }
        });*/
    }

    public void addEntry(String attribute, String value){
        data.add(new Entry(attribute, value));
    }

    public void setEditing(boolean editing) {
        this.editing = editing;
        if(editing){
            button.setText("Zapisz");
            valueColumn.setEditable(true);
        }else{
            button.setText("Edytuj");
            valueColumn.setEditable(false);
        }
    }

    /**
     * Event handler fired when the user requests a new vista.
     *
     * @param event the event that triggered the handler.
     */
    @FXML
    void buttonAction(ActionEvent event) {
        if(VistaLogInController.hasOrganizerPermissions()){
            if(editing){
                //Zapisz
                ServerData.saveData(table.itemsProperty().getValue(), type);
            }
            setEditing(!editing);
        }else {
            Dialogs.error("Niewystarczające uprawnienia.");
            VistaNavigator.loadVista(VistaNavigator.VISTA_LOGIN, parent);
        }
    }

    class EditingCell extends TableCell<Entry, String> {

        private TextField textField;

        public EditingCell() {}

        @Override
        public void startEdit() {
            super.startEdit();

            if (textField == null) {
                createTextField();
            }

            setGraphic(textField);
            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            textField.selectAll();
        }

        @Override
        public void cancelEdit() {
            super.cancelEdit();

            // Check to see if there's a value
            if (!textField.getText().equals("")) {
                commitEdit(textField.getText());
                setText( textField.getText() );
            }

            setContentDisplay(ContentDisplay.TEXT_ONLY);
        }

        @Override
        public void commitEdit(String newValue) {
            super.commitEdit(newValue);
            if(!newValue.isEmpty())
                getTableView().getItems().get(getIndex()).setValue(textField.getText());
        }

        @Override
        public void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);

            if (empty) {
                setText(null);
                setGraphic(null);
            } else {
                if (isEditing()) {
                    if (textField != null) {
                        textField.setText(getString());
                    }
                    setGraphic(textField);
                    setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                } else {
                    setText(getString());
                    setContentDisplay(ContentDisplay.TEXT_ONLY);
                }
            }
        }

        private void createTextField() {
            textField = new TextField(getString());
            textField.setMinWidth(this.getWidth() - this.getGraphicTextGap()*2);
            textField.setOnKeyPressed(new EventHandler<KeyEvent>() {

                @Override
                public void handle(KeyEvent t) {
                    if (t.getCode() == KeyCode.ENTER) {
                        commitEdit(textField.getText());
                    } else if (t.getCode() == KeyCode.ESCAPE) {
                        textField.setText("");
                        textField.cancelEdit();
                    }
                }
            });
            textField.focusedProperty().addListener(
                    (ObservableValue<? extends Boolean> arg0, Boolean arg1, Boolean arg2)
                            -> {
                        if (!arg2) {
                            commitEdit(textField.getText());
                        }
                    });
        }

        private String getString() {
            return getItem() == null ? "" : getItem().toString();
        }
    }

}