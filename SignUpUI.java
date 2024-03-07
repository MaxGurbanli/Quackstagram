import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;
import javax.swing.filechooser.FileNameExtensionFilter;

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
        JLabel lblBio = new JLabel("Bio");
        lblBio.setFont(new Font("Arial", Font.BOLD, 12));

        txtUsername = UIComponentsUtil.createTextField(Color.BLACK);
        txtPassword = UIComponentsUtil.createPasswordField(Color.BLACK);
        txtBio = UIComponentsUtil.createTextField(Color.GRAY);
    
        fieldsPanel.add(lblUsername);
        fieldsPanel.add(txtUsername);
        fieldsPanel.add(Box.createVerticalStrut(10));
        fieldsPanel.add(lblPassword);
        fieldsPanel.add(txtPassword);
        fieldsPanel.add(Box.createVerticalStrut(10));
        fieldsPanel.add(lblBio);
        fieldsPanel.add(txtBio);
        fieldsPanel.add(Box.createVerticalStrut(10));
    
        btnUploadPhoto = UIComponentsUtil.createButton("Upload Photo", e -> handleProfilePictureUpload());
        JPanel photoUploadPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        photoUploadPanel.add(btnUploadPhoto);
        fieldsPanel.add(photoUploadPanel);
    
        btnRegister = UIComponentsUtil.createButton("Register", this::onRegisterClicked);
        JPanel registerPanel = new JPanel(new BorderLayout());
        registerPanel.setBackground(Color.WHITE);
        registerPanel.add(btnRegister, BorderLayout.CENTER);
    
        // "Sign In" button allows the user to go back to the SignInUI
        JButton btnSignIn = UIComponentsUtil.createButton("Already have an account? Sign In", e -> openSignInUI());
        btnSignIn.setBackground(Color.WHITE);
        registerPanel.add(btnSignIn, BorderLayout.SOUTH);
    
        add(fieldsPanel, BorderLayout.CENTER);
        add(registerPanel, BorderLayout.SOUTH);
    }

    private void onRegisterClicked(ActionEvent event) {
        String username = txtUsername.getText();
        String password = txtPassword.getText();
        String bio = txtBio.getText();

        if (doesUsernameExist(username)) {
            JOptionPane.showMessageDialog(this, "Username already exists. Please choose a different username.", "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            saveCredentials(username, password, bio);
            handleProfilePictureUpload();
            dispose();
    
        // Open the SignInUI frame
        SwingUtilities.invokeLater(() -> {
            SignInUI signInFrame = new SignInUI();
            signInFrame.setVisible(true);
        });
        }
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
