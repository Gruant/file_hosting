package gui;

import core.*;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Controller implements Initializable {
    @FXML
    public TableView filesTable;
    @FXML
    public TextField pathField;

    private ClientChannel clientChannel;
    private Sender sender;
    private Receiver receiver;
    private final Path ROOT;
    private Path currentPath;

    public Controller() throws IOException {
        ROOT = Paths.get(getProperties().getProperty("folder"));
        currentPath = ROOT;
    }


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

        filesTable.setOnMouseClicked(event -> {
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
        });

        try {
            updateList(ROOT);
        } catch (Exception exception) {
            exception.printStackTrace();
        }

    }

    public void updateList(Path path) throws Exception {
        connect();


        FileInfo requestedDir = new FileInfo(path);
        Message message = new Message(Command.GET_LIST, requestedDir);

        sender = new Sender(this.clientChannel.getChannel(), message);
        receiver = new Receiver(this.clientChannel.getChannel());
        sender.sendMessage();

        List<FileInfo> filesList = receiver.getFilesList();

        currentPath = path;
        pathField.setText(currentPath.toString());
        filesTable.getItems().clear();
        filesTable.getItems().addAll(filesList);
        filesTable.sort();
    }

    public void itemExitAction() {
        this.clientChannel.close();
        Platform.exit();
    }

    public String getSelectedPath() {
        return ((FileInfo) filesTable.getSelectionModel().getSelectedItem()).getStringPath();
    }

    public void btnDelete() throws Exception {
        connect();
        Path path = Paths.get(getSelectedPath());
        Message message = new Message(Command.DELETE, new FileInfo(path));
        sender = new Sender(this.clientChannel.getChannel(), message);
        sender.sendMessage();
        updateList(currentPath);
    }

    public void btnDownload() throws Exception {
        List<String> paths;
        Path path = Paths.get(getSelectedPath());
        Message message = new Message(Command.GET_FILES_PATH, new FileInfo(path));

        DirectoryChooser dirToSave = new DirectoryChooser();
        dirToSave.setTitle("Выберите папку для загрузки файла");

        File downloadDir = dirToSave.showDialog(null);

        connect();
        sender = new Sender(this.clientChannel.getChannel(), message);
        sender.sendMessage();
        receiver = new Receiver(this.clientChannel.getChannel());
        paths = receiver.getAllFilesList();
        this.clientChannel.close();

        for (String p : paths) {
            connect();
            FileInfo fileInfo = new FileInfo(Paths.get(p));
            mkDirs(Paths.get(fileInfo.getStringPath()), fileInfo.getFilename());
            Message downloadMessage = new Message(Command.DOWNLOAD, fileInfo);
            sender = new Sender(this.clientChannel.getChannel(), downloadMessage);
            sender.sendMessage();
            receiver = new Receiver(clientChannel.getChannel());
            receiver.getFile(Paths.get(downloadDir.getPath()), Paths.get(fileInfo.getFilename()), fileInfo.getSize());
        }
    }

    public void btnUpload() throws Exception {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        List<File> selectedFile = fileChooser.showOpenMultipleDialog(null);

        for (File file: selectedFile) {
            connect();
            FileInfo fileInfo = new FileInfo(Paths.get(file.getPath()));
            Message message = new Message(Command.UPLOAD, new FileInfo(currentPath), fileInfo.getFilename(), fileInfo.getSize());
            sender = new Sender(this.clientChannel.getChannel(), message);
            sender.sendMessage();
            sender = new Sender(clientChannel.getChannel(), Paths.get(file.getPath()));
            sender.sendAllFilesFromDir();
        }
        updateList(currentPath);
    }

    public void btnFldCreate() throws Exception {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Inter folder name");
        dialog.setContentText("Please enter folder name:");
        dialog.showAndWait();
        String result;
        result = dialog.getResult();
        if (result != null) {
            dialog.close();
            System.out.println(result);
            connect();
            Message message = new Message(Command.MAKE_DIR, new FileInfo(currentPath), result);
            sender = new Sender(this.clientChannel.getChannel(), message);
            sender.sendMessage();
        }
        updateList(currentPath);
    }


    public void btnBack() throws Exception {
        Path upperPath = Paths.get(pathField.getText()).getParent();
        if (upperPath != null) {
            updateList(upperPath);
        }
    }

    private void mkDirs(Path path, String fileName) {
        Path dirPath = Paths.get(path.toString().replaceFirst(fileName, ""));
        if (!Files.exists(dirPath)){
            File folder = path.toFile();
            if (!folder.exists()) {
                folder.mkdirs();
            }
        }
    }

    public void connect() throws Exception {
        this.clientChannel.start();
        String response = isAuthByToken();
        if(response.equals("false")){
            this.clientChannel.close();
        }
        System.out.println(this.clientChannel.toString());

    }

    private String isAuthByToken() throws IOException {
        String token = getProperties().getProperty("token");
        Message message = new Message(Command.AUTH, token);
        sender = new Sender(this.clientChannel.getChannel(), message);
        sender.sendMessage();
        receiver = new Receiver(this.clientChannel.getChannel());
        return receiver.getAuthResponse();
    }

    private Properties getProperties() throws IOException {
        Properties props = new Properties();
        URL url = ClassLoader.getSystemResource("token.properties");
        props.load(url.openStream());
        return props;
    }


    private void setToken(String token){

    }

    public boolean authByLoginPassword(){
//        отправить объект user
//        получить объект user
//        записать папку и токен
        return false;
    }
}
