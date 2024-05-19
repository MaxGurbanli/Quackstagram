package Util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class NotificationGenerator {

    public static void generateNotification(String imagePath) {
        User currentUser = User.getLoggedInUser();
        String currentUsername = currentUser.getUsername();
        Picture picture = Picture.getPictureByPath(imagePath);
        User imagePoster = picture.getAuthor();
        String imagePosterUsername = imagePoster.getUsername();
        Connection conn = DatabaseConnection.getConnection();
        String sql = "INSERT INTO notification (notifierId, targetId, imagePath) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, currentUsername);
            pstmt.setString(2, imagePosterUsername);
            pstmt.setString(3, imagePath);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

}
