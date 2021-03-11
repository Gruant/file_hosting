package gui;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.TableView;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    public TableView filesTable;

    public void initialize(URL location, ResourceBundle resources) {

    }

    public void itemExitAction(ActionEvent actionEvent) {
        Platform.exit();
    }

    public void btnDelete(ActionEvent actionEvent) {
    }

    public void btnDownload(ActionEvent actionEvent) {
    }

    public void btnUpload(ActionEvent actionEvent) {
    }

    public void btnAuth(ActionEvent actionEvent) {
    }

    public void btnFldCreate(ActionEvent actionEvent) {
    }
}
