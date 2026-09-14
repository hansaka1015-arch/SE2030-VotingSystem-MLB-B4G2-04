package com.votingsystem.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    // Database connection credentials
    private static final String URL = "jdbc:mysql://localhost:3306/votingsystem_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";    private static final String USER = "root";
    private static final String PASSWORD = ""; //

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found: " + e.getMessage());
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
