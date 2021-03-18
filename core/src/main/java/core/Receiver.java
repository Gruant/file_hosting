package core;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.EnumSet;

public class Receiver {
    SocketChannel channel;
    ByteBuffer data;
    ByteBuffer buf;

    public Receiver(SocketChannel channel) {
        this.channel = channel;
        this.data = ByteBuffer.allocate(1024);
        this.buf = ByteBuffer.allocate(1024);
    }

    private FileInfo getFileInfo() throws IOException, ClassNotFoundException {
        data.clear();
        channel.read(data);
        ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(data.array()));
        FileInfo fileInfo = (FileInfo) objectInputStream.readObject();
        objectInputStream.close();
        return fileInfo;
    }

    public void getFile() throws IOException, ClassNotFoundException {
        FileInfo fileInfo = getFileInfo();
        Path path = Paths.get(fileInfo.getFilename());
        FileChannel fileChannel = FileChannel.open(path, EnumSet.of(StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE));
        buf.clear();
        int res = 1;
        while (res > 0 || buf.position() > 0) {
            res = channel.read(buf);
            buf.flip();
            fileChannel.write(buf);
            buf.compact();
            System.out.println(res);
        }
        buf.clear();
        fileChannel.close();
        channel.write(ByteBuffer.wrap("OK".getBytes()));
        channel.close();
    }
}





