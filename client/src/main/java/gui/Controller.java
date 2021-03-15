package gui;

import core.FileInfo;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Properties;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    public TableView filesTable;
    private SocketChannel channel;

    public void initialize(URL location, ResourceBundle resources) {
        try {
            connect();
        } catch (IOException e) {
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

    public void connect() throws IOException {
        channel = SocketChannel.open(new InetSocketAddress("localhost", 9999));
        Properties prop = new Properties();
        InputStream in = getClass().getResourceAsStream("../token.properties");
        prop.load(in);
        channel.write(ByteBuffer.wrap(prop.getProperty("token").getBytes()));
    }
}
