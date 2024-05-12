package UI;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.imageio.ImageIO;
import javax.swing.filechooser.FileNameExtensionFilter;

import Util.DatabaseConnection;
import Util.DisplayMessage;
import Util.UIComponentsUtil;
import Util.User;

public class SignUpUI extends JFrame {

    private static final int WIDTH = 300;
    private static final int HEIGHT = 500;

    private JTextField txtUsername;
    private JTextField txtPassword;
    private JTextField txtBio;
    private JButton btnRegister;
    private JLabel lblPhoto;
    private final String profilePhotoStoragePath = "img/storage/profile/";

    public SignUpUI() {
        setTitle("Register");
        setSize(WIDTH, HEIGHT);
        setMinimumSize(new Dimension(WIDTH, HEIGHT));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        initializeUI();
    }

    private void initializeUI() {
        JPanel headerPanel = createHeaderPanel();
        JPanel fieldsPanel = createFieldsPanel();
        JPanel registerPanel = createRegisterPanel();

        add(headerPanel, BorderLayout.NORTH);
        add(fieldsPanel, BorderLayout.CENTER);
        add(registerPanel, BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        return UIComponentsUtil.createHeaderPanel("Quackstagram 🐥");
    }

    private JPanel createFieldsPanel() {
        lblPhoto = UIComponentsUtil.createPhotoLabel("img/logos/DACS.png");
        JPanel photoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        photoPanel.add(lblPhoto);

        JPanel fieldsPanel = new JPanel();
        fieldsPanel.setLayout(new BoxLayout(fieldsPanel, BoxLayout.Y_AXIS));
        fieldsPanel.setBorder(BorderFactory.createEmptyBorder(5, 20, 5, 20));
        fieldsPanel.add(Box.createVerticalStrut(10));
        fieldsPanel.add(photoPanel);
        fieldsPanel.add(Box.createVerticalStrut(10));

        // Initialize text fields before adding them
        txtUsername = UIComponentsUtil.createTextField("", Color.BLACK);
        txtPassword = UIComponentsUtil.createTextField("", Color.BLACK);
        txtBio = UIComponentsUtil.createTextField("", Color.BLACK);

        addField(fieldsPanel, "Username", txtUsername);
        addField(fieldsPanel, "Password", txtPassword);
        addField(fieldsPanel, "Bio", txtBio);

        return fieldsPanel;
    }

    private void addField(JPanel panel, String labelText, JTextField textField) {
        JLabel label = new JLabel(labelText);
        panel.add(label);
        panel.add(textField); // Use the passed textField directly without reinitializing it
    }

    private JPanel createRegisterPanel() {
        btnRegister = UIComponentsUtil.createButton("Register", this::onRegisterClicked);
        JPanel registerPanel = new JPanel(new BorderLayout());
        registerPanel.setBackground(Color.WHITE);
        registerPanel.add(btnRegister, BorderLayout.CENTER);

        JButton btnSignIn = UIComponentsUtil.createButton("Already have an account? Sign In", e -> openSignInUI());
        btnSignIn.setBackground(Color.WHITE);
        registerPanel.add(btnSignIn, BorderLayout.SOUTH);

        return registerPanel;
    }

    private void onRegisterClicked(ActionEvent event) {
        String username = txtUsername.getText();
        String password = txtPassword.getText();
        String bio = txtBio.getText();

        if (username.isEmpty() || password.isEmpty() || bio.isEmpty()) {
            DisplayMessage.displayError(this, "Please fill out all fields");
            return;
        } else if (User.doesUsernameExist(username)) {
            DisplayMessage.displayError(this, "Username already exists. Please choose a different username.");
            return;
        } else if (password.length() < 6) {
            DisplayMessage.displayError(this, "Password must be at least 6 characters long.");
            return;
        }

        saveCredentials(username, password, bio);
        handleProfilePictureUpload();
        dispose();
        
        // Open the SignInUI frame
        SwingUtilities.invokeLater(() -> {
            SignInUI signInFrame = new SignInUI();
            signInFrame.setVisible(true);
        });
        DisplayMessage.displayInfo(this, "Account created successfully. Please sign in.");

    }

    private void saveCredentials(String username, String password, String bio) {
        Connection conn = DatabaseConnection.getConnection();
        String sql = "INSERT INTO User (username, password, bio) VALUES (?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setString(3, bio);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // Method to handle profile picture upload
    private void handleProfilePictureUpload() {
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Image files", ImageIO.getReaderFileSuffixes());
        fileChooser.setFileFilter(filter);
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            int nextId = User.getNextId();
            saveProfilePicture(selectedFile, nextId);
        }
    }

    private void saveProfilePicture(File file, int nextId) {
        try {
            BufferedImage image = ImageIO.read(file);
            File outputFile = new File(profilePhotoStoragePath + nextId + ".png");
            ImageIO.write(image, "png", outputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openSignInUI() {
        // Close the SignUpUI frame
        dispose();

        // Open the SignInUI frame
        SwingUtilities.invokeLater(() -> {
            SignInUI signInFrame = new SignInUI();
            signInFrame.setVisible(true);
        });
    }

}
