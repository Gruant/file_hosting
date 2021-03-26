package core;

public class User {
    private String login;
    private String password;
    private String token;
    private String folder;

    public User(String login, String password, String token) {
        this.login = login;
        this.password = password;
        this.token = token;
        this.folder = login;
    }

    public User(String login, String password) {
        this.login = login;
        this.password = password;
        this.folder = login;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }
}
