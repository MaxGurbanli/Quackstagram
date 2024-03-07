import javax.swing.*;
import java.awt.*;

public class InitializeUI {

    public static void initializeUI(JFrame frame, JPanel headerPanel, JPanel mainContentPanel, JPanel navigationPanel) {
        frame.getContentPane().removeAll(); // Clear existing components
        frame.setLayout(new BorderLayout()); // Reset the layout manager

        // Add panels to the frame
        frame.add(headerPanel, BorderLayout.NORTH);
        frame.add(mainContentPanel, BorderLayout.CENTER);
        frame.add(navigationPanel, BorderLayout.SOUTH);

        frame.revalidate();
        frame.repaint();
    }
}