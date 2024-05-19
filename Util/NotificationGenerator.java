package Util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class NotificationGenerator {

    public static void generateNotification(String imagePath) {
        User currentUser = User.getLoggedInUser();
        Picture picture = Picture.getPictureByPath(imagePath);
        User imagePoster = picture.getAuthor();
        Connection conn = DatabaseConnection.getConnection();
        String sql = "INSERT INTO notification (notifierId, targetId, imagePath, timestamp) VALUES (?, ?, ?, CURRENT_TIMESTAMP)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, currentUser.getUserId());
            pstmt.setString(2, imagePoster.getUserId());
            pstmt.setString(3, imagePath);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

}
