import core.Command;
import core.Message;
import core.Receiver;
import core.Sender;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class ServerMessageHandler {

    private final Message message;
    private final SocketChannel channel;

    public ServerMessageHandler(Message message, SocketChannel channel) throws IOException, ClassNotFoundException {
        this.message = message;
        this.channel = channel;
        handle();
    }


    private void handle() throws IOException, ClassNotFoundException {
        if (message.getCmd() == Command.GET_LIST){
            String stringPath = message.getFileInfo().getStringPath();
            Path path = Paths.get(stringPath);
            Sender sender = new Sender(this.channel, path);
            try {
                sender.sendFilesList();
                channel.close();
            } catch (IOException e){
                e.printStackTrace();
            }
        }

        if(message.getCmd() == Command.UPLOAD){
            Receiver receiver = new Receiver(this.channel);
            receiver.getFile();
            channel.close();
        }

        if(message.getCmd().equals(Command.DOWNLOAD)){

        }
    }

}
