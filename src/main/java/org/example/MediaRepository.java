package org.example;

import java.sql.*;

public class MediaRepository {

    private DatabaseConnection database = new DatabaseConnection();

    public MediaEntry findMediaById(int id) throws SQLException{

        String sql= "SELECT * FROM media WHERE id = ?";

        try (Connection conn = database.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)){

            stmt.setInt(1, id);

            try(ResultSet result = stmt.executeQuery()){
                if(result.next()){
                    return new MediaEntry(
                            result.getInt("id"),
                            result.getString("title"),
                            result.getString("description"),
                            result.getString("media_type"),
                            result.getInt("release_year"),
                            result.getString("genres"),
                            result.getInt("age_restriction"),
                            result.getInt("creator_id")

                    );
                }
            }
        }

        return null;
    }

    public void save(MediaEntry media) throws SQLException{

        String sql = "INSERT INTO media (title, description, media_type, release_year, genres, age_restriction, creator_id) " +
                "VALUES (?,?,?,?,?,?,?)";

        try(Connection conn= database.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)){

            stmt.setString(1, media.getTitle());
            stmt.setString(2, media.getDescription());
            stmt.setString(3, media.getMediaType());
            stmt.setInt(4, media.getReleaseYear());
            stmt.setString(5, media.getGenres());
            stmt.setInt(6, media.getAgeRestriction());
            stmt.setInt(7, media.getCreatorId());


            stmt.executeUpdate();
        }
    }

    public boolean update (MediaEntry media) throws SQLException{

        String sql = "UPDATE media SET title = ? , description = ?, media_type = ? ,"+
        " release_year = ?, genres = ?, age_restriction = ? WHERE id = ? AND creator_id = ? ";

        try (Connection conn= database.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, media.getTitle());
            stmt.setString(2, media.getDescription());
            stmt.setString(3, media.getMediaType());
            stmt.setInt(4, media.getReleaseYear());
            stmt.setString(5, media.getGenres());
            stmt.setInt(6, media.getAgeRestriction());
            stmt.setInt(7, media.getId());
            stmt.setInt(8, media.getCreatorId());

            return stmt.executeUpdate() == 1;

        }

    }

    public boolean delete(int id, int creatorId) throws SQLException{

        String sql = "DELETE FROM media WHERE id = ? AND creator_id = ?";

        try (Connection conn= database.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)){

            stmt.setInt(1, id);
            stmt.setInt(2, creatorId);

            return stmt.executeUpdate() == 1;
        }

    }

}
