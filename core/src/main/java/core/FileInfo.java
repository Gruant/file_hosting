package core;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileInfo{

    private final String filename;
    private final long size;
    private final FileType type;
    private final String path;

    public FileInfo(Path path){
        try {
            this.filename = path.getFileName().toString();
            this.path = path.toString();
            this.size = Files.size(path);
            this.type = Files.isDirectory(path) ? FileType.DIRECTORY : FileType.FILE;
        } catch (IOException e) {
            throw new RuntimeException("Can't get a info from file");
        }
    }

    public String getFilename() {
        return filename;
    }

    public FileType getType() {
        return type;
    }

    public String getStringPath() {
        return path;
    }

    public long getSize() {
        return size;
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
