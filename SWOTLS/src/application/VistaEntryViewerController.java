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
    @FXML private Button actionButton;
    private boolean editing = false;

    public VistaEntryViewerController(VistaContainer parent){
        this.init(parent);
    }

    @Override
    public void init(VistaContainer parent){
        this.parent = parent;
        FXMLLoader loader = new FXMLLoader(getClass().getResource(VistaNavigator.VISTA_ENTRY));
        loader.setController(this);
        try {
            parent.setVista(loader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }

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

    protected VistaContainer getParent() {
        return parent;
    }

    public void addEntry(String attribute, String value){
        data.add(new Entry(attribute, value));
    }

    public void setEditing(boolean editing) {
        this.editing = editing;
        if(editing){
            actionButton.setText("Zapisz");
            valueColumn.setEditable(true);
        }else{
            actionButton.setText("Edytuj");
            valueColumn.setEditable(false);
        }
    }

    @FXML
    private void buttonEdit(ActionEvent event) {
        Dialogs.error("Nie można edytować tego typu danych.");

        /*if(VistaLogInController.hasOrganizerPermissions()){
            if(editing){
                //Zapisz
                ServerData.saveEntry(table.itemsProperty().getValue(), type);
            }
            setEditing(!editing);
        }else {
            Dialogs.insufficientPermissions();
        }*/
    }

    @FXML
    private void buttonDelete(ActionEvent event) {
        Dialogs.error("Nie można usuwać tego typu danych.");

        /*if(VistaLogInController.hasOrganizerPermissions()){
            int id = Integer.valueOf(data.get(0).getValue()); //Integer.valueOf(table.itemsProperty().getValue().get(0).getValue());
            if( id>=0 ){
                //Usuń z bazy
                ServerData.deleteEntry(id, type);
            }
            parent.close();
        }else {
            Dialogs.insufficientPermissions();
        }*/
    }

    private class EditingCell extends TableCell<Entry, String> {

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