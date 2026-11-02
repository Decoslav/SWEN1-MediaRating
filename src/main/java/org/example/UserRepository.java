package org.example;

import java.sql.*;

public class UserRepository {

    private DatabaseConnection database = new DatabaseConnection();

    public User findWithUsername(String username) throws SQLException{

        String sql= "SELECT id, username, hashed_password FROM users WHERE username=?";

        try (Connection conn = database.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)){

            stmt.setString(1, username);

            try(ResultSet result = stmt.executeQuery()){
                if(result.next()){
                    return new User(
                            result.getInt("id"),
                            result.getString("username"),
                            result.getString("hashed_password")
                    );
                }
            }
        }

        return null;
    }

    public void save(String username, String hashedPassword) throws SQLException{

        String sql = "INSERT INTO users (username, hashed_password) VALUES (?,?)";

        try(Connection conn= database.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)){

            stmt.setString(1, username);
            stmt.setString(2,hashedPassword);

            stmt.executeUpdate();
        }
    }

}
