package Util;

import java.awt.Component;
import javax.swing.JOptionPane;

public class DisplayMessage {
    public static void displayError(Component parentComponent, String message) {
        JOptionPane.showMessageDialog(parentComponent, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void displayInfo(Component parentComponent, String message) {
        JOptionPane.showMessageDialog(parentComponent, message, "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void displayWarning(Component parentComponent, String message) {
        JOptionPane.showMessageDialog(parentComponent, message, "Warning", JOptionPane.WARNING_MESSAGE);
    }

    public static boolean displayConfirm(Component parentComponent, String message) {
        int result = JOptionPane.showConfirmDialog(parentComponent, message, "Confirm", JOptionPane.YES_NO_OPTION);
        return result == JOptionPane.YES_OPTION;
    }
}
