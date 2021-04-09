package Connection;

import core.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.UUID;

public class Database {
    private static Connection connection;

    public static void connect() {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:server/file_hosting.db");
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
    }


    public static void createUser(User user) {
        String sql = "INSERT INTO user (login, password, token, folder) VALUES(?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, user.getLogin());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, UUID.randomUUID().toString());
            pstmt.setString(4, user.getLogin());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static Boolean authByToken(String token) {
        String sql = "SELECT token FROM user WHERE token=?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, token);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                if (rs.getString("token").equals(token)) {
                    return true;
                }
            }
            return false;

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }


    public static Boolean authByLogPass(User user) {
        String sql = "SELECT login, password FROM user WHERE login=?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, user.getLogin());
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                if (rs.getString("login").equals(user.getLogin())) {
                    if (rs.getString("password").equals(user.getPassword()))
                        return true;
                }
            }
            return false;

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    public static User UserInfo(User user) {
        String sql = "SELECT login, password, folder, token FROM user WHERE login=?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, user.getLogin());
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                if (rs.getString("login").equals(user.getLogin())) {
                    if (rs.getString("password").equals(user.getPassword())) {
                        user.setFolder(rs.getString("folder"));
                        user.setToken(rs.getString("token"));
                        return user;
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return user;
    }



    public static void disconnect(){
        try {
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
