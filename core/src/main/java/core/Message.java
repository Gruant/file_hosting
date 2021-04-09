package core;


public class Message {

    private User user = null;
    private long fileSize = 0;
    private FileInfo fileInfo = null;
    private final Command cmd;
    private String token = null;
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

    public Message(Command cmd, String token) {
        this.cmd = cmd;
        this.token = token;
    }

    public Message(Command upload, FileInfo fileInfo, String filename, long size) {
        this.cmd = upload;
        this.fileInfo = fileInfo;
        this.additional = filename;
        this.fileSize = size;
    }

    public Message(Command auth, User user){
        this.cmd = auth;
        this.user = user;
    }

    public long getFileSize() {
        return fileSize;
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

    public String getToken() {
        return token;
    }

    public User getUser() {
        return user;
    }

    @Override
    public String toString() {
        return "Message{" +
                "fileInfo=" + fileInfo +
                ", cmd=" + cmd +
                ", token='" + token + '\'' +
                ", additional='" + additional + '\'' +
                '}';
    }
}
