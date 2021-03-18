package gui;

import core.ClientChannel;
import core.FileInfo;
import core.Sender;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
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
    private final Path path = Paths.get("/Users/antongrutsin/Desktop/CB_logo/");

    public void initialize(URL location, ResourceBundle resources) {
        try {
            sendFiles();
        } catch (Exception e) {
            e.printStackTrace();
        }

        TableColumn<FileInfo, String> typeColumn = new TableColumn<FileInfo, String>("Type");
        typeColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getType().getName()));
        typeColumn.setPrefWidth(50);
        typeColumn.setSortType(TableColumn.SortType.ASCENDING);

        TableColumn<FileInfo, String> fileNameColumn = new TableColumn<>("File Name");
        fileNameColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getFilename()));

        filesTable.getColumns().addAll(typeColumn, fileNameColumn);

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

    public void connect() throws Exception {

    }

    public void sendFiles() throws Exception {
        this.clientChannel = new ClientChannel();
        List<Path> paths = getFiles(this.path);
        for (Path path: paths) {
            clientChannel.start();
            sender = new Sender(clientChannel.getChannel(), path);
            sender.sendAllFilesFromDir();
        }
    }

    public List<Path> getFiles(Path path) throws IOException {
        List<Path> paths = Files.walk(this.path)
                .filter(Files::isRegularFile)
                .collect(Collectors.toList());
        System.out.println(Arrays.toString(paths.toArray()));
        return paths;
    }
}
