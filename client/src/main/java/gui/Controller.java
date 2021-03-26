package gui;

import core.*;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    public TableView filesTable;
    public TextField pathField;
    private ClientChannel clientChannel;
    private Sender sender;
    private Receiver receiver;
    private Path root = Paths.get("TestDir");
    private Path currentPath = root;
    private Message message;


    public void initialize(URL location, ResourceBundle resources) {
        try {
            this.clientChannel = new ClientChannel();
        } catch (Exception e) {
            e.printStackTrace();
        }

        TableColumn<FileInfo, String> typeColumn = new TableColumn<>("Type");
        typeColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getType().getName()));
        typeColumn.setPrefWidth(50);

        TableColumn<FileInfo, String> fileNameColumn = new TableColumn<>("File Name");
        fileNameColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getFilename()));
        fileNameColumn.setPrefWidth(600);

        filesTable.getColumns().addAll(typeColumn, fileNameColumn);
        filesTable.getSortOrder().addAll(typeColumn);

        filesTable.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getClickCount() == 2) {
                    Path path = Paths.get(((FileInfo) filesTable.getSelectionModel().getSelectedItem()).getStringPath());
                    if(Files.isDirectory(path)){
                        try {
                            updateList(path);
                        } catch (Exception exception) {
                            exception.printStackTrace();
                        }
                    }
                }
            }
        });

        try {
            updateList(root);
        } catch (Exception exception) {
            exception.printStackTrace();
        }

    }

    public void updateList(Path path) throws Exception {
        connect();

        FileInfo requestedDir = new FileInfo(path);
        this.message = new Message(Command.GET_LIST, requestedDir);

        sender = new Sender(this.clientChannel.getChannel(), this.message);
        receiver = new Receiver(this.clientChannel.getChannel());
        sender.sendMessage();

        List<FileInfo> filesList = receiver.getFilesList();

        currentPath = path;
        pathField.setText(currentPath.toString());
        filesTable.getItems().clear();
        filesTable.getItems().addAll(filesList);
        filesTable.sort();
    }

    public void itemExitAction(ActionEvent actionEvent) {
        Platform.exit();
    }

    public String getSelectedFileName() {
        return ((FileInfo) filesTable.getSelectionModel().getSelectedItem()).getFilename();
    }

    public String getSelectedPath() {
        return ((FileInfo) filesTable.getSelectionModel().getSelectedItem()).getStringPath();
    }


    public void btnDelete(ActionEvent actionEvent) {
    }

    public void btnDownload(ActionEvent actionEvent) throws Exception {
//        String home = System.getProperty("user.home");
//        File file = new File(home+"/Downloads/" + fileName + ".txt");

        connect();
        Path path = Paths.get(getSelectedPath());
        Message message = new Message(Command.DOWNLOAD, new FileInfo(path));
        sender = new Sender(this.clientChannel.getChannel(), message);
        sender.sendMessage();



    }

    public void btnUpload(ActionEvent actionEvent) throws Exception {
        connect();
//        Взять файл из кнопки
        sender = new Sender(this.clientChannel.getChannel());
        List<Path> paths = sender.getFiles();
        for (Path path: paths) {
            clientChannel.start();
            sender = new Sender(clientChannel.getChannel(), path);
            sender.sendAllFilesFromDir();
        }
    }

    public void btnAuth(ActionEvent actionEvent) {
    }

    public void btnFldCreate(ActionEvent actionEvent) {
    }

    public void connect() throws Exception {
        this.clientChannel = new ClientChannel();
        this.clientChannel.start();

    }

    public void auth() throws IOException {
        Properties props = new Properties();
        props.load(new FileInputStream("token.properties"));
        String token = props.getProperty("token");

    }

    public void btnBack(ActionEvent actionEvent) throws Exception {
        Path upperPath = Paths.get(pathField.getText()).getParent();
        if (upperPath != null) {
            updateList(upperPath);
        }
    }

//    public static void openContainingFolder(Path path) throws IOException {
//        Path dirPath = Paths.get(path.toString().replaceFirst(String.valueOf(path.getFileName()), ""));
//        System.out.println(dirPath.toString());
//        if (!Files.exists(dirPath)){
//            File folder = dirPath.toFile();
//            if (!folder.exists()) {
//                folder.mkdirs();
//            }
//        }
//    }
//
//    public static void openFolder(File folder) throws IOException {
//        if (Desktop.isDesktopSupported()) {
//            Desktop.getDesktop().open(folder);
//        }
//    }


}
