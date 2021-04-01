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

    public Sender(SocketChannel socketChannel){
        this.channel = socketChannel;
    }

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


    private void sendFile(Path path){
        ByteBuffer response = ByteBuffer.allocate(2);
        boolean isAnswer = false;
        try {
            FileChannel fileChannel = FileChannel.open(path);
            ByteBuffer buf = ByteBuffer.allocate(1024);
            int res = 1;
            while (res > 0){
                res = fileChannel.read(buf);
                buf.flip();
                if(res > 0) {
                    channel.write(buf);
                }
                buf.clear();
            }
            System.out.println("Ожидаем ответа от получателя");
            while (!isAnswer) {
                channel.read(response);
                String answer = new String(response.array());
                if (answer.equals("OK")) {
                    System.out.println("Файл успешно принят сервером");
                    isAnswer = true;
                } else if(answer.equals("NO")){
                    System.out.println("Файл не принят сервером");
                    isAnswer = true;
                }
            }
            fileChannel.close();
            response.clear();
        } catch (IOException e) {
            System.out.println("Не удалось отправить файл");
        } finally {
            try {
                channel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public void sendAllFilesFromDir(){
        sendFile(path);
    }

    public void sendAuthResponse(String response) throws IOException {
        String gMessage = gson.toJson(response);
        channel.write(ByteBuffer.wrap(gMessage.getBytes()));
    }
}
