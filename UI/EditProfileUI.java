package UI;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import Util.DisplayError;
import Util.User;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.*;

public class EditProfileUI extends JFrame {

    private static final int WIDTH = 300;
    private static final int HEIGHT = 500;
    private JTextField txtUsername;
    private JTextField txtPassword;
    private JTextArea txtBio;
    private JButton btnChangePfp;
    private JButton btnSave;
    private User user;

    public EditProfileUI(User user) {
        this.user = user;
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Edit Profile");
        setSize(WIDTH, HEIGHT);
        setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        txtUsername = new JTextField(user.getUsername());
        panel.add(createFieldPanel("Username", txtUsername));
        txtBio = new JTextArea(user.getBio());
        JScrollPane scrollPane = new JScrollPane(txtBio);
        scrollPane.setPreferredSize(new Dimension(200, 100));
        panel.add(createFieldPanel("Bio", scrollPane));
        txtPassword = new JTextField();
        panel.add(createFieldPanel("Password", txtPassword));
        btnChangePfp = new JButton("Change Profile Picture");
        btnSave = new JButton("Save");
        btnChangePfp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openPfpChooser();
            }
        });
        btnSave.addActionListener(e -> saveProfile());
        panel.add(btnChangePfp);
        panel.add(btnSave);

        add(panel, BorderLayout.CENTER);
    }

    private JPanel createFieldPanel(String label, Component component) {
        JPanel fieldPanel = new JPanel(new BorderLayout());
        fieldPanel.add(new JLabel(label), BorderLayout.NORTH);
        fieldPanel.add(component, BorderLayout.CENTER);
        return fieldPanel;
    }

    private void saveProfile() {
        String newUsername = txtUsername.getText();
        String newBio = txtBio.getText();
        String newPassword = txtPassword.getText();
        
        boolean usernameChanged = !newUsername.equals(user.getUsername());
        boolean bioChanged = !newBio.equals(user.getBio());
        boolean passwordChanged = !newPassword.isEmpty() && !newPassword.equals(user.getPassword());
    
        if (bioChanged) {
            user.setBio(newBio);
        }
        if (passwordChanged) {

             if (newPassword.length() < 6) {
                DisplayError.displayError(this, "Password must be at least 6 characters long.");
                return;
            }

            user.setPassword(newPassword);
        }

        if (usernameChanged) {
            if (newUsername.isEmpty()) {
                DisplayError.displayError(this, "Username cannot be empty.");
                return;
            } else if (newUsername.equals(user.getUsername())) {
                DisplayError.displayError(this, "Username cannot be the same as the current username.");
                return;
            } else if (User.doesUsernameExist(newUsername)) {
                DisplayError.displayError(this, "Username already exists. Please choose a different username.");
                return;
            } else if (!newUsername.matches("^[a-zA-Z0-9]*$")) {
                DisplayError.displayError(this, "Username can only contain alphanumeric characters.");
                return;
            }
            user.setUsername(newUsername);
        }
    
        dispose();
        ProfileUI profileUI = new ProfileUI(user);
        profileUI.setVisible(true);
    }
    

    public void openPfpChooser() {
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Image files", ImageIO.getReaderFileSuffixes());
        fileChooser.setFileFilter(filter);
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            saveProfilePicture(selectedFile, user.getUsername());
        }
    }

    private void saveProfilePicture(File file, String username) {
        try {
            BufferedImage image = ImageIO.read(file);
            File outputFile = new File("img/storage/profile/" + username + ".png");
            if (!outputFile.exists() || outputFile.delete()) {
                ImageIO.write(image, "png", outputFile);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
