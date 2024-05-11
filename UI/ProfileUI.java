package UI;
import javax.swing.*;

import Util.DatabaseConnection;
import Util.User;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.awt.*;
import java.nio.file.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.stream.Stream;

public class ProfileUI extends JFrame {

    private static final int WIDTH = 300;
    private static final int HEIGHT = 500;
    private static final int PROFILE_IMAGE_SIZE = 80; // Adjusted size for the profile image to match UI
    private static final int GRID_IMAGE_SIZE = WIDTH / 3; // Static size for grid images
    private static final int NAV_ICON_SIZE = 20; // Corrected static size for bottom icons
    private JPanel contentPanel; // Panel to display the image grid or the clicked image
    private JPanel headerPanel; // Panel for the header
    private JPanel navigationPanel; // Panel for the navigation
    private User currentUser; // User object to store the current user's information

    @SuppressWarnings("unused")
    public ProfileUI(User user) {
        currentUser = user;
        int imageCount = 0;
        int followersCount = 0;
        int followingCount = 0;
        Connection conn = DatabaseConnection.getConnection();
    
        // Query to count images
        String imageSql = "SELECT COUNT(*) FROM Picture WHERE authorId = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(imageSql)) {
            pstmt.setInt(1, currentUser.getId());
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                imageCount = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    
        // Query to count followers
        String followersSql = "SELECT COUNT(*) FROM Follow WHERE targetId = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(followersSql)) {
            pstmt.setInt(1, currentUser.getId());
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                followersCount = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    
        // Query to count following
        String followingSql = "SELECT COUNT(*) FROM Follow WHERE followerId = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(followingSql)) {
            pstmt.setInt(1, currentUser.getId());
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                followingCount = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    
        setTitle("Profile");
        setSize(WIDTH, HEIGHT);
        setMinimumSize(new Dimension(WIDTH, HEIGHT));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        contentPanel = new JPanel();
        headerPanel = createHeaderPanel(); // Initialize header panel
        navigationPanel = createNavigationPanel(); // Initialize navigation panel
    
        initializeUI();
    }
    

    private void initializeUI() {
        getContentPane().removeAll(); // Clear existing components

        // Re-add the header and navigation panels
        add(headerPanel, BorderLayout.NORTH);
        add(navigationPanel, BorderLayout.SOUTH);

        // Initialize the image grid
        initializeImageGrid();

        revalidate();
        repaint();
    }

