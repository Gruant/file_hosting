package core;

import com.google.gson.Gson;
import com.google.gson.internal.bind.util.ISO8601Utils;
import org.w3c.dom.ls.LSOutput;

import javax.crypto.spec.PSource;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Sender {
    SocketChannel channel;
    Path path;
    Message message;
    Gson gson = new Gson();


    public Sender(SocketChannel socketChannel, Path path) {
        this.channel = socketChannel;
        this.path = path;
    }

    public Sender(SocketChannel socketChannel, Message message) {
        this.channel = socketChannel;
        this.message = message;
    }


    public void sendMessage() throws IOException {
        String gMessage = gson.toJson(message);
        System.out.println(gMessage);
        channel.write(ByteBuffer.wrap(gMessage.getBytes()));
        System.out.println(message);
    }

    private List<FileInfo> getFilesFromDir(Path path) throws IOException {
        List<FileInfo> fileInfoList = Files.list(path).map(FileInfo::new).collect(Collectors.toList());
        return fileInfoList;
    }

    public void sendFilesListFromDir() throws IOException {
        String gMessage = gson.toJson(getFilesFromDir(path));
        System.out.println(gMessage);
        channel.write(ByteBuffer.wrap(gMessage.getBytes()));
    }

    public void sendAllFilesList() throws IOException {
        String gMessage = gson.toJson(getFiles(path));
        channel.write(ByteBuffer.wrap(gMessage.getBytes()));
    }


    public List<Path> getFiles(Path path) throws IOException {
        List<Path> paths = Files.walk(path)
                .filter(Files::isRegularFile)
                .collect(Collectors.toList());
        System.out.println(Arrays.toString(paths.toArray()));
        return paths;
    }


    private void sendFile(Path path) throws Exception {
        FileChannel fileChannel = FileChannel.open(path);
        System.out.println("Send file: " + path);
        ByteBuffer buf = ByteBuffer.allocate(1024);
        int res = 1;
        while (res > 0){
            res = fileChannel.read(buf);
            buf.flip();
            if (res >= 0) {
                channel.write(buf);
            }
            buf.compact();
        }
        fileChannel.close();
    }

    private void sendFileInfo(FileInfo fileInfo) throws Exception {

        String gMessage = gson.toJson(fileInfo);
        System.out.println("Send fileInfo: " + gMessage);
        channel.write(ByteBuffer.wrap(gMessage.getBytes()));
    }

    public void sendAllFilesFromDir() throws Exception {
        ByteBuffer response = ByteBuffer.allocate(2);
        int answer = 0;
        FileInfo file = new FileInfo(path);
        System.out.println("Send fileInfo: " + file.toString());
        sendFileInfo(file);
        sendFile(path);
        System.out.println("Send file");
        while (answer == 0) {
            answer = channel.read(response);
        }
        String ansText = new String(response.array());
        response.clear();
        if (!ansText.equals("OK")) {
            throw new Exception("Не удалось загрузить файл");
        }
        channel.close();
    }

    public void sendDir(String string) throws IOException {
        channel.write(ByteBuffer.wrap(string.getBytes()));
    }
}
