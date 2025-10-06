package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    // chỉnh lại URL, USER, PASS theo máy của bạn
    private static final String URL = "jdbc:mysql://localhost:3306/mailapp?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASS = "1234";

    static {
        try {
            // optional: đảm bảo driver load
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found. Add connector jar to classpath.");
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}
