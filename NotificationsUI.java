import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class NotificationsUI extends JFrame {

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
    }

    private JPanel createMainContentPanel() {
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        String currentUsername = getCurrentUsername();
        populateNotifications(contentPanel, currentUsername);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        return mainPanel;
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

    private void populateNotifications(JPanel contentPanel, String currentUsername) {
        try (BufferedReader reader = Files.newBufferedReader(Paths.get("data", "notifications.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts[0].trim().equals(currentUsername)) {
                    String notificationMessage = parts[1].trim() + " liked your picture - "
                            + getElapsedTime(parts[3].trim()) + " ago";
                    JPanel notificationPanel = new JPanel(new BorderLayout());
                    notificationPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                    notificationPanel.add(new JLabel(notificationMessage), BorderLayout.CENTER);
                    contentPanel.add(notificationPanel);
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
