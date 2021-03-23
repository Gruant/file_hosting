package core;

import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
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
        channel.write(ByteBuffer.wrap(gMessage.getBytes()));
    }

    private List<FileInfo> getFilesFromDir(Path path) throws IOException {
        List<FileInfo> fileInfoList = Files.list(path).map(FileInfo::new).collect(Collectors.toList());
        return fileInfoList;
    }

    public void sendFilesList() throws IOException {
        String gMessage = gson.toJson(getFilesFromDir(path));
        channel.write(ByteBuffer.wrap(gMessage.getBytes()));
    }


    public List<Path> getFiles(Path path) throws IOException {
        List<Path> paths = Files.walk(this.path)
                .filter(Files::isRegularFile)
                .collect(Collectors.toList());
        System.out.println(Arrays.toString(paths.toArray()));
        return paths;
    }


    private void sendFile(Path path) throws Exception {
        FileChannel fileChannel = FileChannel.open(path);
        ByteBuffer buf = ByteBuffer.allocate(1024);
        int res = 1;
        while (res > 0){
            res = fileChannel.read(buf);
            buf.flip();
            if (res > 0) {
                channel.write(buf);
            }
            buf.compact();
        }
        fileChannel.close();
    }

    private void sendFileInfo(FileInfo fileInfo) throws Exception {
        String gMessage = gson.toJson(fileInfo);
        channel.write(ByteBuffer.wrap(gMessage.getBytes()));
    }

    public void sendAllFilesFromDir() throws Exception {
        ByteBuffer response = ByteBuffer.allocate(2);
        int answer = 0;
        FileInfo file = new FileInfo(path);
        sendFileInfo(file);
        sendFile(path);
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
}
