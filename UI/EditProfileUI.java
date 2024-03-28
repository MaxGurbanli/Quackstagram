package UI;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.*;
import java.io.File;
import java.io.IOException;
import javax.swing.JFileChooser;
public class EditProfileUI extends JFrame {


    private static final int WIDTH = 300;
    private static final int HEIGHT = 200;
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
        panel.add(btnChangePfp);
        btnSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveProfile();
            }
        });
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
        String newBio = txtBio.getText();
        String newPassword = txtPassword.getText();


        // Update username in the file
        updateBioInCredentials(user.getUsername(), newBio);
        updatePasswordInCredentials(user.getUsername(),newPassword);
        dispose();
    }
    private void updatePasswordInCredentials(String username, String newPassword) {
        if (newPassword.isEmpty())
            return;
        File credentialsFile = new File("data/credentials.txt");
        File tempCredentialsFile = new File("data/credentials_temp.txt");


        BufferedReader credentialsReader = null;
        BufferedWriter credentialsWriter = null;


        try {
            credentialsReader = new BufferedReader(new FileReader(credentialsFile));
            credentialsWriter = new BufferedWriter(new FileWriter(tempCredentialsFile));


            String credentialsLine;


            while ((credentialsLine = credentialsReader.readLine()) != null) {
                String[] parts = credentialsLine.split(":");
                if (parts.length == 3 && parts[0].equals(username)) {
                    credentialsLine = parts[0] + ":" + newPassword + ":" + parts[2];
                }
                credentialsWriter.write(credentialsLine);
                credentialsWriter.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // Close all file streams in a finally block
            try {
                if (credentialsReader != null) credentialsReader.close();
                if (credentialsWriter != null) credentialsWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        // Delete the original file
        if (!credentialsFile.delete()) {
            System.out.println("Failed to delete original credentials file.");
            return;
        }


        // Rename the temporary file to replace the original file
        if (!tempCredentialsFile.renameTo(credentialsFile)) {
            System.out.println("Failed to update password in credentials file.");
        }
    }


    private void updateBioInCredentials(String username, String newBio) {
        if (newBio.isEmpty())
            return;
        File credentialsFile = new File("data/credentials.txt");
        File tempCredentialsFile = new File("data/credentials_temp.txt");
        BufferedReader credentialsReader = null;
        BufferedWriter credentialsWriter = null;


        try {
            credentialsReader = new BufferedReader(new FileReader(credentialsFile));
            credentialsWriter = new BufferedWriter(new FileWriter(tempCredentialsFile));


            String credentialsLine;


            while ((credentialsLine = credentialsReader.readLine()) != null) {
                String[] parts = credentialsLine.split(":");
                if (parts.length == 3 && parts[0].equals(username)) {
                    credentialsLine = parts[0] + ":" + parts[1] + ":" + newBio;
                }
                credentialsWriter.write(credentialsLine);
                credentialsWriter.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // Close all file streams in a finally block
            try {
                if (credentialsReader != null) credentialsReader.close();
                if (credentialsWriter != null) credentialsWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        // Delete the original file
        if (!credentialsFile.delete()) {
            System.out.println("Failed to delete original credentials file.");
            return;
        }


        // Rename the temporary file to replace the original file
        if (!tempCredentialsFile.renameTo(credentialsFile)) {
            System.out.println("Failed to update bio in credentials file.");
        }
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
            if (outputFile.exists()) {
                // If it exists, delete it before writing the new image
                outputFile.delete();
            }
            ImageIO.write(image, "png", outputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

