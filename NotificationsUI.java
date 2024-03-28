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
import java.time.temporal.ChronoUnit;

public class NotificationsUI extends JFrame implements Observer {

    private JScrollPane scrollPane;
    private ImageLikesManager imageLikesManager;
    private JPanel mainContentPanel;

    public NotificationsUI() {
        InitializeUI.setupFrame(this, "Notifications");
        JPanel headerPanel = InitializeUI.createHeaderPanel("Notifications ");
        JPanel mainContentPanel = createMainContentPanel();
        String likesFilePath = "data/likes.txt";
        imageLikesManager = new ImageLikesManager(likesFilePath, this);
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

    private void generateAndWriteNotification(String likerUsername, String imagePosterUsername, String imageId) {
        // Generate notification message
        System.out.println("generating notification");
        String notificationMessage = imagePosterUsername + ";" + likerUsername + ";" + imageId + ";"
                + LocalDateTime.now();

        // Write notification message to file
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get("data", "notifications.txt"),
                StandardOpenOption.APPEND)) {
            writer.write(notificationMessage);
            writer.newLine();
            System.out.println("notification written");
        } catch (IOException e) {
            System.out.println("Error writing notification");
            e.printStackTrace();
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
        String likesFilePath = "data/likes.txt";
        ImageLikesManager imageLikesManager = new ImageLikesManager(likesFilePath, this); // Register NotificationsUI as
                                                                                          // observer
        imageLikesManager.registerObserver(this);
    }

    public void displayNotification(String notification) {
        JLabel notificationLabel = new JLabel(notification);
        mainContentPanel.add(notificationLabel);
        mainContentPanel.revalidate();
        mainContentPanel.repaint();
    }

    public void handleLikeEvent(String imageId) {
        // Get current logged-in user
        User currentUser = User.getLoggedInUser();
        String currentUsername = currentUser.getUsername();
        User imagePoster = User.getUserByImageId(imageId);
        String imagePosterUsername = imagePoster.getUsername();

        System.out.println(currentUsername + " liked " + imagePosterUsername + "'s image");

        // Generate and write notification only if the liked user is not the current
        // user

        if (currentUser == imagePoster) {
            System.out.println("User liked their own image");
            return;
        }

        generateAndWriteNotification(currentUsername, imagePosterUsername, imageId);
    }

    private void loadNotifications() {
        String currentUsername = User.getLoggedInUser().getUsername();
        if (!currentUsername.isEmpty()) {
            populateNotifications(mainContentPanel, currentUsername);
        }
    }

    private void populateNotifications(JPanel mainContentPanel, String currentUsername) {
        try (BufferedReader reader = Files.newBufferedReader(Paths.get("data", "notifications.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] notificationDetails = line.split(";");
                String imagePosterUsername = notificationDetails[0];
                String likerUsername = notificationDetails[1];
                String imageId = notificationDetails[2];
                String timestamp = notificationDetails[3];
                if (currentUsername.equals(imagePosterUsername)) {
                    String notificationMessage = likerUsername + " liked your image " + imageId + " "
                            + getElapsedTime(timestamp) + " ago";
                    JLabel notificationLabel = new JLabel(notificationMessage);
                    mainContentPanel.add(notificationLabel);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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
