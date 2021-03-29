package core;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Sender {
    private final SocketChannel channel;
    private Path path;
    private Message message;
    private final Gson gson = new Gson();
    private final Type itemsListType = new TypeToken<List<FileInfo>>(){}.getType();
    private final Type pathsListType = new TypeToken<List<Path>>(){}.getType();
    private final Type fileInfo = new TypeToken<FileInfo>(){}.getType();


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

    public void sendFilesListFromDir() throws IOException {
        List<FileInfo> filesList = getFilesFromDir(path);
        String gMessage = gson.toJson(filesList, itemsListType);
        channel.write(ByteBuffer.wrap(gMessage.getBytes()));
    }

    public void sendAllFilesList() throws IOException {
        List<String> filesPathList = getFiles(path);
        System.out.println("Отправлен список: " + filesPathList);
        String gMessage = gson.toJson(filesPathList, pathsListType);
        channel.write(ByteBuffer.wrap(gMessage.getBytes()));
    }


    public List<String> getFiles(Path path) throws IOException {
        List<Path> paths = Files.walk(path)
                .filter(Files::isRegularFile)
                .collect(Collectors.toList());
        List<String> stringPaths = new ArrayList<>();
        for (Path p : paths) {
            stringPaths.add(p.toString());
        }
        return stringPaths;
    }


    private void sendFile(Path path) throws IOException {
        try {
            FileChannel fileChannel = FileChannel.open(path);
            System.out.println("Send file: " + path);
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
        } catch (IOException e) {
            throw new IOException("Не удалось отправить файл");
        }
        channel.close();

    }

    private void sendFileInfo(FileInfo fileInfo) throws Exception {

        String gMessage = gson.toJson(fileInfo);
        System.out.println("Send gson FileInfo: " + gMessage);
        channel.write(ByteBuffer.wrap(gMessage.getBytes()));
    }

    private void sendFileName(String filename) throws IOException {
        String gMessage = gson.toJson(filename);
        System.out.println("Send gson filename: " + gMessage);
        channel.write(ByteBuffer.wrap(gMessage.getBytes()));
    }

    public void sendAllFilesFromDir() throws Exception {
        ByteBuffer response = ByteBuffer.allocate(2);
        int answer = 0;
        FileInfo file = new FileInfo(path);
        sendFileName(file.getFilename());
        sendFile(path);
        response.clear();
    }

    public void sendDir(String string) throws IOException {
        channel.write(ByteBuffer.wrap(string.getBytes()));
    }
}
