import Connection.Database;
import com.google.gson.JsonSyntaxException;
import core.*;

import java.io.File;
import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ServerMessageHandler {

    private final Message message;
    private final SocketChannel channel;
    private Boolean isUser;

    public ServerMessageHandler(Message message, SocketChannel channel) throws Exception {
        this.message = message;
        this.channel = channel;
        handle();
    }


    private void handle() throws Exception {
        try {
            if(message.getCmd() == Command.REGISTRATION){
                User user = message.getUser();
                Database.connect();
                Database.createUser(user);
                User bdUser = Database.UserInfo(user);
                Sender userSender = new Sender(this.channel);
                if(!Files.exists(Paths.get(bdUser.getFolder()))){
                    new File(user.getFolder()).mkdir();
                }
                userSender.sendUserInfo(bdUser);
                Database.disconnect();
            }

            if (message.getCmd() == Command.AUTH_BY_TOKEN) {
                String token = message.getToken();
                Database.connect();
                isUser = Database.authByToken(token);
                Database.disconnect();
                Sender sender = new Sender(this.channel);
                sender.sendAuthResponse(isUser.toString());
            }

            if (message.getCmd() == Command.AUTH_BY_USER_INFO) {
                User user = message.getUser();
                Database.connect();
                isUser = Database.authByLogPass(user);
                Sender sender = new Sender(this.channel);
                sender.sendAuthResponse(isUser.toString());
                if (isUser) {
                    Sender userSender = new Sender(this.channel);
                    User bdUser = Database.UserInfo(user);
                    if(!Files.exists(Paths.get(bdUser.getFolder()))){
                        new File(user.getFolder()).mkdir();
                    }
                    userSender.sendUserInfo(bdUser);
                }
                Database.disconnect();
                System.out.println(isUser);
            }

            if (message.getCmd() == Command.GET_LIST) {
                String stringPath = message.getFileInfo().getStringPath();
                Path path = Paths.get(stringPath);
                Sender sender = new Sender(this.channel, path);
                try {
                    sender.sendFilesListFromDir();
                    channel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (message.getCmd() == Command.UPLOAD) {
                Path currentPath = Paths.get(message.getFileInfo().getStringPath());
                Path uploadedFileName = Paths.get(message.getAdditional());
                Receiver receiver = new Receiver(this.channel);
                receiver.getFile(currentPath, uploadedFileName, message.getFileSize());
            }

            if (message.getCmd() == Command.MAKE_DIR) {
                Path currentPath = Paths.get(message.getFileInfo().getStringPath());
                Path dir = Paths.get(message.getAdditional());
                if (!Files.exists(dir)) {
                    Files.createDirectory(currentPath.resolve(dir));
                }
            }

            if (message.getCmd().equals(Command.GET_FILES_PATH)) {
                String stringPath = message.getFileInfo().getStringPath();
                Path path = Paths.get(stringPath);
                Sender sender = new Sender(this.channel, path);
                sender.sendAllFilesList();
                channel.close();
            }

            if (message.getCmd().equals(Command.DOWNLOAD)) {
                String stringPath = message.getFileInfo().getStringPath();
                Path path = Paths.get(stringPath);
                Sender sender = new Sender(this.channel, path);
                sender.sendAllFilesFromDir();
            }

            if (message.getCmd().equals(Command.DELETE)) {

                String stringPath = message.getFileInfo().getStringPath();
                File file = new File(stringPath);
                rmFile(file);
                channel.close();
            }
        } catch (JsonSyntaxException e) {
            throw new JsonSyntaxException("Ошибка с синтаксисом Json");
        } catch (NullPointerException e){
            channel.close();
        }
    }

    public void rmFile(File file) {
        if (file.isDirectory()) {
            File[] list = file.listFiles();
            if (list != null) {
                for (File tmpF : list) {
                    if (tmpF.isDirectory()) {
                        rmFile(tmpF);
                    }
                    tmpF.delete();
                }
            }
            if (!file.delete()) {
                System.out.println("can't delete folder : " + file);
            }
        }
        if (file.isFile()){
            file.delete();
        }
    }
}
