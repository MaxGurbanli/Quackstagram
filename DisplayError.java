import java.awt.Component;
import javax.swing.JOptionPane;

class DisplayError {
    public static void displayError(Component parentComponent, String message) {
        JOptionPane.showMessageDialog(parentComponent, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
