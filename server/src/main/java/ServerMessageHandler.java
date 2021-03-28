import core.Command;
import core.Message;
import core.Receiver;
import core.Sender;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ServerMessageHandler {

    private final Message message;
    private final SocketChannel channel;

    public ServerMessageHandler(Message message, SocketChannel channel) throws IOException{
        this.message = message;
        this.channel = channel;
        handle();
        System.out.println(message.toString());
    }


    private void handle() throws IOException {
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
            Receiver receiver = new Receiver(this.channel);
            receiver.getFile(currentPath);
        }

        if(message.getCmd() == Command.MAKE_DIR) {
            Path currentPath = Paths.get(message.getFileInfo().getStringPath());
            Path dir = Paths.get(message.getAdditional());
            System.out.println(dir);
            if(!Files.exists(dir)){
                Files.createDirectory(currentPath.resolve(dir));
            }
            channel.close();
        }

//        if(message.getCmd().equals(Command.GET_FILES_PATH)){
//            String stringPath = message.getFileInfo().getStringPath();
//            System.out.println(stringPath);
//            Path path = Paths.get(stringPath);
//            Sender sender = new Sender(this.channel, path);
//            sender.sendAllFilesList();
//            channel.close();
//        }
    }

}
