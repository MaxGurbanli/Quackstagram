package UI;

import javax.swing.*;

import Util.User;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class EditProfileUI extends JFrame {
    private JTextField bioField;
    private JPasswordField passwordField;
    private JButton saveButton;

    public EditProfileUI() {
        setLayout(new FlowLayout());

        bioField = new JTextField(20);
        passwordField = new JPasswordField(20);
        saveButton = new JButton("Save");

        add(new JLabel("Bio:"));
        add(bioField);
        add(new JLabel("Password:"));
        add(passwordField);
        add(saveButton);

        saveButton.addActionListener(this::saveAction);
    }

    private void saveAction(ActionEvent event) {
        String bio = bioField.getText();
        char[] password = passwordField.getPassword();
        String username = User.getLoggedInUser().getUsername();

        try (BufferedReader reader = new BufferedReader(new FileReader("data/credentials.txt"))) {
            StringBuilder fileContent = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                String[] credentials = line.split(":");
                if (credentials[0].equals(username)) {
                    // Replace the old bio and password with the new ones
                    line = username + ":" + new String(password) + ":" + bio;
                }
                fileContent.append(line).append(System.lineSeparator());
            }

            // Write the new content to the file
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("data/credentials.txt"))) {
                writer.write(fileContent.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
}
}