    private JPanel createHeaderPanel() {
        User loggedInUser = User.getLoggedInUser();
    
        // Determine if the logged-in user is viewing their own profile
        boolean isCurrentUser = loggedInUser.getId() == currentUser.getId();
    
        // Determine if the logged-in user is following the profile being viewed
        boolean isFollowing = false;
        Connection conn = DatabaseConnection.getConnection();
        String sql = "SELECT 1 FROM Follow WHERE followerId = ? AND targetId = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, loggedInUser.getId());
            pstmt.setInt(2, currentUser.getId());
            ResultSet rs = pstmt.executeQuery();
            isFollowing = rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    
        // Header Panel
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(Color.GRAY);
    
        // Top Part of the Header (Profile Image, Stats, Follow Button)
        JPanel topHeaderPanel = new JPanel(new BorderLayout(10, 0));
        topHeaderPanel.setBackground(new Color(249, 249, 249));
    
        // Profile image
        ImageIcon profileIcon = new ImageIcon(new ImageIcon("img/storage/profile/" + currentUser.getUsername() + ".png")
                .getImage().getScaledInstance(PROFILE_IMAGE_SIZE, PROFILE_IMAGE_SIZE, Image.SCALE_SMOOTH));
        JLabel profileImage = new JLabel(profileIcon);
        profileImage.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        topHeaderPanel.add(profileImage, BorderLayout.WEST);
    
        // Stats Panel
        JPanel statsPanel = new JPanel();
        statsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 0));
        statsPanel.setBackground(new Color(249, 249, 249));
        statsPanel.add(createStatLabel(Integer.toString(currentUser.getPostsCount()), "Posts"));
        statsPanel.add(createStatLabel(Integer.toString(currentUser.getFollowersCount()), "Followers"));
        statsPanel.add(createStatLabel(Integer.toString(currentUser.getFollowingCount()), "Following"));
        statsPanel.setBorder(BorderFactory.createEmptyBorder(25, 0, 10, 0));
    
        // Follow or Edit Profile Button
        JButton followOrEditProfileButton;
        if (isCurrentUser) {
            followOrEditProfileButton = new JButton("Edit Profile");
            followOrEditProfileButton.addActionListener(e -> openEditProfileUI());
        } else {
            followOrEditProfileButton = new JButton(isFollowing ? "Following" : "Follow");
            followOrEditProfileButton.addActionListener(e -> {
                if (followOrEditProfileButton.getText().equals("Follow")) {
                    followOrEditProfileButton.setText("Following");
                    handleFollowAction(currentUser.getUsername());
                } else {
                    followOrEditProfileButton.setText("Follow");
                    handleUnfollowAction(currentUser.getUsername());
                }
            });
        }
    
        followOrEditProfileButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        followOrEditProfileButton.setFont(new Font("Arial", Font.BOLD, 12));
        followOrEditProfileButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, followOrEditProfileButton.getMinimumSize().height));
        followOrEditProfileButton.setBackground(new Color(225, 228, 232));
        followOrEditProfileButton.setForeground(Color.BLACK);
        followOrEditProfileButton.setOpaque(true);
        followOrEditProfileButton.setBorderPainted(false);
        followOrEditProfileButton.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
    
        // Add Stats and Follow Button to a combined Panel
        JPanel statsFollowPanel = new JPanel();
        statsFollowPanel.setLayout(new BoxLayout(statsFollowPanel, BoxLayout.Y_AXIS));
        statsFollowPanel.add(statsPanel);
        statsFollowPanel.add(followOrEditProfileButton);
        topHeaderPanel.add(statsFollowPanel, BorderLayout.CENTER);
    
        headerPanel.add(topHeaderPanel);
    
        // Profile Name and Bio Panel
        JPanel profileNameAndBioPanel = new JPanel();
        profileNameAndBioPanel.setLayout(new BorderLayout());
        profileNameAndBioPanel.setBackground(new Color(249, 249, 249));
    
        JLabel profileNameLabel = new JLabel(currentUser.getUsername());
        profileNameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        profileNameLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10)); // Padding on the sides
    
        JTextArea profileBio = new JTextArea(currentUser.getBio());
        profileBio.setEditable(false);
        profileBio.setFont(new Font("Arial", Font.PLAIN, 12));
        profileBio.setBackground(new Color(249, 249, 249));
        profileBio.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10)); // Padding on the sides
    
        profileNameAndBioPanel.add(profileNameLabel, BorderLayout.NORTH);
        profileNameAndBioPanel.add(profileBio, BorderLayout.CENTER);
    
        headerPanel.add(profileNameAndBioPanel);
    
        return headerPanel;
    }
    

    private void handleFollowAction(String usernameToFollow) {
        Connection conn = DatabaseConnection.getConnection();
        try {
            String sqlFollow = "INSERT INTO Follow (followerId, targetId) VALUES (?, (SELECT id FROM User WHERE username = ?))";
            PreparedStatement pstmtFollow = conn.prepareStatement(sqlFollow);
            pstmtFollow.setInt(1, User.getLoggedInUser().getId());
            pstmtFollow.setString(2, usernameToFollow);
            pstmtFollow.executeUpdate();
            refreshUI(); // Refresh the UI to update the followers count
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void handleUnfollowAction(String usernameToUnfollow) {
        Connection conn = DatabaseConnection.getConnection();
        try {
            String sqlUnfollow = "DELETE FROM Follow WHERE followerId = ? AND targetId = (SELECT id FROM User WHERE username = ?)";
            PreparedStatement pstmtUnfollow = conn.prepareStatement(sqlUnfollow);
            pstmtUnfollow.setInt(1, User.getLoggedInUser().getId());
            pstmtUnfollow.setString(2, usernameToUnfollow);
            pstmtUnfollow.executeUpdate();
            refreshUI(); // Refresh the UI to update the followers count
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void refreshUI() {
        headerPanel = createHeaderPanel();
        initializeUI();
    }
    
    private JPanel createNavigationPanel() {
        // Navigation Bar
        JPanel navigationPanel = new JPanel();
        navigationPanel.setBackground(new Color(249, 249, 249));
        navigationPanel.setLayout(new BoxLayout(navigationPanel, BoxLayout.X_AXIS));
        navigationPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        navigationPanel.add(createIconButton("img/icons/home.png", "home"));
        navigationPanel.add(Box.createHorizontalGlue());
        navigationPanel.add(createIconButton("img/icons/search.png", "explore"));
        navigationPanel.add(Box.createHorizontalGlue());
        navigationPanel.add(createIconButton("img/icons/add.png", "add"));
        navigationPanel.add(Box.createHorizontalGlue());
        navigationPanel.add(createIconButton("img/icons/heart.png", "notification"));
        navigationPanel.add(Box.createHorizontalGlue());
        navigationPanel.add(createIconButton("img/icons/profile.png", "profile"));

        return navigationPanel;

    }

    private void initializeImageGrid() {
        contentPanel.removeAll(); // Clear existing content
        contentPanel.setLayout(new GridLayout(0, 3, 5, 5)); // Grid layout for image grid

        Path imageDir = Paths.get("img", "uploaded");
        try (Stream<Path> paths = Files.list(imageDir)) {
            paths.filter(path -> path.getFileName().toString().startsWith(currentUser.getUsername() + "_"))
                    .forEach(path -> {
                        ImageIcon imageIcon = new ImageIcon(new ImageIcon(path.toString()).getImage()
                                .getScaledInstance(GRID_IMAGE_SIZE, GRID_IMAGE_SIZE, Image.SCALE_SMOOTH));
                        JLabel imageLabel = new JLabel(imageIcon);
                        imageLabel.addMouseListener(new MouseAdapter() {
                            @Override
                            public void mouseClicked(MouseEvent e) {
                                displayImage(imageIcon); // Call method to display the clicked image
                            }
                        });
                        contentPanel.add(imageLabel);
                    });
        } catch (IOException ex) {
            ex.printStackTrace();
            // Handle exception (e.g., show a message or log)
        }

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        add(scrollPane, BorderLayout.CENTER); // Add the scroll pane to the center

        revalidate();
        repaint();
    }

    private void displayImage(ImageIcon imageIcon) {
        contentPanel.removeAll(); // Remove existing content
        contentPanel.setLayout(new BorderLayout()); // Change layout for image display

        JLabel fullSizeImageLabel = new JLabel(imageIcon);
        fullSizeImageLabel.setHorizontalAlignment(JLabel.CENTER);
        contentPanel.add(fullSizeImageLabel, BorderLayout.CENTER);

        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> {
            getContentPane().removeAll(); // Remove all components from the frame
            initializeUI(); // Re-initialize the UI
        });
        contentPanel.add(backButton, BorderLayout.SOUTH);

        revalidate();
        repaint();
    }

    private JLabel createStatLabel(String number, String text) {
        JLabel label = new JLabel("<html><div style='text-align: center;'>" + number + "<br/>" + text + "</div></html>",
                SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 12));
        label.setForeground(Color.BLACK);
        return label;
    }

    private JButton createIconButton(String iconPath, String buttonType) {
        ImageIcon iconOriginal = new ImageIcon(iconPath);
        Image iconScaled = iconOriginal.getImage().getScaledInstance(NAV_ICON_SIZE, NAV_ICON_SIZE, Image.SCALE_SMOOTH);
        JButton button = new JButton(new ImageIcon(iconScaled));
        button.setBorder(BorderFactory.createEmptyBorder());
        button.setContentAreaFilled(false);

        // Define actions based on button type
        if ("home".equals(buttonType)) {
            button.addActionListener(e -> openHomeUI());
        } else if ("profile".equals(buttonType)) {
            //
        } else if ("notification".equals(buttonType)) {
            button.addActionListener(e -> notificationsUI());
        } else if ("explore".equals(buttonType)) {
            button.addActionListener(e -> exploreUI());
        } else if ("add".equals(buttonType)) {
            button.addActionListener(e -> ImageUploadUI());
        }
        return button;

    }

    private void ImageUploadUI() {
        this.dispose();
        ImageUploadUI upload = new ImageUploadUI();
        upload.setVisible(true);
    }

    private void notificationsUI() {
        this.dispose();
        NotificationsUI notificationsUI = new NotificationsUI();
        notificationsUI.setVisible(true);
    }

    private void openHomeUI() {
        this.dispose();
        HomeUI homeUI = new HomeUI();
        homeUI.setVisible(true);
    }

    private void exploreUI() {
        this.dispose();
        ExploreUI explore = new ExploreUI();
        explore.setVisible(true);
    }
    private void openEditProfileUI() {
        this.dispose(); // Close current profile UI
        EditProfileUI editProfileUI = new EditProfileUI(currentUser); // Open edit profile UI
        editProfileUI.setVisible(true);
        }
    

}
