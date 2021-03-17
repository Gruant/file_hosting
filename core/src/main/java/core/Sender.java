package core;

import java.io.BufferedWriter;
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

    public Sender(SocketChannel channel, Path path) {
        this.channel = channel;
        this.path = path;
    }

    private List<Path> getFiles(Path path) throws IOException {
        List<Path> paths = Files.walk(this.path)
                .filter(Files::isRegularFile)
                .collect(Collectors.toList());
        System.out.println(Arrays.toString(paths.toArray()));
        return paths;
    }

    private List<FileInfo> getFilesFromDir(Path path) throws IOException {
        List<FileInfo> fileInfoList = Files.list(path).map(FileInfo::new).collect(Collectors.toList());
        return fileInfoList;
    }

    private void sendFile(Path path) throws Exception {
        FileChannel fileChannel = FileChannel.open(path);
        ByteBuffer buf = ByteBuffer.allocate(1024);
        int res = 0;
        while (true){
            res = fileChannel.read(buf);
            if (res < 0) {
                break;
            }
            buf.flip();
            while (res > 0){
                res -= channel.write(buf);
            }
            buf.clear();
        }
        fileChannel.close();
    }

    private void sendFileWithProtocol(FileInfo fileInfo) throws Exception {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(fileInfo);
        objectOutputStream.flush();
        int size = channel.write(ByteBuffer.wrap(byteArrayOutputStream.toByteArray()));
        System.out.println("send FileInfo - lenght: " + size);

        sendFile(path);


    }

        public void sendAllFilesFromDir() throws Exception {
        ByteBuffer response = ByteBuffer.allocate(2);
        ByteBuffer command = ByteBuffer.allocate(4);
        List<Path> paths = getFiles(this.path);
        long fileInfoObjectSize = 0;
        for (Path path: paths) {
            fileInfoObjectSize += new FileInfo(path).getSize();
        }

        int fileNum = paths.size();
        int answer = 0;

        for (Path path: paths) {
            FileInfo file = new FileInfo(path);
            sendFileWithProtocol(file);
            System.out.println(path.toString());
            while (answer == 0) {
                response.clear();
                answer = channel.read(response);
                System.out.println("read response");
            }
            answer = 0;
            String ansText = new String(response.array());
            response.clear();
            System.out.println(fileNum);
            if (!ansText.equals("OK")) {
                throw new Exception("Нет ответа от сервера");
            } else if (--fileNum == 0){
                channel.close();
            }
        }
    }
}
