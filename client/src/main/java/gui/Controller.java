package gui;

import core.*;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
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
    public TableView filesTable;
    public TextField pathField;
    private ClientChannel clientChannel;
    private Sender sender;
    private Receiver receiver;
    private final Path ROOT = Paths.get("TestDir");
    private Path currentPath = ROOT;


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
        this.clientChannel.start();


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

    public void itemExitAction(ActionEvent actionEvent) {
        this.clientChannel.close();
        Platform.exit();
    }

    public String getSelectedPath() {
        return ((FileInfo) filesTable.getSelectionModel().getSelectedItem()).getStringPath();
    }

    public void btnDelete(ActionEvent actionEvent) throws Exception {
        this.clientChannel.start();
        Path path = Paths.get(getSelectedPath());
        Message message = new Message(Command.DELETE, new FileInfo(path));
        sender = new Sender(this.clientChannel.getChannel(), message);
        sender.sendMessage();
        System.out.println("Отправлено сообщение " + message);
        updateList(currentPath);
    }

    public void btnDownload() throws Exception {
        List<String> paths;
        Path path = Paths.get(getSelectedPath());
        Message message = new Message(Command.GET_FILES_PATH, new FileInfo(path));

        DirectoryChooser dirToSave = new DirectoryChooser();
        dirToSave.setTitle("Выберите папку для загрузки файла");

        File downloadDir = dirToSave.showDialog(null);

        this.clientChannel.start();
        sender = new Sender(this.clientChannel.getChannel(), message);
        sender.sendMessage();
        receiver = new Receiver(this.clientChannel.getChannel());
        paths = receiver.getAllFilesList();
        this.clientChannel.close();

        for (String p : paths) {
            this.clientChannel.start();
            FileInfo fileInfo = new FileInfo(Paths.get(p));
            mkDirs(Paths.get(fileInfo.getStringPath()), fileInfo.getFilename());
            Message downloadMessage = new Message(Command.DOWNLOAD, fileInfo);
            System.out.println(fileInfo.toString());
            sender = new Sender(this.clientChannel.getChannel(), downloadMessage);
            sender.sendMessage();
            receiver = new Receiver(clientChannel.getChannel());
            receiver.getFile(Paths.get(downloadDir.getPath()), Paths.get(fileInfo.getFilename()), fileInfo.getSize());
        }
    }

    public void btnUpload(ActionEvent actionEvent) throws Exception {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        List<File> selectedFile = fileChooser.showOpenMultipleDialog(null);

        for (File file: selectedFile) {
            this.clientChannel.start();
            FileInfo fileInfo = new FileInfo(Paths.get(file.getPath()));
            Message message = new Message(Command.UPLOAD, new FileInfo(currentPath), fileInfo.getFilename(), fileInfo.getSize());
            sender = new Sender(this.clientChannel.getChannel(), message);
            sender.sendMessage();
            sender = new Sender(clientChannel.getChannel(), Paths.get(file.getPath()));
            System.out.println("Send file path" + file.getPath());
            sender.sendAllFilesFromDir();
        }
        updateList(currentPath);
    }

    public void btnFldCreate(ActionEvent actionEvent) throws Exception {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Inter folder name");
        dialog.setContentText("Please enter folder name:");
        dialog.showAndWait();
        String result = null;
        result = dialog.getResult();
        if (result != null) {
            dialog.close();
            System.out.println(result);
            connect();
            Message message = new Message(Command.MAKE_DIR, new FileInfo(currentPath), result);
            sender = new Sender(this.clientChannel.getChannel(), message);
            System.out.println(message.toString());
            sender.sendMessage();
        }
        updateList(currentPath);
    }


    public void btnBack(ActionEvent actionEvent) throws Exception {
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
        String token = getToken();
        Message message = new Message(Command.AUTH, token);
        sender = new Sender(this.clientChannel.getChannel(), message);
        sender.sendMessage();
        receiver = new Receiver(this.clientChannel.getChannel());
        String response =  receiver.getAuthResponse();
        return response;
    }

    private String getToken() throws IOException {
        Properties props = new Properties();
        URL url = ClassLoader.getSystemResource("token.properties");
        props.load(url.openStream());
        return props.getProperty("token");
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
