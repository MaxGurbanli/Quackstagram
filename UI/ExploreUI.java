package UI;
import javax.imageio.ImageIO;
import javax.swing.*;

import Util.DatabaseConnection;
import Util.ImageLikesManager;
import Util.InitializeUI;
import Util.Picture;
import Util.User;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class ExploreUI extends JFrame {

    private static final int WIDTH = 300;
    private static final int IMAGE_SIZE = WIDTH / 3; // Size for each image in the grid
    private static final Color LIKE_BUTTON_COLOR = new Color(255, 90, 95);

    JPanel navigationPanel;
    ImageLikesManager imageLikesManager;

    public ExploreUI() {
        InitializeUI.setupFrame(this, "Explore");
        JPanel headerPanel = InitializeUI.createHeaderPanel("Explore 🐥");
        JPanel mainContentPanel = createMainContentPanel();
        ActionListener[] actions = {
                e -> openHomeUI(),
                e -> exploreUI(),
                e -> ImageUploadUI(),
                e -> notificationsUI(),
                e -> openProfileUI()
        };
        navigationPanel = InitializeUI.createNavigationPanel(actions);

        InitializeUI.addComponents(this, headerPanel, mainContentPanel, navigationPanel);
    }

    private JPanel createMainContentPanel() {
        // Create the main content panel with search and image grid

        // Image Grid
        JPanel imageGridPanel = new JPanel(new GridLayout(0, 3, 2, 2)); // 3 columns, auto rows

        // Load images from the uploaded folder
        File imageDir = new File("img/uploaded");
        if (imageDir.exists() && imageDir.isDirectory()) {
            File[] imageFiles = imageDir.listFiles((dir, name) -> name.matches(".*\\.(png|jpg|jpeg)"));
            if (imageFiles != null) {
                for (File imageFile : imageFiles) {
                    ImageIcon imageIcon = new ImageIcon(new ImageIcon(imageFile.getPath()).getImage()
                            .getScaledInstance(IMAGE_SIZE, IMAGE_SIZE, Image.SCALE_SMOOTH));
                    JLabel imageLabel = new JLabel(imageIcon);
                    imageLabel.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            displayImage(imageFile.getPath());
                        }
                    });
                    imageGridPanel.add(imageLabel);
                }
            }
        }

        JScrollPane scrollPane = new JScrollPane(imageGridPanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        // Main content panel that holds both the search bar and the image grid
        JPanel mainContentPanel = new JPanel();
        mainContentPanel.setLayout(new BoxLayout(mainContentPanel, BoxLayout.Y_AXIS));
        mainContentPanel.add(scrollPane); // This will stretch to take up remaining space
        return mainContentPanel;
    }

    private void displayImage(String fullImagePath) {

        String imagePath = fullImagePath.substring(fullImagePath.lastIndexOf("\\") + 1);
        String fileExtension = fullImagePath.substring(fullImagePath.lastIndexOf(".") + 1);
        imagePath = imagePath.replace("." + fileExtension, "") + "." + fileExtension;

        getContentPane().removeAll();
        setLayout(new BorderLayout());

        // Add the header and navigation panels back
        add(InitializeUI.createHeaderPanel("Explore"), BorderLayout.NORTH);
        add(navigationPanel, BorderLayout.SOUTH);

        // Read image details
        String username = "";
        String bio = "";
        String timestampString = "";
        int likes = 0;
        try {
            Connection connection = DatabaseConnection.getConnection();
            String query = "SELECT * FROM Picture WHERE imagePath = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, imagePath);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
            User author = User.getUserByUserId(resultSet.getInt("authorId"));
            username = author.getUsername();
            bio = resultSet.getString("caption");
            timestampString = resultSet.getString("timestamp");
            Picture picture = new Picture(imagePath, bio);
            
            likes = picture.getLikesCount();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        // Calculate time since posting
        String timeSincePosting = "Unknown";
        if (!timestampString.isEmpty()) {
            LocalDateTime timestamp = LocalDateTime.parse(timestampString,
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            LocalDateTime now = LocalDateTime.now();
            long days = ChronoUnit.DAYS.between(timestamp, now);
            timeSincePosting = days + " day" + (days != 1 ? "s" : "") + " ago";
        }

        // Top panel for username and time since posting
        JPanel topPanel = new JPanel(new BorderLayout());
        JButton usernameLabel = new JButton(username);
        JLabel timeLabel = new JLabel(timeSincePosting);
        timeLabel.setHorizontalAlignment(JLabel.RIGHT);
        topPanel.add(usernameLabel, BorderLayout.WEST);
        topPanel.add(timeLabel, BorderLayout.EAST);

        // Prepare the image for display
        JLabel imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(JLabel.CENTER);
        try {
            BufferedImage originalImage = ImageIO.read(new File(fullImagePath));
            ImageIcon imageIcon = new ImageIcon(originalImage);
            imageLabel.setIcon(imageIcon);
        } catch (IOException ex) {
            imageLabel.setText("Image not found");
        }

        // Bottom panel for bio and likes
        JPanel bottomPanel = new JPanel(new BorderLayout());
        JTextArea bioTextArea = new JTextArea(bio);
        bioTextArea.setEditable(false);
        JLabel likesLabel = new JLabel("Likes: " + likes);
        bottomPanel.add(bioTextArea, BorderLayout.CENTER);
        bottomPanel.add(likesLabel, BorderLayout.SOUTH);

        // Adding the components to the frame
        add(topPanel, BorderLayout.NORTH);
        add(imageLabel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        // Re-add the header and navigation panels
        add(InitializeUI.createHeaderPanel("Explore"), BorderLayout.NORTH);
        add(navigationPanel, BorderLayout.SOUTH);

        // Panel for the back button
        JPanel backButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton backButton = new JButton("Back");

        // Make the button take up the full width
        backButton.setPreferredSize(new Dimension(WIDTH - 20, backButton.getPreferredSize().height));

        backButtonPanel.add(backButton);

        backButton.addActionListener(e -> {
            getContentPane().removeAll();
            add(InitializeUI.createHeaderPanel("Explore"), BorderLayout.NORTH);
            add(createMainContentPanel(), BorderLayout.CENTER);
            add(navigationPanel, BorderLayout.SOUTH);
            revalidate();
            repaint();
        });

        final String finalUsername = username;
        usernameLabel.addActionListener(e -> {
            User user = new User(finalUsername);
            ProfileUI profileUI = new ProfileUI(user);
            profileUI.setVisible(true);
            dispose(); // Close the current frame
        });

        final String finalImagePath = imagePath;
        JButton likeButton = new JButton("Like");
        likeButton.setBackground(LIKE_BUTTON_COLOR);
        likeButton.addActionListener(e -> {
            handleLikeAction(finalImagePath, likesLabel);
        });
        bottomPanel.add(likeButton);

        // Container panel for image and details
        JPanel containerPanel = new JPanel(new BorderLayout());

        containerPanel.add(topPanel, BorderLayout.NORTH);
        containerPanel.add(imageLabel, BorderLayout.CENTER);
        containerPanel.add(bottomPanel, BorderLayout.SOUTH);

        // Add the container panel and back button panel to the frame
        add(backButtonPanel, BorderLayout.NORTH);
        add(containerPanel, BorderLayout.CENTER);

        revalidate();
        repaint();
    }

    private void handleLikeAction(String imagePath, JLabel likesLabel) {
        Picture picture = Picture.getPictureByPath(imagePath);
        User currentUser = User.getLoggedInUser();
        if (currentUser != null && !picture.hasLiked(currentUser)) {
            picture.addLike(currentUser);
            int updatedLikes = picture.getLikesCount();
            likesLabel.setText("Likes: " + updatedLikes);
        } else {
            picture.removeLike(currentUser);
            int updatedLikes = picture.getLikesCount();
            likesLabel.setText("Likes: " + updatedLikes);
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