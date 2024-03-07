import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class UIComponentsUtil {

    public static JPanel createHeaderPanel(String title) {
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        headerPanel.setBackground(new Color(51, 51, 51));
        JLabel lblRegister = new JLabel(title);
        lblRegister.setFont(new Font("Arial", Font.BOLD, 16));
        lblRegister.setForeground(Color.WHITE);
        headerPanel.add(lblRegister);
        headerPanel.setPreferredSize(new Dimension(300, 40));
        return headerPanel;
    }

    public static JLabel createPhotoLabel(String imagePath) {
        JLabel lblPhoto = new JLabel();
        lblPhoto.setPreferredSize(new Dimension(80, 80));
        lblPhoto.setHorizontalAlignment(JLabel.CENTER);
        lblPhoto.setVerticalAlignment(JLabel.CENTER);
        lblPhoto.setIcon(new ImageIcon(new ImageIcon(imagePath).getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH)));
        return lblPhoto;
    }

    public static JTextField createTextField(String text, Color foreground) {
        JTextField textField = new JTextField(text);
        textField.setForeground(foreground);
        return textField;
    }

    public static JPasswordField createPasswordField(String text, Color foreground) {
        JPasswordField passwordField = new JPasswordField(text);
        passwordField.setForeground(foreground);
        return passwordField;
    }

    public static JButton createButton(String text, ActionListener actionListener) {
        JButton button = new JButton(text);
        button.addActionListener(actionListener);
        button.setBackground(new Color(255, 90, 95));
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        return button;
    }
}
