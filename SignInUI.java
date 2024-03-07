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
        initializeSignInUI();
    }

    private void initializeSignInUI() {
        setTitle("Quackstagram - Sign In");
        setSize(WIDTH, HEIGHT);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
    
        add(UIComponentsUtil.createHeaderPanel("Quackstagram 🐥"), BorderLayout.NORTH);
    
        lblPhoto = UIComponentsUtil.createPhotoLabel("img/logos/DACS.png");
        JPanel photoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        photoPanel.add(lblPhoto);
    
        JPanel fieldsPanel = new JPanel();
        fieldsPanel.setLayout(new BoxLayout(fieldsPanel, BoxLayout.Y_AXIS));
        fieldsPanel.setBorder(BorderFactory.createEmptyBorder(5, 20, 5, 20));
    
        fieldsPanel.add(Box.createVerticalStrut(10));
        fieldsPanel.add(photoPanel);
        fieldsPanel.add(Box.createVerticalStrut(10));
        
        JLabel lblUsername = new JLabel("Username");
        lblUsername.setFont(new Font("Arial", Font.BOLD, 12));
        txtUsername = UIComponentsUtil.createTextField(Color.GRAY);
        JLabel lblPassword = new JLabel("Password");
        lblPassword.setFont(new Font("Arial", Font.BOLD, 12));
        txtPassword = UIComponentsUtil.createPasswordField(Color.GRAY);

        fieldsPanel.add(lblUsername);
        fieldsPanel.add(txtUsername);
        fieldsPanel.add(lblPassword);
        fieldsPanel.add(txtPassword);
    
        btnSignIn = UIComponentsUtil.createButton("Sign-In", this::onSignInClicked);
        btnRegisterNow = UIComponentsUtil.createButton("No Account? Register Now", this::onRegisterNowClicked);
        btnRegisterNow.setBackground(Color.WHITE);
    
        JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        buttonPanel.setBackground(Color.white);
        buttonPanel.add(btnSignIn);
        buttonPanel.add(btnRegisterNow);
    
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
