package com.kspk.bugtracker;

import org.springframework.stereotype.Component;

import java.sql.*;

@Component
public class DB {

    private final String DRIVER = "com.mysql.cj.jdbc.Driver";
    private final String URL = "jdbc:mysql://localhost:3306/bug_tracker";
    private final String USERNAME = "root";
    private final String PASSWORD = "";

    public Connection conn = null;

    public DB() {
        try {
            Class.forName(DRIVER);
            conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public ResultSet executeQuery(String q) {
        if(conn != null) {
            Statement statement = null;
            try {
                statement = conn.createStatement();
                return statement.executeQuery(q);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

}
