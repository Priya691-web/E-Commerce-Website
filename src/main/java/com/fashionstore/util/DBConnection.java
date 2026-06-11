package com.fashionstore.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String URL_FORMAT = "jdbc:mysql://%s:3306/fashionstore";
    private static final String USER = "root";
    private static final String PASSWORD = "root123";

    public static Connection getConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String host = System.getenv("DB_HOST");
            if (host == null || host.isEmpty()) {
                host = "localhost"; 
            }
            String url = String.format(URL_FORMAT, host);
            return DriverManager.getConnection(url, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            System.err.println("[DBConnection] ERROR: MySQL Driver not found: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("MySQL Database Driver not found", e);
        } catch (SQLException e) {
            System.err.println("[DBConnection] ERROR: Database connection failed: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Database connection failed", e);
        }
    }
}
