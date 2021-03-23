package core;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import java.io.*;
import java.lang.reflect.Type;
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
    Gson gson = new Gson();
    Type itemsListType = new TypeToken<List<FileInfo>>() {}.getType();

    public Receiver(SocketChannel channel) {
        this.channel = channel;
    }

    public Message readMessage() throws IOException, ClassNotFoundException {
        data.clear();
        channel.read(data);
        String gMessage = new String(data.array());
        JsonReader reader = new JsonReader(new StringReader(gMessage));
        reader.setLenient(true);
        return gson.fromJson(reader, Message.class);
    }

    private FileInfo getFileInfo() throws IOException, ClassNotFoundException {
        data.clear();
        channel.read(data);
        String gMessage = new String(data.array());
        JsonReader reader = new JsonReader(new StringReader(gMessage));
        reader.setLenient(true);
        return gson.fromJson(reader, FileInfo.class);
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
        String gMessage = new String(data.array());
        JsonReader reader = new JsonReader(new StringReader(gMessage));
        reader.setLenient(true);
        return new Gson().fromJson(reader,itemsListType);
    }

}





