package org.example;

import java.sql.*;

public class DatabaseConnection {
    private String url = "jdbc:postgresql://localhost:5432/semesterprojekt";
    private String user= "postgres";
    private String password = "mysecretpassword";

    public Connection getConnection() throws SQLException{

        return DriverManager.getConnection(url,user, password);
    }
}
