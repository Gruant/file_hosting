package core;

import java.io.Serializable;

public class Message implements Serializable {

    private final FileInfo fileInfo;
    private Command cmd;

    public Message(Command cmd, FileInfo fileInfo) {
        this.cmd = cmd;
        this.fileInfo = fileInfo;
    }

    public FileInfo getFileInfo() {
        return fileInfo;
    }

    public Command getCmd() {
        return cmd;
    }

    public void setCmd(Command cmd) {
        this.cmd = cmd;
    }

    @Override
    public String toString() {
        return "Message{" +
                "fileInfo=" + fileInfo +
                ", cmd=" + cmd +
                '}';
    }
}
