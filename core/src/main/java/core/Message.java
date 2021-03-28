package core;

import java.nio.file.Path;

public class Message {

    private final FileInfo fileInfo;
    private final Command cmd;
    private String additional = null;

    public Message(Command cmd, FileInfo fileInfo) {
        this.cmd = cmd;
        this.fileInfo = fileInfo;
    }

    public Message(Command cmd, FileInfo fileInfo, String additional) {
        this.cmd = cmd;
        this.fileInfo = fileInfo;
        this.additional = additional;
    }


    public FileInfo getFileInfo() {
        return fileInfo;
    }

    public Command getCmd() {
        return cmd;
    }

    public String getAdditional() {
        return additional;
    }

    @Override
    public String toString() {
        return "Message{" +
                "fileInfo=" + fileInfo +
                ", cmd=" + cmd +
                ", additional='" + additional + '\'' +
                '}';
    }
}
