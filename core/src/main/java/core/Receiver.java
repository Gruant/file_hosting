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
import java.nio.file.StandardOpenOption;
import java.util.EnumSet;
import java.util.List;

public class Receiver {
    private final SocketChannel channel;
    private final ByteBuffer data = ByteBuffer.allocate(2048);
    private final Gson gson = new Gson();
    private final Type itemsListType = new TypeToken<List<FileInfo>>(){}.getType();
    private final Type pathsListType = new TypeToken<List<String>>(){}.getType();


    public Receiver(SocketChannel channel) {
        this.channel = channel;
    }

    public Message readMessage() throws IOException{
        data.clear();
        channel.read(data);
        String gMessage = new String(data.array());
        JsonReader reader = new JsonReader(new StringReader(gMessage));
        reader.setLenient(true);
        return gson.fromJson(reader, Message.class);
    }

    public void getFile(Path dirToWrite, Path uploadedFileName, Long size) throws IOException {
        System.out.println(uploadedFileName);
        ByteBuffer buf = ByteBuffer.allocate(1024);
        try {
            FileChannel fileChannel = FileChannel.open(dirToWrite.resolve(uploadedFileName), EnumSet.of(StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE));
            int res = 1;
            long getSize = 0;
            while (getSize != size && res > 0){
                res = channel.read(buf);
                getSize += res;
                System.out.println(res);
                buf.flip();
                if (res > 0) {
                    fileChannel.write(buf);
                }
                buf.compact();
            }
//
            System.out.println("Дj отправки ответа");
            channel.write(ByteBuffer.wrap("OK".getBytes()));
            System.out.println("После отправки ответа");
        } catch (IOException e){
            channel.write(ByteBuffer.wrap("NO".getBytes()));
        } finally {
            channel.close();
        }
    }

    public List<FileInfo> getFilesList() throws IOException{
        data.clear();
        channel.read(data);
        String gMessage = new String(data.array());
        JsonReader reader = new JsonReader(new StringReader(gMessage));
        reader.setLenient(true);
        return gson.fromJson(reader, itemsListType);
    }

    public List<String> getAllFilesList() throws IOException {
        data.clear();
        channel.read(data);
        String gMessage = new String(data.array());
        System.out.println("Получено: " + gMessage );
        JsonReader reader = new JsonReader(new StringReader(gMessage));
        reader.setLenient(true);
        return gson.fromJson(reader, pathsListType);
    }

    public String getAuthResponse() throws IOException {
        data.clear();
        channel.read(data);
        String gMessage = new String(data.array());
        JsonReader reader = new JsonReader(new StringReader(gMessage));
        reader.setLenient(true);
        return gson.fromJson(reader, String.class);
    }

    public String getTokenFromServer() throws IOException {
        data.clear();
        channel.read(data);
        String gMessage = new String(data.array());
        JsonReader reader = new JsonReader(new StringReader(gMessage));
        reader.setLenient(true);
        return gson.fromJson(reader, String.class);
    }

}





