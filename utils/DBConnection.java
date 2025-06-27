package utils;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {
    private static Connection conn = null;

    public static Connection connect() {
        try {
            if (conn == null || conn.isClosed()) {
                Class.forName("org.sqlite.JDBC");
                conn = DriverManager.getConnection("jdbc:sqlite:bankapp.db");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return conn;
    }
}
