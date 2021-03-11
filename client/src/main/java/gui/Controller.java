package gui;

import core.FileInfo;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    public TableView filesTable;

    public void initialize(URL location, ResourceBundle resources) {

        TableColumn<FileInfo, String> typeColumn = new TableColumn<FileInfo, String>("Type");
        typeColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getType().getName()));
        typeColumn.setPrefWidth(50);
        typeColumn.setSortType(TableColumn.SortType.ASCENDING);

        filesTable.getColumns().addAll(typeColumn);

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
