import Connection.Database;
import core.Command;
import core.Message;
import core.Receiver;
import core.Sender;

import java.io.File;
import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class ServerMessageHandler {

    private final Message message;
    private final SocketChannel channel;
    private final Database db = new Database();

    public ServerMessageHandler(Message message, SocketChannel channel) throws Exception {
        this.message = message;
        this.channel = channel;
        System.out.println(message.toString());
        handle();
    }


    private void handle() throws Exception {

        if (message.getCmd() == Command.AUTH){
            String token = message.getToken();
            Database.connect();
            Boolean isUser = Database.authByToken(token);
            Database.disconnect();
            System.out.println(isUser);
            Sender sender = new Sender(this.channel);
            sender.sendAuthResponse(isUser.toString());
        }

        if (message.getCmd() == Command.GET_LIST){
            String stringPath = message.getFileInfo().getStringPath();
            Path path = Paths.get(stringPath);
            Sender sender = new Sender(this.channel, path);
            try {
                sender.sendFilesListFromDir();
                channel.close();
            } catch (IOException e){
                e.printStackTrace();
            }
        }

        if(message.getCmd() == Command.UPLOAD){
            Path currentPath = Paths.get(message.getFileInfo().getStringPath());
            Path uploadedFileName = Paths.get(message.getAdditional());
            Receiver receiver = new Receiver(this.channel);
            receiver.getFile(currentPath, uploadedFileName, message.getFileSize());
        }

        if(message.getCmd() == Command.MAKE_DIR) {
            Path currentPath = Paths.get(message.getFileInfo().getStringPath());
            Path dir = Paths.get(message.getAdditional());
            System.out.println(dir);
            if(!Files.exists(dir)){
                Files.createDirectory(currentPath.resolve(dir));
            }
        }

        if(message.getCmd().equals(Command.GET_FILES_PATH)){
            String stringPath = message.getFileInfo().getStringPath();
            System.out.println(stringPath);
            Path path = Paths.get(stringPath);
            Sender sender = new Sender(this.channel, path);
            sender.sendAllFilesList();
            channel.close();
        }

        if(message.getCmd().equals(Command.DOWNLOAD)){
            String stringPath = message.getFileInfo().getStringPath();
            System.out.println("Отправлен файл: " + stringPath);
            Path path = Paths.get(stringPath);
            Sender sender = new Sender(this.channel, path);
            sender.sendAllFilesFromDir();
        }

        if(message.getCmd().equals(Command.DELETE)){

            String stringPath = message.getFileInfo().getStringPath();
            File file = new File(stringPath);
            rmFile(file);
            channel.close();
        }
    }

    public void rmFile(File file) {
        if (file.isDirectory()) {
            File[] list = file.listFiles();
            if (list != null) {
                for (int i = 0; i < list.length; i++) {
                    File tmpF = list[i];
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
