package application;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextArea;

import java.io.IOException;

public class VistaReportViewerController implements VistaContainable {
    private VistaContainer parent;
    @FXML private TextArea textArea;

    public VistaReportViewerController(VistaContainer parent, String raport){
        this.init(parent);
        textArea.setText(raport);
    }

    @Override
    public void init(VistaContainer parent){
        this.parent = parent;
        FXMLLoader loader = new FXMLLoader(getClass().getResource(VistaNavigator.VISTA_REPORT_VIEWER));
        loader.setController(this);
        try {
            parent.setVista(loader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}