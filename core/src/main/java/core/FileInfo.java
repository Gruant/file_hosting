package core;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileInfo implements Serializable {

    public enum FileType {
        FILE("File"), DIRECTORY("Directory");

        private String name;

        public String getName() {
            return name;
        }

        FileType(String name) {
            this.name = name;
        }
    }

    private String filename;
    private long size;
    private FileType type;

    public FileInfo(Path path){
        try {
            this.filename = path.getFileName().toString();
            this.size = Files.size(path);
            this.type = Files.isDirectory(path) ? FileType.DIRECTORY : FileType.FILE;
            if (this.type == FileType.DIRECTORY) {
                this.size = -1L;
            }
        } catch (IOException e) {
            throw new RuntimeException("Can't get a info from file");
        }
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public FileType getType() {
        return type;
    }

    public void setType(FileType type) {
        this.type = type;
    }

//    public long getObjectSize() {
//        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//        ObjectOutputStream objectOutputStream = null;
//        try {
//            objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        try {
//            assert objectOutputStream != null;
//            objectOutputStream.writeObject(this);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return byteArrayOutputStream.toByteArray();
//    }
}
