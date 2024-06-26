package UI;

import javax.swing.*;

import Util.DatabaseConnection;
import Util.InitializeUI;
import Util.User;

import java.awt.*;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class NotificationsUI extends JFrame {

    private JScrollPane scrollPane;
    private JPanel mainContentPanel;

    public NotificationsUI() {
        InitializeUI.setupFrame(this, "Notifications");
        JPanel headerPanel = InitializeUI.createHeaderPanel("Notifications ");
        JPanel mainContentPanel = createMainContentPanel();
        ActionListener[] actions = {
                e -> openHomeUI(),
                e -> exploreUI(),
                e -> ImageUploadUI(),
                e -> notificationsUI(),
                e -> openProfileUI()
        };
        JPanel navigationPanel = InitializeUI.createNavigationPanel(actions);

        InitializeUI.addComponents(this, headerPanel, mainContentPanel, navigationPanel);

        loadNotifications();
    }

    private JPanel createMainContentPanel() {
        mainContentPanel = new JPanel();
        mainContentPanel.setLayout(new BoxLayout(mainContentPanel, BoxLayout.Y_AXIS));
        scrollPane = new JScrollPane(mainContentPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setPreferredSize(new Dimension(800, 500));
        return mainContentPanel;
    }

    public void displayNotification(String notification) {
        JLabel notificationLabel = new JLabel(notification);
        mainContentPanel.add(notificationLabel);
        mainContentPanel.revalidate();
        mainContentPanel.repaint();
    }

    private void loadNotifications() {
        int currentUserId = User.getLoggedInUser().getId();
        populateNotifications(mainContentPanel, currentUserId);
    }

    private void populateNotifications(JPanel mainContentPanel, int currentUserId) {
        Connection conn = DatabaseConnection.getConnection();
        String sql = "SELECT * FROM notification WHERE targetId = ? ORDER BY timestamp ASC";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, currentUserId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                int notifierId = rs.getInt("notifierId");
                String imagePath = rs.getString("imagePath");
                String timestamp = rs.getString("timestamp");
                String notification = getNotificationString(notifierId, imagePath, timestamp);
                displayNotification(notification);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private String getNotificationString(int notifierId, String imagePath, String timestamp) {
        String notifierUsername = User.getUserById(notifierId).getUsername();
        String notification = notifierUsername + " liked your image " + getElapsedTime(timestamp)
                + " ago";
        return notification;
    }

    private String getElapsedTime(String timestamp) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime notificationTime = LocalDateTime.parse(timestamp, formatter);
        LocalDateTime currentTime = LocalDateTime.now();
        long seconds = ChronoUnit.SECONDS.between(notificationTime, currentTime);
        if (seconds < 60) {
            return seconds + " seconds";
        } else if (seconds < 3600) {
            return seconds / 60 + " minutes";
        } else if (seconds < 86400) {
            return seconds / 3600 + " hours";
        } else {
            return seconds / 86400 + " days";
        }
    }

    private void ImageUploadUI() {
        this.dispose();
        ImageUploadUI upload = new ImageUploadUI();
        upload.setLocationRelativeTo(null);
        upload.setVisible(true);
    }

    private void openProfileUI() {
        this.dispose();
        User user = User.getLoggedInUser();
        ProfileUI profileUI = new ProfileUI(user);
        profileUI.setLocationRelativeTo(null);
        profileUI.setVisible(true);
    }

    private void notificationsUI() {
        this.dispose();
        NotificationsUI notificationsUI = new NotificationsUI();
        notificationsUI.setLocationRelativeTo(null);
        notificationsUI.setVisible(true);
    }

    private void openHomeUI() {
        this.dispose();
        HomeUI homeUI = new HomeUI();
        homeUI.setLocationRelativeTo(null);
        homeUI.setVisible(true);
    }

    private void exploreUI() {
        this.dispose();
        ExploreUI explore = new ExploreUI();
        explore.setLocationRelativeTo(null);
        explore.setVisible(true);
    }

}
