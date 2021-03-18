package gui;

import core.*;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class Controller implements Initializable {
    public TableView filesTable;
    private ClientChannel clientChannel;
    private Sender sender;
    private Receiver receiver;
    private Path path = Paths.get("TestDir");
    private Message message;


    public void initialize(URL location, ResourceBundle resources) {
        try {
            this.clientChannel = new ClientChannel();
        } catch (Exception e) {
            e.printStackTrace();
        }

        TableColumn<FileInfo, String> typeColumn = new TableColumn<FileInfo, String>("Type");
        typeColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getType().getName()));
        typeColumn.setPrefWidth(50);

        TableColumn<FileInfo, String> fileNameColumn = new TableColumn<>("File Name");
        fileNameColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getFilename()));
        fileNameColumn.setPrefWidth(600);

        filesTable.getColumns().addAll(typeColumn, fileNameColumn);

        try {
            updateList();
        } catch (Exception exception) {
            exception.printStackTrace();
        }

    }

    public void updateList() throws Exception {
            connect();
            FileInfo requestedDir = new FileInfo(path);
            this.message = new Message(Message.Command.GET_LIST, requestedDir);
            sender = new Sender(this.clientChannel.getChannel(), this.message);
            receiver = new Receiver(this.clientChannel.getChannel());
            sender.sendMessage();
            List<FileInfo> filesList = receiver.getFilesList();
            filesTable.getItems().clear();
            filesTable.getItems().addAll(filesList);
            filesTable.sort();
    }

    public void itemExitAction(ActionEvent actionEvent) {
        Platform.exit();
    }

    public void btnDelete(ActionEvent actionEvent) {
    }

    public void btnDownload(ActionEvent actionEvent) throws Exception {
        connect();
    }

    public void btnUpload(ActionEvent actionEvent) {
    }

    public void btnAuth(ActionEvent actionEvent) {
    }

    public void btnFldCreate(ActionEvent actionEvent) {
    }

    public void connect() throws Exception {
        this.clientChannel = new ClientChannel();
    }


}
