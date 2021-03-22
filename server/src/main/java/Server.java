
import core.Receiver;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    private static final ExecutorService READ_THREAD_POOL = Executors.newFixedThreadPool(2);
    private static final ExecutorService WRITE_THREAD_POOL = Executors.newFixedThreadPool(2);

    private final Selector acceptSelector;

    private ServerSocketChannel serverSocket;
    private Receiver receiver;


    public Server() throws IOException {
        acceptSelector = Selector.open();
        serverSocket = ServerSocketChannel.open();
        serverSocket.socket().bind(new InetSocketAddress("localhost", 9999));
        serverSocket.configureBlocking(false);
        serverSocket.register(acceptSelector, SelectionKey.OP_ACCEPT);
    }


    public static void main(String[] args) throws IOException, ClassNotFoundException {
        try {
            new Server().start();
        } finally {
            READ_THREAD_POOL.shutdownNow();
            WRITE_THREAD_POOL.shutdownNow();
        }
    }

    public void start() throws IOException, ClassNotFoundException {
        while (true) {
            acceptSelector(acceptSelector);
        }
    }


    public void acceptSelector (Selector selector) throws IOException, ClassNotFoundException {
        selector.select();

        Set<SelectionKey> selectionKeys = acceptSelector.selectedKeys();
        Iterator<SelectionKey> keyIterator = selectionKeys.iterator();

        while (keyIterator.hasNext()) {
            SelectionKey key = keyIterator.next();
            if (key.isAcceptable()) {
                System.out.println("Acceptable event on acceptSelector");
                register(acceptSelector, serverSocket);
            }

            if (key.isReadable()) {
                System.out.println("Start reading");
                readMessage(key);
                System.out.println("Reading done");
            }

            keyIterator.remove();
        }
    }



    public void register (Selector selector, ServerSocketChannel serverSocketChannel) throws IOException {
        SocketChannel client = serverSocketChannel.accept();
        client.configureBlocking(false);
        client.register(selector, SelectionKey.OP_READ);
        System.out.println("User is connect");
    }

    public void readMessage(SelectionKey key) throws IOException, ClassNotFoundException {
        SocketChannel client = (SocketChannel) key.channel();
        receiver = new Receiver(client);
        ServerMessageHandler handler = new ServerMessageHandler(receiver.readMessage(), client);
    }
}
