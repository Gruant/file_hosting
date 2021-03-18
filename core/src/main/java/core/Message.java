package core;

import java.io.Serializable;

public class Message implements Serializable {
    public enum Command {
        AUTH(0),
        GET_LIST(1),
        SEND_FILE(2),
        ;

        private int cmd;

        public int getCmd() {
            return cmd;
        }

        Command(int cmd) {
            this.cmd = cmd;
        }
    }

    private FileInfo fileInfo;
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
}
