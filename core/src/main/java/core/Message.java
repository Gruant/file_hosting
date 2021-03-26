package core;

public class Message {

    private final FileInfo fileInfo;
    private final Command cmd;

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

    @Override
    public String toString() {
        return "Message{" +
                "fileInfo=" + fileInfo +
                ", cmd=" + cmd +
                '}';
    }
}
