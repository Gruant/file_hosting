package core;

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
        while (fileChannel.read(buf) > 0){
            buf.flip();
            try {
                channel.write(buf);
            } catch (IOException e) {
                throw new Exception("Не удалось отправить файл");
            }
            buf.clear();
        }
        fileChannel.close();
    }

    private void sendFileWithProtocol(Path path) throws Exception {
        FileInfo info = new FileInfo(path);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(info);
        objectOutputStream.flush();
        channel.write(ByteBuffer.wrap(byteArrayOutputStream.toByteArray()));
        System.out.println("send FileInfo");

        sendFile(path);

        objectOutputStream.close();
        byteArrayOutputStream.close();
    }

    public void sendAllFilesFromDir() throws Exception {
        List<Path> paths = getFiles(this.path);
        for (Path path: paths) {
            sendFileWithProtocol(path);
            System.out.println(path.toString());
        }
    }
}
