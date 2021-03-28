package core;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.EnumSet;
import java.util.List;

public class Receiver {
    private final SocketChannel channel;
    private final ByteBuffer data = ByteBuffer.allocate(1024);
    private final ByteBuffer buf = ByteBuffer.allocate(1024);
    private final Type itemsListType = new TypeToken<List<FileInfo>>(){}.getType();
    private final Type pathsListType = new TypeToken<List<Path>>(){}.getType();

    public Receiver(SocketChannel channel) {
        this.channel = channel;
    }

    public Message readMessage() throws IOException{
        data.clear();
        channel.read(data);
        String gMessage = new String(data.array());
        System.out.println(gMessage);
        JsonReader reader = new JsonReader(new StringReader(gMessage));
        reader.setLenient(true);
        return new Gson().fromJson(reader, Message.class);
    }

    private FileInfo getFileInfo() throws IOException{
        data.clear();
        channel.read(data);
        String gMessage = new String(data.array());
        JsonReader reader = new JsonReader(new StringReader(gMessage));
        reader.setLenient(true);
        System.out.println(gMessage);
        return new Gson().fromJson(reader, FileInfo.class);
    }

    public void getFile(Path dirToWrite) throws IOException{
        FileInfo fileInfo = getFileInfo();
        System.out.println(fileInfo.toString());
        Path path = Paths.get(fileInfo.getStringPath());
        System.out.println(path.getFileName());
        FileChannel fileChannel = FileChannel.open(dirToWrite.resolve(path.getFileName()), EnumSet.of(StandardOpenOption.CREATE,
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

    private void mkDirs(Path path, String fileName) {
        Path dirPath = Paths.get(path.toString().replaceFirst(fileName, ""));
        if (!Files.exists(dirPath)){
            File folder = path.toFile();
            if (!folder.exists()) {
                folder.mkdirs();
            }
        }
    }

    public String getDir() throws IOException {
        data.clear();
        channel.read(data);
        return new String(data.array());
    }

    public List<FileInfo> getFilesList() throws IOException{
        data.clear();
        channel.read(data);
        String gMessage = new String(data.array());
        JsonReader reader = new JsonReader(new StringReader(gMessage));
        reader.setLenient(true);
        return new Gson().fromJson(reader, itemsListType);
    }

    public List<Path> getAllFilesList() throws IOException {
        data.clear();
        channel.read(data);
        String gMessage = new String(data.array());
        JsonReader reader = new JsonReader(new StringReader(gMessage));
        reader.setLenient(true);
        return new Gson().fromJson(reader, pathsListType);
    }

}





