package core;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.EnumSet;

public class Receiver {
    SocketChannel channel;
    Selector selector;

    ByteBuffer data;
    ByteBuffer buf;
    ByteBuffer command;

    public Receiver(SocketChannel channel, Selector selector) {

        this.channel = channel;
        this.selector = selector;
        this.data = ByteBuffer.allocate(1024);
        this.buf = ByteBuffer.allocate(1024);
        this.command = ByteBuffer.allocate(4);
    }

    private FileInfo getFileInfo() throws IOException, ClassNotFoundException {
        channel.read(data);
        ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(data.array()));
        FileInfo fileInfo = (FileInfo) objectInputStream.readObject();
        System.out.println(fileInfo.getFilename());
        objectInputStream.close();
        return fileInfo;
    }

    public void getFile() throws IOException, ClassNotFoundException {
        FileInfo fileInfo = getFileInfo();
        System.out.println(fileInfo.getFilename());
        Path path = Paths.get(fileInfo.getFilename());
        FileChannel fileChannel = FileChannel.open(path, EnumSet.of(StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE));
        int res = 1;
        int count = 0;
        while (count != fileInfo.getSize()) {
            buf.clear();
            res = channel.read(buf);
            System.out.println(res);
            System.out.println(res);
            buf.flip();
            if (res > 0) {
                fileChannel.write(buf);
                count+=res;
            }
            buf.compact();
        }
        buf.clear();
        System.out.println("File is read");
        fileChannel.close();
        channel.write(ByteBuffer.wrap("OK".getBytes()));
    }
}





