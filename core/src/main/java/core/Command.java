package core;

public enum Command {
    AUTH(0),
    GET_LIST(1),
    UPLOAD(2),
    DOWNLOAD(3),
    MAKE_DIR(4),
    ;

    private int cmd;

    public int getCmd() {
        return cmd;
    }

    Command(int cmd) {
        this.cmd = cmd;
    }

}
