import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;


public class SignInUI extends JFrame {

    private static final int WIDTH = 300;
    private static final int HEIGHT = 500;

    private JTextField txtUsername;
    private JTextField txtPassword;
    private JButton btnSignIn, btnRegisterNow;
    private JLabel lblPhoto;
    private User newUser;
    

    public SignInUI() {
        setTitle("Quackstagram - Register");
        setSize(WIDTH, HEIGHT);
        setMinimumSize(new Dimension(WIDTH, HEIGHT));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Quackstagram - Register");
        setSize(WIDTH, HEIGHT);
        setMinimumSize(new Dimension(WIDTH, HEIGHT));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
    
        // Header with the Register label
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        headerPanel.setBackground(new Color(51, 51, 51)); // Set a darker background for the header
        JLabel lblRegister = new JLabel("Quackstagram 🐥");
        lblRegister.setFont(new Font("Arial", Font.BOLD, 16));
        lblRegister.setForeground(Color.WHITE); // Set the text color to white
        headerPanel.add(lblRegister);
        headerPanel.setPreferredSize(new Dimension(WIDTH, 40)); // Give the header a fixed height
    
        // Profile picture placeholder without border
        lblPhoto = new JLabel();
        lblPhoto.setPreferredSize(new Dimension(80, 80));
        lblPhoto.setHorizontalAlignment(JLabel.CENTER);
        lblPhoto.setVerticalAlignment(JLabel.CENTER);
        lblPhoto.setIcon(new ImageIcon(new ImageIcon("img/logos/DACS.png").getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH)));
        JPanel photoPanel = new JPanel(); // Use a panel to center the photo label
        photoPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        photoPanel.add(lblPhoto);
    
        // Text fields panel
        JPanel fieldsPanel = new JPanel();
        fieldsPanel.setLayout(new BoxLayout(fieldsPanel, BoxLayout.Y_AXIS));
        fieldsPanel.setBorder(BorderFactory.createEmptyBorder(5, 20, 5, 20));
    
        fieldsPanel.add(Box.createVerticalStrut(10));
        fieldsPanel.add(photoPanel);
    
        // Username label and text field
        JLabel lblUsername = new JLabel("Username");
        lblUsername.setFont(new Font("Arial", Font.BOLD, 12));
        lblUsername.setForeground(Color.BLACK);
        txtUsername = new JTextField();
        txtUsername.setForeground(Color.BLACK);
        fieldsPanel.add(Box.createVerticalStrut(10));
        fieldsPanel.add(lblUsername);
        fieldsPanel.add(txtUsername);
    
        // Password label and text field
        JLabel lblPassword = new JLabel("Password");
        lblPassword.setFont(new Font("Arial", Font.BOLD, 12));
        lblPassword.setForeground(Color.BLACK);
        txtPassword = new JPasswordField(); // Changed to JPasswordField for better security
        txtPassword.setForeground(Color.BLACK);
        fieldsPanel.add(Box.createVerticalStrut(10));
        fieldsPanel.add(lblPassword);
        fieldsPanel.add(txtPassword);
        fieldsPanel.add(Box.createVerticalStrut(10));
    
        // Sign-In and Register buttons
        btnSignIn = new JButton("Sign-In");
        btnSignIn.addActionListener(this::onSignInClicked);
        btnSignIn.setBackground(new Color(255, 90, 95)); // Use a red color that matches the mockup
        btnSignIn.setForeground(Color.BLACK); // Set the text color to black
        btnSignIn.setFocusPainted(false);
        btnSignIn.setBorderPainted(false);
        btnSignIn.setFont(new Font("Arial", Font.BOLD, 14));
    
        btnRegisterNow = new JButton("No Account? Register Now");
        btnRegisterNow.addActionListener(this::onRegisterNowClicked);
        btnRegisterNow.setBackground(Color.WHITE); // Set a different color for distinction
        btnRegisterNow.setForeground(Color.BLACK);
        btnRegisterNow.setFocusPainted(false);
        btnRegisterNow.setBorderPainted(false);
    
        JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 10, 10)); // Grid layout with 1 row, 2 columns
        buttonPanel.setBackground(Color.white);
        buttonPanel.add(btnSignIn);
        buttonPanel.add(btnRegisterNow);
    
        // Adding components to the frame
        add(headerPanel, BorderLayout.NORTH);
        add(fieldsPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    

   private void onSignInClicked(ActionEvent event) {
    String enteredUsername = txtUsername.getText();
    String enteredPassword = txtPassword.getText();
    System.out.println(enteredUsername+" <-> "+enteredPassword);
    if (verifyCredentials(enteredUsername, enteredPassword)) {
        System.out.println("It worked");
         // Close the SignUpUI frame
    dispose();

    // Open the SignInUI frame
    SwingUtilities.invokeLater(() -> {
        InstagramProfileUI profileUI = new InstagramProfileUI(newUser);
        profileUI.setVisible(true);
    });
    } else {
        System.out.println("It Didn't");
    }
}

private void onRegisterNowClicked(ActionEvent event) {
    // Close the SignInUI frame
    dispose();

    // Open the SignUpUI frame
    SwingUtilities.invokeLater(() -> {
        SignUpUI signUpFrame = new SignUpUI();
        signUpFrame.setVisible(true);
    });
}

private boolean verifyCredentials(String username, String password) {
    try (BufferedReader reader = new BufferedReader(new FileReader("data/credentials.txt"))) {
        String line;
        while ((line = reader.readLine()) != null) {
            String[] credentials = line.split(":");
            if (credentials[0].equals(username) && credentials[1].equals(password)) {
            String bio = credentials[2];
            // Create User object and save information
        newUser = new User(username, bio, password); // Assuming User constructor takes these parameters
        saveUserInformation(newUser);
    
                return true;
            }
        }
    } catch (IOException e) {
        e.printStackTrace();
    }
    return false;
}

   private void saveUserInformation(User user) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("data/users.txt", false))) {
            writer.write(user.toString());  // Implement a suitable toString method in User class
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SignInUI frame = new SignInUI();
            frame.setVisible(true);
        });
    }
}
