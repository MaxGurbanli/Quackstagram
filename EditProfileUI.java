import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class EditProfileUI extends JFrame {

    private static final int WIDTH = 300;
    private static final int HEIGHT = 200;
    private JTextField txtPassword;
    private JTextField txtUsername;
    private JTextArea txtBio;
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
        btnSave = new JButton("Save");
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
        String newUsername = txtUsername.getText();
        String newBio = txtBio.getText();
        String newPassword = txtPassword.getText();

        // Update username in the file
        replaceUsernameInFile(user.getUsername(), newUsername);
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

    public static void replaceUsernameInFile(String oldUsername, String newUsername) {
        // Path to credentials.txt and usernames.txt files
        Path credentialsFilePath = Paths.get("data", "credentials.txt");
        Path usernamesFilePath = Paths.get("data", "usernames.txt");

        try {
            // Read all lines from credentials.txt
            BufferedReader credentialsReader = Files.newBufferedReader(credentialsFilePath);
            String credentialsLine;
            StringBuilder updatedUsernamesContent = new StringBuilder();
            int lineNumber = 1; // Line number counter

            // Iterate through each line in credentials.txt
            while ((credentialsLine = credentialsReader.readLine()) != null) {
                // If the line contains the old username, update it with the new username
                if (credentialsLine.startsWith(oldUsername)) {
                    updatedUsernamesContent.append(newUsername); // Add the new username
                } else {
                    updatedUsernamesContent.append(credentialsLine); // Keep the line as it is
                }

                // Append newline character after each line except the last one
                if (lineNumber != Files.readAllLines(credentialsFilePath).size()) {
                    updatedUsernamesContent.append(System.lineSeparator());
                }

                lineNumber++; // Increment line number
            }

            // Write the updated content back to usernames.txt
            BufferedWriter usernamesWriter = Files.newBufferedWriter(usernamesFilePath);
            usernamesWriter.write(updatedUsernamesContent.toString());

            // Close the readers and writers
            credentialsReader.close();
            usernamesWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
