package core;

public enum Command {
    AUTH("0"),
    GET_LIST("1"),
    UPLOAD("2"),
    DOWNLOAD("3"),
    MAKE_DIR("4"),
    GET_FILES_PATH("5"),
    DELETE("6")
    ;

    private String cmd;

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    Command(String cmd) {
        this.cmd = cmd;
    }

}
