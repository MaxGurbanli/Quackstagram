package Util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Picture {
    private String imagePath;
    private String caption;

    public Picture(String imagePath, String caption) {
        this.imagePath = imagePath;
        this.caption = caption;
    }

    public String getPath() {
        return imagePath;
    }

    public String getCaption() {
        return caption;
    }

    public static Picture getPictureByPath(String imagePath) {
        Connection conn = DatabaseConnection.getConnection();
        try (Statement stmt = conn.createStatement()) {
            String query = "SELECT * FROM Picture WHERE imagePath = '" + imagePath + "'";
            ResultSet rs = stmt.executeQuery(query);
            if (rs.next()) {
                Picture picture = new Picture(rs.getString("imagePath"), rs.getString("caption"));  
                return picture;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void addLike(User user) {
        Connection conn = DatabaseConnection.getConnection();
        try (Statement stmt = conn.createStatement()) {
            String query = "INSERT INTO PictureLike (imagePath, likerId) VALUES ('" + imagePath + "', " + user.getUserId() + ")";
            stmt.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void removeLike(User user) {
        Connection conn = DatabaseConnection.getConnection();
        try (Statement stmt = conn.createStatement()) {
            String query = "DELETE FROM PictureLike WHERE imagePath = '" + imagePath + "' AND likerId = " + user.getUserId();
            stmt.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean hasLiked(User user) {
        Connection conn = DatabaseConnection.getConnection();
        try (Statement stmt = conn.createStatement()) {
            String query = "SELECT * FROM PictureLike WHERE imagePath = '" + imagePath + "' AND likerId = " + user.getUserId();
            ResultSet rs = stmt.executeQuery(query);
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public int getLikesCount() {
        int likesCount = 0;
        try {
            // Connect to the database
            Connection conn = DatabaseConnection.getConnection();
            Statement stmt = conn.createStatement();
    
            // Execute a SQL query to get likes count
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) AS likesCount FROM PictureLike WHERE imagePath = '" + imagePath + "'");
    
            if (rs.next()) {
                likesCount = rs.getInt("likesCount");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return likesCount;
    }

    public User getAuthor() {
        Connection conn = DatabaseConnection.getConnection();
        try (Statement stmt = conn.createStatement()) {
            String query = "SELECT * FROM Picture WHERE imagePath = '" + imagePath + "'";
            ResultSet rs = stmt.executeQuery(query);
            if (rs.next()) {
                int userId = rs.getInt("UserId");
                return User.getUserById(userId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}
