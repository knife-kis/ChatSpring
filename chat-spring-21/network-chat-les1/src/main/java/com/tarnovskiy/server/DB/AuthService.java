package com.tarnovskiy.server.DB;

import javax.sql.DataSource;
import java.sql.*;

/**
 * @author Tarnovskiy Maksim
 */
public class AuthService {
    private static final String CON_STR = "jdbc:sqlite:C:\\Users\\Maks-Oks\\Desktop\\geekbrains\\SPRING\\hw01\\chat-spring-21\\network-chat-les1\\mydb.db";
    private static Connection connection;
    private static Statement stmt;
    private static int id;

    public static void connect(DataSource dataSource) {
        try {
            dataSource.getConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(CON_STR);
            stmt = connection.createStatement();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String changeNick(String nickname){
        String id = String.format("SELECT id FROM main where nickname = '%s'", nickname);
        String sql = String.format("UPDATE main SET nickname = '%s' WHERE id = %s", nickname, id);

        try {
            ResultSet rs = stmt.executeQuery(sql);
            if(rs.next()){
                return rs.getString("nickname");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getnickByLoginAndPass(String login, String pass){
        String sql = String.format("SELECT nickname FROM main where login = '%s' and password = '%s'", login, pass);

        try {
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()){
                return rs.getString("nickname");
            } else return "";
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static boolean clientLocationOnServer(String nick){
        String nickSql = getNick(nick);
        boolean have;
        if (nickSql.equals(nick))
            have = true;
        else
            have = false;
        return have;
    }

    private static String getNick(String nick) {
        String sql = String.format("SELECT nickname FROM main where nickname = '%s'", nick);

        try {
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()){
                return rs.getString("nickname");
            } else return "";
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static void disconnect(){
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void addClientDb(String login, String password, String nickName) {
        connect();
        id = maxId();
        id++;
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO main(`id`, `login`, `password`, `nickname`) " +
                        "VALUES(?, ?, ?, ?)")) {
            statement.setObject(1, String.valueOf(id));
            statement.setObject(2, login);
            statement.setObject(3, password);
            statement.setObject(4, nickName);
            // Выполняем запрос
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static int maxId() {
        String sqlId = "SELECT MAX(id) FROM main";

        try {
            ResultSet rs = stmt.executeQuery(sqlId);
            if (rs.next()){
                return rs.getInt("MAX(id)");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }


    public static boolean isLoginAndNickDb(String login, String nick) {
        connect();
        String sqlLogin = String.format("SELECT login FROM main where login = '%s'", login);
        String sqlNick = String.format("SELECT nickname FROM main where nickname = '%s'", nick);
        try {
            ResultSet rsLogin = stmt.executeQuery(sqlLogin);
            if (rsLogin.next())
                return true;
            ResultSet rsNick = stmt.executeQuery(sqlNick);
            if (rsNick.next())
                return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
