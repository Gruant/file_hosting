package core;

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
