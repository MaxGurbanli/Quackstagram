package UI;
import javax.swing.*;

import Util.DatabaseConnection;
import Util.DisplayMessage;
import Util.UIComponentsUtil;
import Util.User;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SignInUI extends JFrame {

    private static final int WIDTH = 300;
    private static final int HEIGHT = 500;

    private JTextField txtUsername;
    private JTextField txtPassword;
    private JButton btnSignIn, btnRegisterNow;
    private JLabel lblPhoto;
    private User newUser;

    public SignInUI() {
        setTitle("Sign In");
        setSize(WIDTH, HEIGHT);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        initializeSignInUI();
    }

    private void initializeSignInUI() {
        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createFieldsPanel(), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        return UIComponentsUtil.createHeaderPanel("Quackstagram 🐥");
    }

    private JPanel createFieldsPanel() {
        JPanel fieldsPanel = new JPanel();
        fieldsPanel.setLayout(new BoxLayout(fieldsPanel, BoxLayout.Y_AXIS));
        fieldsPanel.setBorder(BorderFactory.createEmptyBorder(5, 20, 5, 20));

        lblPhoto = UIComponentsUtil.createPhotoLabel("img/logos/DACS.png");
        JPanel photoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        photoPanel.add(lblPhoto);

        fieldsPanel.add(Box.createVerticalStrut(10));
        fieldsPanel.add(photoPanel);
        fieldsPanel.add(Box.createVerticalStrut(10));

        txtUsername = addTextField(fieldsPanel, "Username");
        txtPassword = addPasswordField(fieldsPanel, "Password");

        return fieldsPanel;
    }

    private JTextField addTextField(JPanel panel, String label) {
        JLabel jLabel = new JLabel(label);
        jLabel.setFont(new Font("Arial", Font.BOLD, 12));
        JTextField textField = UIComponentsUtil.createTextField("", Color.BLACK);
        panel.add(jLabel);
        panel.add(textField);
        return textField;
    }

    private JPasswordField addPasswordField(JPanel panel, String label) {
        JLabel jLabel = new JLabel(label);
        jLabel.setFont(new Font("Arial", Font.BOLD, 12));
        JPasswordField passwordField = UIComponentsUtil.createPasswordField("", Color.BLACK);
        panel.add(jLabel);
        panel.add(passwordField);
        return passwordField;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        buttonPanel.setBackground(Color.white);

        btnSignIn = UIComponentsUtil.createButton("Sign-In", this::onSignInClicked);
        btnRegisterNow = UIComponentsUtil.createButton("No Account? Register Now", this::onRegisterNowClicked);
        btnRegisterNow.setBackground(Color.WHITE);

        buttonPanel.add(btnSignIn);
        buttonPanel.add(btnRegisterNow);

        return buttonPanel;
    }

    private void onSignInClicked(ActionEvent event) {
        String enteredUsername = txtUsername.getText();
        String enteredPassword = txtPassword.getText();
        if (verifyCredentials(enteredUsername, enteredPassword)) {
            dispose();

            // Open the SignInUI frame
            SwingUtilities.invokeLater(() -> {
                ProfileUI profileUI = new ProfileUI(newUser);
                profileUI.setLocationRelativeTo(null);
                profileUI.setVisible(true);
            });
        } else {
            DisplayMessage.displayError(this, "Invalid username or password. Please try again.");
        }
    }

    private void onRegisterNowClicked(ActionEvent event) {
        // Close the SignInUI frame
        dispose();

        // Open the SignUpUI frame
        SwingUtilities.invokeLater(() -> {
            SignUpUI signUpFrame = new SignUpUI();
            signUpFrame.setLocationRelativeTo(null);
            signUpFrame.setVisible(true);
        });
    }

    private boolean verifyCredentials(String username, String password) {
        Connection conn = DatabaseConnection.getConnection();
        String sql = "SELECT bio FROM User WHERE username = ? AND password = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String bio = rs.getString("bio");
                newUser = new User(username, bio, password);
                User.setLoggedInUser(newUser);

                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

}
