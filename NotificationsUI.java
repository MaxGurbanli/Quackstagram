import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class NotificationsUI extends JFrame implements Observer {

    private JTextArea notificationArea;
    private JScrollPane scrollPane;

    public NotificationsUI() {
        InitializeUI.setupFrame(this, "Notifications");
        JPanel headerPanel = InitializeUI.createHeaderPanel("Notifications 🐥");
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

        // Initialize observers and load notifications
        initializeObservers();
        loadNotifications();
    }

    private void generateAndWriteNotification(String username, String likedUsername, String imageId) {
        // Generate notification message
        String notificationMessage = username + ";" + likedUsername + ";" + imageId + ";" + LocalDateTime.now();

        // Write notification message to file
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get("data", "notifications.txt"), StandardOpenOption.APPEND)) {
            writer.write(notificationMessage);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private JPanel createMainContentPanel() {
        JPanel contentPanel = new JPanel(new BorderLayout());

        notificationArea = new JTextArea();
        scrollPane = new JScrollPane(notificationArea); // Initialize the JScrollPane
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        return contentPanel;
    }

    private void initializeObservers() {
        String likesFilePath = "data/likes.txt";
    ImageLikesManager imageLikesManager = new ImageLikesManager(likesFilePath, this);
    imageLikesManager.registerObserver(this);
    }

    @Override
    public void update(String notification) {
        SwingUtilities.invokeLater(() -> {
            notificationArea.append(notification + "\n");
            // Scroll to the bottom to show the latest notification
            JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
            verticalScrollBar.setValue(verticalScrollBar.getMaximum());
        });
    }

    void handleLikeEvent(String likedUsername, String imageId) {
        // Get current logged-in user
        String currentUser = getCurrentUsername();

        // Generate and write notification only if the liked user is not the current user
        if (!likedUsername.equals(currentUser)) {
            generateAndWriteNotification(currentUser, likedUsername, imageId);
        }
    }

    private void loadNotifications() {
        String currentUsername = getCurrentUsername();
        if (!currentUsername.isEmpty()) {
            populateNotifications(notificationArea, currentUsername);
        }
    }

    private String getCurrentUsername() {
        try (BufferedReader reader = Files.newBufferedReader(Paths.get("data", "users.txt"))) {
            String line = reader.readLine();
            if (line != null) {
                return line.split(":")[0].trim();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    private void populateNotifications(JTextArea textArea, String currentUsername) {
        try (BufferedReader reader = Files.newBufferedReader(Paths.get("data", "notifications.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts[0].trim().equals(currentUsername)) {
                    String notificationMessage = parts[1].trim() + " liked your picture - "
                            + getElapsedTime(parts[3].trim()) + " ago";
                    textArea.append(notificationMessage + "\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getElapsedTime(String timestamp) {
        LocalDateTime timeOfNotification = LocalDateTime.parse(timestamp,
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        long daysBetween = ChronoUnit.DAYS.between(timeOfNotification, LocalDateTime.now());
        long minutesBetween = ChronoUnit.MINUTES.between(timeOfNotification, LocalDateTime.now()) % 60;

        StringBuilder timeElapsed = new StringBuilder();
        if (daysBetween > 0) {
            timeElapsed.append(daysBetween).append(" day").append(daysBetween > 1 ? "s" : "");
        }
        if (minutesBetween > 0) {
            if (daysBetween > 0)
                timeElapsed.append(" and ");
            timeElapsed.append(minutesBetween).append(" minute").append(minutesBetween > 1 ? "s" : "");
        }
        return timeElapsed.toString();
    }

    private void ImageUploadUI() {
        // Open InstagramProfileUI frame
        this.dispose();
        ImageUploadUI upload = new ImageUploadUI();
        upload.setVisible(true);
    }

    private void openProfileUI() {
        // Open InstagramProfileUI frame
        this.dispose();
        User user = User.getLoggedInUser();
        InstagramProfileUI profileUI = new InstagramProfileUI(user);
        profileUI.setVisible(true);
    }

    private void notificationsUI() {
        // Open InstagramProfileUI frame
        this.dispose();
        NotificationsUI notificationsUI = new NotificationsUI();
        notificationsUI.setVisible(true);
    }

    private void openHomeUI() {
        // Open InstagramProfileUI frame
        this.dispose();
        QuackstagramHomeUI homeUI = new QuackstagramHomeUI();
        homeUI.setVisible(true);
    }

    private void exploreUI() {
        // Open InstagramProfileUI frame
        this.dispose();
        ExploreUI explore = new ExploreUI();
        explore.setVisible(true);
    }
}
