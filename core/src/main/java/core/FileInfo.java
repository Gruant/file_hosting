package core;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileInfo implements Serializable {

    private String filename;
    private long size;
    private FileType type;
    private final String path;

    public FileInfo(Path path){
        try {
            this.filename = path.getFileName().toString();
            this.path = path.toString();
            this.size = Files.size(path);
            this.type = Files.isDirectory(path) ? FileType.DIRECTORY : FileType.FILE;
            if (this.type == FileType.DIRECTORY) {
                this.size = 0;
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

    public String getStringPath() {
        return path;
    }

    @Override
    public String toString() {
        return "FileInfo{" +
                "filename='" + filename + '\'' +
                ", size=" + size +
                ", type=" + type +
                ", path='" + path + '\'' +
                '}';
    }
}
