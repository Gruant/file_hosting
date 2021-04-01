package Connection;

import java.sql.*;

public class Database {
    private static Connection connection;

    public static void connect(){
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:server/file_hosting.db");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }


//    public static User createUser(User user) {
//
//    }

    public static Boolean authByToken(String token){
        String sql = "SELECT token FROM user WHERE token=?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)){
                pstmt.setString(1, token);
                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    if(rs.getString("token").equals(token)){
                        return true;
                    }
                }
                return false;

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

//    public static String refreshToken() {
//
//    }

//    public static Boolean authByLogPass(User user){
//
//    }

    public static void disconnect(){
        try {
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
