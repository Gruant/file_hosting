package core;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

public class ClientChannel {
    private SocketChannel channel;

    public void start() {
                try {
                    channel = SocketChannel.open(new InetSocketAddress("localhost", 9999));

                } catch (IOException e) {
                    e.printStackTrace();
                }
    }

    public SocketChannel getChannel() {
        return channel;
    }

    public void close(){
        try {
            channel.close();
        }catch (IOException  e){
            e.printStackTrace();
        }

    }
}
