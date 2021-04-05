package core;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
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
    private final Type userType = new TypeToken<User>(){}.getType();


    public Receiver(SocketChannel channel) {
        this.channel = channel;
    }

    public Message readMessage() throws IOException{

        try {
            data.clear();
            channel.read(data);
            String gMessage = new String(data.array());
            JsonReader reader = new JsonReader(new StringReader(gMessage));
            reader.setLenient(true);
            return gson.fromJson(reader, Message.class);
        } catch (JsonSyntaxException e) {
            channel.close();
        }
        return null;
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
            channel.write(ByteBuffer.wrap("OK".getBytes()));
        } catch (IOException e){
            throw new IOException("Не удалось записать файл");
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

    public User getUserInfo() throws IOException {
        data.clear();
        channel.read(data);
        String gMessage = new String(data.array());
        JsonReader reader = new JsonReader(new StringReader(gMessage));
        reader.setLenient(true);
        return gson.fromJson(reader, userType);
    }


}





