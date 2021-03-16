package core;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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

    ByteBuffer data = ByteBuffer.allocate(1024);
    ByteBuffer buf = ByteBuffer.allocate(1024);

    public Receiver(SocketChannel channel) {
        this.channel = channel;
    }

    private FileInfo getFileInfo() throws IOException, ClassNotFoundException {
        channel.read(data);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data.array());
        ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
        FileInfo fileInfo = (FileInfo) objectInputStream.readObject();
        data.clear();
        System.out.println("Get FileInfo");
        objectInputStream.close();
        byteArrayInputStream.close();
        return fileInfo;
    }

    public void getFile() throws IOException, ClassNotFoundException {
        FileInfo file = getFileInfo();
        System.out.println(file.getFilename());
        Path path = Paths.get(file.getFilename());
        FileChannel fileChannel = FileChannel.open(path, EnumSet.of(StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE));

        while(channel.read(buf) > 0){
            buf.flip();
            fileChannel.write(buf);
            buf.compact();
        }

        channel.write(ByteBuffer.wrap("Done".getBytes()));
        fileChannel.close();
        buf.clear();
        System.out.println("Done");
    }
}




