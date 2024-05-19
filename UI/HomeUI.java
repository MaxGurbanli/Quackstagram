package UI;
import javax.imageio.ImageIO;
import javax.swing.*;

import Util.DatabaseConnection;
import Util.DisplayMessage;
import Util.InitializeUI;
import Util.Picture;
import Util.User;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class HomeUI extends JFrame {
    private static final int WIDTH = 300;
    private static final int HEIGHT = 500;
    private static final int IMAGE_WIDTH = WIDTH - 100;
    private static final int IMAGE_HEIGHT = 150;
    private static final Color LIKE_BUTTON_COLOR = new Color(255, 90, 95);
    private CardLayout cardLayout;
    private JPanel cardPanel;
    private JPanel homePanel;
    private JPanel imageViewPanel;

    public HomeUI() {
        InitializeUI.setupFrame(this, "Home");

        // Initialize the CardLayout before using it in the cardPanel
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        JPanel headerPanel = InitializeUI.createHeaderPanel("🐥 Quackstagram 🐥");
        homePanel = new JPanel(new BorderLayout());
        imageViewPanel = new JPanel(new BorderLayout());

        initializeUI();

        cardPanel.add(homePanel, "Home");
        cardPanel.add(imageViewPanel, "ImageView");

        ActionListener[] actions = {
                e -> openHomeUI(),
                e -> exploreUI(),
                e -> ImageUploadUI(),
                e -> notificationsUI(),
                e -> openProfileUI()
        };
        JPanel navigationPanel = InitializeUI.createNavigationPanel(actions);

        InitializeUI.addComponents(this, headerPanel, cardPanel, navigationPanel);
        cardLayout.show(cardPanel, "Home");
    }

    private void initializeUI() {
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        String[][] sampleData = createSampleData();
        populateContentPanel(contentPanel, sampleData);
        homePanel.add(scrollPane, BorderLayout.CENTER);
    }

    private void populateContentPanel(JPanel panel, String[][] sampleData) {

        for (String[] postData : sampleData) {
            JPanel itemPanel = new JPanel();
            itemPanel.setLayout(new BoxLayout(itemPanel, BoxLayout.Y_AXIS));
            itemPanel.setBackground(Color.WHITE); // Set the background color for the item panel
            itemPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            itemPanel.setAlignmentX(CENTER_ALIGNMENT);
            JLabel nameLabel = new JLabel(postData[0]);
            nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

            // Crop the image to the fixed size
            JLabel imageLabel = new JLabel();
            imageLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            imageLabel.setPreferredSize(new Dimension(IMAGE_WIDTH, IMAGE_HEIGHT));
            imageLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK)); // Add border to image label
            String imagePath = new File(postData[3]).getName();
            try {
                BufferedImage originalImage = ImageIO.read(new File(postData[3]));
                BufferedImage croppedImage = originalImage.getSubimage(0, 0,
                        Math.min(originalImage.getWidth(), IMAGE_WIDTH),
                        Math.min(originalImage.getHeight(), IMAGE_HEIGHT));
                ImageIcon imageIcon = new ImageIcon(croppedImage);
                imageLabel.setIcon(imageIcon);
            } catch (IOException ex) {
                // Handle exception: Image file not found or reading error
                imageLabel.setText("Image not found");
            }

            JLabel descriptionLabel = new JLabel(postData[1]);
            descriptionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            
            Picture picture = Picture.getPictureByPath(imagePath);
            int likesCount = picture.getLikesCount();
            JLabel likesLabel = new JLabel(likesCount + " likes");
            likesLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

            JButton likeButton = new JButton("❤");
            likeButton.setAlignmentX(Component.LEFT_ALIGNMENT);
            likeButton.setBackground(LIKE_BUTTON_COLOR); // Set the background color for the like button
            likeButton.setOpaque(true);
            likeButton.setBorderPainted(false); // Remove border
            likeButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    handleLikeAction(imagePath, likesLabel);
                }
            });

            // Check if post has been saved by user
            String currentUser = User.getLoggedInUser().getUsername();
            String savedImagePath = "img/saved/" + currentUser + "_" + imagePath;
            File savedImageFile = new File(savedImagePath);
            boolean isSaved = savedImageFile.exists();

            JButton saveButton = new JButton();

            if (isSaved) {
                saveButton.setText("💾 Saved");
                saveButton.setEnabled(false);
            } else {
                saveButton.setText("💾 Save");
            }
            saveButton.setAlignmentX(Component.LEFT_ALIGNMENT);
            saveButton.setBackground(Color.GREEN); // Set the background color for the save button
            saveButton.setOpaque(true);
            saveButton.setBorderPainted(false); // Remove border


            saveButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    handleSaveAction(imagePath, saveButton);
                }
            });

            itemPanel.add(nameLabel);
            itemPanel.add(imageLabel);
            itemPanel.add(descriptionLabel);
            itemPanel.add(likesLabel);
            itemPanel.add(likeButton);
            itemPanel.add(saveButton);

            panel.add(itemPanel);

            // Make the image clickable
            imageLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    displayImage(postData); // Call a method to switch to the image view
                }
            });

            // Grey spacing panel
            JPanel spacingPanel = new JPanel();
            spacingPanel.setPreferredSize(new Dimension(WIDTH - 10, 5)); // Set the height for spacing
            spacingPanel.setBackground(new Color(230, 230, 230)); // Grey color for spacing
            panel.add(spacingPanel);
        }
    }

    private void handleSaveAction(String imagePath, JButton saveButton) {
        // Save the image to the user's PC
        String currentUser = User.getLoggedInUser().getUsername();
        if (currentUser != null) {
            String sourcePath = "img/uploaded/" + imagePath;
            String destinationPath = "img/saved/" + currentUser + "_" + imagePath;
            try {
                Files.copy(Paths.get(sourcePath), Paths.get(destinationPath));
                saveButton.setText("💾 Saved");
                saveButton.setEnabled(false);
            } catch (IOException e) {
                DisplayMessage.displayError(this, "Failed to save the image");
            }
        }
    }

    private void handleLikeAction(String imagePath, JLabel likesLabel) {
        User currentUser = User.getLoggedInUser();
        Picture picture = Picture.getPictureByPath(imagePath);

        if (currentUser != null && picture != null) {
            if (picture.hasLiked(currentUser)) {
                picture.removeLike(currentUser);
                updateLikesCount(likesLabel, -1);
            } else {
                picture.addLike(currentUser);
                updateLikesCount(likesLabel, 1);
            }
        }
    }

    private void updateLikesCount(JLabel likesLabel, int increment) {
        String currentLikes = likesLabel.getText();
        int updatedLikes = Integer.parseInt(currentLikes.split(" ")[0]) + increment;
        likesLabel.setText(updatedLikes + " likes");
    }

    private String[][] createSampleData() {
        String currentUsername = User.getLoggedInUser().getUsername();
    
        // Temporary structure to hold the data
        String[][] tempData = new String[100][]; // Assuming a maximum of 100 posts for simplicity
        int count = 0;
    
        try {
            // Connect to the database
            Connection conn = DatabaseConnection.getConnection();
            Statement stmt = conn.createStatement();
    
            // Execute a SQL query to get image details
            ResultSet rs = stmt.executeQuery("SELECT * FROM Picture WHERE authorId IN (SELECT targetId FROM Follow WHERE followerId = (SELECT id FROM User WHERE username = '" + currentUsername + "'))");
    
            while (rs.next() && count < tempData.length) {
                // Get image poster name using authorId to fetch 
                Picture picture = Picture.getPictureByPath(rs.getString("imagePath"));
                String imagePoster = User.getUserById(rs.getInt("authorId")).getUsername();
                String imagePath = "img/uploaded/" + rs.getString("imagePath");
                String description = rs.getString("caption");
    
                int numLikes = picture.getLikesCount();
                String likes = numLikes + " likes";
    
                tempData[count++] = new String[] { imagePoster, description, likes, imagePath };
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    
        // Transfer the data to the final array
        String[][] sampleData = new String[count][];
        System.arraycopy(tempData, 0, sampleData, 0, count);
    
        return sampleData;
    }

    private void displayImage(String[] postData) {
        imageViewPanel.removeAll(); // Clear previous content

        String imagePath = new File(postData[3]).getName().split("\\.")[0];
        Picture picture = Picture.getPictureByPath(imagePath);
        JLabel likesLabel = new JLabel(picture.getAuthor() + " likes");

        // Display the image
        JLabel fullSizeImageLabel = new JLabel();
        fullSizeImageLabel.setHorizontalAlignment(JLabel.CENTER);

        try {
            BufferedImage originalImage = ImageIO.read(new File(postData[3]));
            BufferedImage croppedImage = originalImage.getSubimage(0, 0, Math.min(originalImage.getWidth(), WIDTH - 20),
                    Math.min(originalImage.getHeight(), HEIGHT - 40));
            ImageIcon imageIcon = new ImageIcon(croppedImage);
            fullSizeImageLabel.setIcon(imageIcon);
        } catch (IOException ex) {
            // Handle exception: Image file not found or reading error
            fullSizeImageLabel.setText("Image not found");
        }

        // User Info
        JPanel userPanel = new JPanel();
        userPanel.setLayout(new BoxLayout(userPanel, BoxLayout.Y_AXIS));
        JLabel userName = new JLabel(postData[0]);
        userName.setFont(new Font("Arial", Font.BOLD, 18));
        userPanel.add(userName);// User Name

        JButton likeButton = new JButton("❤");
        likeButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        likeButton.setBackground(LIKE_BUTTON_COLOR); // Set the background color for the like button
        likeButton.setOpaque(true);
        likeButton.setBorderPainted(false); // Remove border
        likeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLikeAction(imagePath, likesLabel);
            }
        });

        // Information panel at the bottom
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.add(new JLabel(postData[1])); // Description
        infoPanel.add(likesLabel); // Likes count
        infoPanel.add(likeButton);

        imageViewPanel.add(fullSizeImageLabel, BorderLayout.CENTER);
        imageViewPanel.add(infoPanel, BorderLayout.SOUTH);
        imageViewPanel.add(userPanel, BorderLayout.NORTH);

        imageViewPanel.revalidate();
        imageViewPanel.repaint();

        cardLayout.show(cardPanel, "ImageView"); // Switch to the image view
    }

    private void ImageUploadUI() {
        // Open QuackstagramProfileUI frame
        this.dispose();
        ImageUploadUI upload = new ImageUploadUI();
        upload.setLocationRelativeTo(null);
        upload.setVisible(true);
    }

    private void openProfileUI() {
        // Open QuackstagramProfileUI frame
        this.dispose();
        User user = User.getLoggedInUser();
        ProfileUI profileUI = new ProfileUI(user);
        profileUI.setLocationRelativeTo(null);
        profileUI.setVisible(true);
    }

    private void notificationsUI() {
        // Open QuackstagramProfileUI frame
        this.dispose();
        NotificationsUI notificationsUI = new NotificationsUI();
        notificationsUI.setLocationRelativeTo(null);
        notificationsUI.setVisible(true);
    }

    private void openHomeUI() {
        // Open QuackstagramProfileUI frame
        this.dispose();
        HomeUI homeUI = new HomeUI();
        homeUI.setLocationRelativeTo(null);
        homeUI.setVisible(true);
    }

    private void exploreUI() {
        // Open QuackstagramProfileUI frame
        this.dispose();
        ExploreUI explore = new ExploreUI();
        explore.setLocationRelativeTo(null);
        explore.setVisible(true);
    }

}
