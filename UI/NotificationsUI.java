package UI;
import javax.swing.*;

import Util.DatabaseConnection;
import Util.ImageLikesManager;
import Util.InitializeUI;
import Util.Observer;
import Util.User;
import Util.Picture;

import java.awt.*;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class NotificationsUI extends JFrame implements Observer {

    private JScrollPane scrollPane;
    private ImageLikesManager imageLikesManager;
    private JPanel mainContentPanel;

    public NotificationsUI() {
        InitializeUI.setupFrame(this, "Notifications");
        JPanel headerPanel = InitializeUI.createHeaderPanel("Notifications ");
        JPanel mainContentPanel = createMainContentPanel();
        imageLikesManager = new ImageLikesManager(this);
        imageLikesManager.registerObserver(this);
        ActionListener[] actions = {
                e -> openHomeUI(),
                e -> exploreUI(),
                e -> ImageUploadUI(),
                e -> notificationsUI(),
                e -> openProfileUI()
        };
        JPanel navigationPanel = InitializeUI.createNavigationPanel(actions);

        InitializeUI.addComponents(this, headerPanel, mainContentPanel, navigationPanel);

        initializeObservers();
        loadNotifications();
    }

    private void generateAndWriteNotification(String likerUsername, String imagePosterUsername, String imagePath) {
        // write notification to database
        Connection conn = DatabaseConnection.getConnection();
        String sql = "INSERT INTO notifications (notifierId, targetId, imagePath) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, likerUsername);
            pstmt.setString(2, imagePosterUsername);
            pstmt.setString(3, imagePath);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
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

    @Override
    public void update(String notification) {
        displayNotification(notification);
    }

    private void initializeObservers() {
        ImageLikesManager imageLikesManager = new ImageLikesManager(this); // Register NotificationsUI as
                                                                                          // observer
        imageLikesManager.registerObserver(this);
    }

    public void displayNotification(String notification) {
        JLabel notificationLabel = new JLabel(notification);
        mainContentPanel.add(notificationLabel);
        mainContentPanel.revalidate();
        mainContentPanel.repaint();
    }

    public void handleLikeEvent(String imagePath) {
        User currentUser = User.getLoggedInUser();
        String currentUsername = currentUser.getUsername();
        Picture picture = Picture.getPictureByPath(imagePath);
        User imagePoster = picture.getAuthor();
        String imagePosterUsername = imagePoster.getUsername();

        System.out.println(currentUsername + " liked " + imagePosterUsername + "'s image");

        // Generate and write notification only if the liked user is not the current
        // user

        if (currentUser == imagePoster) {
            System.out.println("User liked their own image");
            return;
        }

        generateAndWriteNotification(currentUsername, imagePosterUsername, imagePath);
    }

    private void loadNotifications() {
        String currentUsername = User.getLoggedInUser().getUsername();
        if (!currentUsername.isEmpty()) {
            populateNotifications(mainContentPanel, currentUsername);
        }
    }

    private void populateNotifications(JPanel mainContentPanel, String currentUsername) {
        Connection conn = DatabaseConnection.getConnection();
        String sql = "SELECT * FROM notifications WHERE targetId = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, currentUsername);
            java.sql.ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String notifierId = rs.getString("notifierId");
                String imagePath = rs.getString("imagePath");
                String notification = getNotificationString(notifierId, imagePath);
                displayNotification(notification);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private String getNotificationString(String notifierId, String imagePath) {
        String notification = notifierId + " liked your image: " + imagePath + " " + getElapsedTime("2024-03-28T12:13:28.483811200");
        return notification;
    }

    private String getElapsedTime(String timestamp) {
        // Parse string in the following format: 2024-03-28T12:13:28.483811200
        LocalDateTime notificationTime = LocalDateTime.parse(timestamp);
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
        // Open QuackstagramProfileUI frame
        this.dispose();
        ImageUploadUI upload = new ImageUploadUI();
        upload.setVisible(true);
    }

    private void openProfileUI() {
        // Open QuackstagramProfileUI frame
        this.dispose();
        User user = User.getLoggedInUser();
        ProfileUI profileUI = new ProfileUI(user);
        profileUI.setVisible(true);
    }

    private void notificationsUI() {
        // Open QuackstagramProfileUI frame
        this.dispose();
        NotificationsUI notificationsUI = new NotificationsUI();
        notificationsUI.setVisible(true);
    }

    private void openHomeUI() {
        // Open QuackstagramProfileUI frame
        this.dispose();
        HomeUI homeUI = new HomeUI();
        homeUI.setVisible(true);
    }

    private void exploreUI() {
        // Open QuackstagramProfileUI frame
        this.dispose();
        ExploreUI explore = new ExploreUI();
        explore.setVisible(true);
    }
}
