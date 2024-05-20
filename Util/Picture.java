package Util;

import java.sql.Connection;
import java.sql.PreparedStatement;
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
            System.out.println(query);
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
        Connection conn = DatabaseConnection.getConnection();
        String sql = "SELECT getLikesCount(?) AS likeCount";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, imagePath);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("likeCount");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }


    public User getAuthor() {
        Connection conn = DatabaseConnection.getConnection();
        try (Statement stmt = conn.createStatement()) {
            String query = "SELECT * FROM Picture WHERE imagePath = '" + imagePath + "'";
            ResultSet rs = stmt.executeQuery(query);
            if (rs.next()) {
                int userId = rs.getInt("authorId");
                return User.getUserById(userId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}
