
import core.Message;
import core.Receiver;
import core.Sender;

import java.io.IOException;
import java.nio.channels.SocketChannel;

import java.nio.file.Path;

public class ServerMessageHandler {

    private Message message;
    private SocketChannel channel;
    private Receiver receiver;
    private Sender sender;
    private Path path;

    public ServerMessageHandler(Message message, SocketChannel channel) throws IOException {
        this.message = message;
        this.channel = channel;
        handle();
    }


    private void handle() throws IOException {
        if (message.getCmd() == Message.Command.GET_LIST){
            Path path = message.getFileInfo().getPath();
            sender = new Sender(this.channel, path);
            try {
                sender.sendFilesList();
                channel.close();
            } catch (IOException e){
                e.printStackTrace();
            }
        }
    }


}
