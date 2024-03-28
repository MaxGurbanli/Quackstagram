package UI;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;
import javax.swing.filechooser.FileNameExtensionFilter;

import Util.DisplayError;
import Util.UIComponentsUtil;

public class SignUpUI extends JFrame {

    private static final int WIDTH = 300;
    private static final int HEIGHT = 500;

    private JTextField txtUsername;
    private JTextField txtPassword;
    private JTextField txtBio;
    private JButton btnRegister;
    private JLabel lblPhoto;
    private JButton btnUploadPhoto;
    private final String credentialsFilePath = "data/credentials.txt";
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

        addPhotoUploadButton(fieldsPanel);

        return fieldsPanel;
    }

    private void addField(JPanel panel, String labelText, JTextField textField) {
        JLabel label = new JLabel(labelText);
        panel.add(label);
        panel.add(textField); // Use the passed textField directly without reinitializing it
    }

    private void addPhotoUploadButton(JPanel panel) {
        btnUploadPhoto = UIComponentsUtil.createButton("Upload Photo", e -> handleProfilePictureUpload());
        JPanel photoUploadPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        photoUploadPanel.add(btnUploadPhoto);
        panel.add(photoUploadPanel);
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
            DisplayError.displayError(this, "Please fill out all fields");
            return;
        } else if (doesUsernameExist(username)) {
            DisplayError.displayError(this, "Username already exists. Please choose a different username.");
            return;
        } else if (password.length() < 6) {
            DisplayError.displayError(this, "Password must be at least 6 characters long.");
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

    }

    private boolean doesUsernameExist(String username) {
        try (BufferedReader reader = new BufferedReader(new FileReader(credentialsFilePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith(username + ":")) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Method to handle profile picture upload
    private void handleProfilePictureUpload() {
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Image files", ImageIO.getReaderFileSuffixes());
        fileChooser.setFileFilter(filter);
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            saveProfilePicture(selectedFile, txtUsername.getText());
        }
    }

    private void saveProfilePicture(File file, String username) {
        try {
            BufferedImage image = ImageIO.read(file);
            File outputFile = new File(profilePhotoStoragePath + username + ".png");
            ImageIO.write(image, "png", outputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveCredentials(String username, String password, String bio) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("data/credentials.txt", true))) {
            writer.write(username + ":" + password + ":" + bio);
            writer.newLine();
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
