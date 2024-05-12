package UI;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import Util.DatabaseConnection;
import Util.InitializeUI;
import Util.User;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ImageUploadUI extends JFrame {

    private static final int WIDTH = 300;
    private static final int HEIGHT = 500;
    private JLabel imagePreviewLabel;
    private JTextArea bioTextArea;
    private JButton uploadButton;

    public ImageUploadUI() {
        InitializeUI.setupFrame(this, "Upload Image");
        JPanel headerPanel = InitializeUI.createHeaderPanel("Upload Image 📷");
        JPanel contentPanel = initializeContentPanel();

        ActionListener[] actions = {
                e -> openHomeUI(),
                e -> exploreUI(),
                e -> UploadImageUI(),
                e -> notificationsUI(),
                e -> openProfileUI()
        };
        JPanel navigationPanel = InitializeUI.createNavigationPanel(actions);

        InitializeUI.addComponents(this, headerPanel, contentPanel, navigationPanel);
    }

    private JPanel initializeContentPanel() {
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

        // Image preview
        imagePreviewLabel = new JLabel();
        imagePreviewLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        imagePreviewLabel.setPreferredSize(new Dimension(WIDTH, HEIGHT / 3));

        contentPanel.add(imagePreviewLabel);

        // Bio text area
        bioTextArea = new JTextArea("Enter a caption");
        bioTextArea.setAlignmentX(Component.CENTER_ALIGNMENT);
        bioTextArea.setLineWrap(true);
        bioTextArea.setWrapStyleWord(true);
        JScrollPane bioScrollPane = new JScrollPane(bioTextArea);
        bioScrollPane.setPreferredSize(new Dimension(WIDTH - 50, HEIGHT / 6));
        contentPanel.add(bioScrollPane);

        // Upload button
        uploadButton = new JButton("Upload Image");
        uploadButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        uploadButton.addActionListener(this::uploadAction);
        contentPanel.add(uploadButton);

        return contentPanel;
    }

    private void uploadAction(ActionEvent event) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select an image file");
        fileChooser.setAcceptAllFileFilterUsed(false);
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Image files", "png", "jpg", "jpeg");
        fileChooser.addChoosableFileFilter(filter);

        int returnValue = fileChooser.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try {
                String username = User.getLoggedInUser().getUsername();
                int authorID = User.getLoggedInUser().getId();
                int imageId = getNextImageId(username);
                String fileExtension = getFileExtension(selectedFile);
                String newFileName = authorID + "_" + imageId + "." + fileExtension;

                Path destPath = Paths.get("img", "uploaded", newFileName);
                Files.copy(selectedFile.toPath(), destPath, StandardCopyOption.REPLACE_EXISTING);

                // Save the bio and image ID to a text file
                try {
                    saveImageInfo(newFileName, username, bioTextArea.getText());
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                // Load the image from the saved path
                ImageIcon imageIcon = new ImageIcon(destPath.toString());

                // Check if imagePreviewLabel has a valid size
                if (imagePreviewLabel.getWidth() > 0 && imagePreviewLabel.getHeight() > 0) {
                    Image image = imageIcon.getImage();

                    // Calculate the dimensions for the image preview
                    int previewWidth = imagePreviewLabel.getWidth();
                    int previewHeight = imagePreviewLabel.getHeight();
                    int imageWidth = image.getWidth(null);
                    int imageHeight = image.getHeight(null);
                    double widthRatio = (double) previewWidth / imageWidth;
                    double heightRatio = (double) previewHeight / imageHeight;
                    double scale = Math.min(widthRatio, heightRatio);
                    int scaledWidth = (int) (scale * imageWidth);
                    int scaledHeight = (int) (scale * imageHeight);

                    // Set the image icon with the scaled image
                    imageIcon.setImage(image.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH));
                }

                imagePreviewLabel.setIcon(imageIcon);

                // Change the text of the upload button
                uploadButton.setText("Upload Another Image");

                JOptionPane.showMessageDialog(this, "Image uploaded and preview updated!");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error saving image: " + ex.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private int getNextImageId(String username) throws IOException {
        Path storageDir = Paths.get("img", "uploaded");
        if (!Files.exists(storageDir)) {
            Files.createDirectories(storageDir);
        }

        int maxId = 0;
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(storageDir, username + "_*")) {
            for (Path path : stream) {
                String fileName = path.getFileName().toString();
                int idEndIndex = fileName.lastIndexOf('.');
                if (idEndIndex != -1) {
                    String idStr = fileName.substring(username.length() + 1, idEndIndex);
                    try {
                        int id = Integer.parseInt(idStr);
                        if (id > maxId) {
                            maxId = id;
                        }
                    } catch (NumberFormatException ex) {
                        // Ignore invalid file names
                    }
                }
            }
        }
        return maxId + 1; // Return the next available ID
    }

    private void saveImageInfo(String imageId, String username, String bio) throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        String sql = "INSERT INTO Picture (imagePath, authorId, caption, timestamp) VALUES (?, (SELECT id FROM User WHERE username = ?), ?, ?)";

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, imageId);
            pstmt.setString(2, username);
            pstmt.setString(3, bio);
            pstmt.setString(4, timestamp);
            pstmt.executeUpdate();
        }
    }


    private String getFileExtension(File file) {
        String name = file.getName();
        int lastIndexOf = name.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return ""; // empty extension
        }
        return name.substring(lastIndexOf + 1);
    }

    private void UploadImageUI() {
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
