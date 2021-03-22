package core;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.EnumSet;
import java.util.List;

public class Receiver {
    SocketChannel channel;
    ByteBuffer data = ByteBuffer.allocate(1024);
    ByteBuffer buf = ByteBuffer.allocate(1024);
    Message message;

    public Receiver(SocketChannel channel) {
        this.channel = channel;
    }

    public Message readMessage() throws IOException, ClassNotFoundException {
        data.clear();
        channel.read(data);
        ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(data.array()));
        Message message = (Message) objectInputStream.readObject();
        objectInputStream.close();
        System.out.println("Read message: " + message);
        return message;
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
        Path path = Paths.get("TestDir" + File.separator + fileInfo.getFilename());
        FileChannel fileChannel = FileChannel.open(path, EnumSet.of(StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE));
        buf.clear();
        int res = 1;
        while (res > 0 || buf.position() > 0) {
            res = channel.read(buf);
            buf.flip();
            if (res > 0) {
                fileChannel.write(buf);
            }
            buf.compact();
            System.out.println(res);
        }
        fileChannel.close();
        channel.write(ByteBuffer.wrap("OK".getBytes()));
        channel.close();
    }

    public List<FileInfo> getFilesList() throws IOException, ClassNotFoundException {
        data.clear();
        channel.read(data);
        ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(data.array()));
        List<FileInfo> filesList = (List<FileInfo>) objectInputStream.readObject();
        objectInputStream.close();
        return filesList;
    }

}





