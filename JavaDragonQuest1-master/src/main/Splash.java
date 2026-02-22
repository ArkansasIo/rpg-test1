package main;

import javax.swing.*;
import java.awt.*;

/**
 * Simple splash manager using a Swing JFrame so it can be shown before JavaFX starts.
 */
public class Splash {
    private static JFrame frame;

    public static void show(String title) {
        // Run on AWT event thread
        try {
            SwingUtilities.invokeAndWait(() -> {
                frame = new JFrame();
                frame.setUndecorated(true);
                frame.setTitle(title);
                JPanel panel = new JPanel(new BorderLayout());
                panel.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 2));
                JLabel label = new JLabel(title, SwingConstants.CENTER);
                label.setFont(new Font("SansSerif", Font.BOLD, 18));
                panel.add(label, BorderLayout.CENTER);
                JLabel sub = new JLabel("Loading editor...", SwingConstants.CENTER);
                panel.add(sub, BorderLayout.SOUTH);
                frame.getContentPane().add(panel);
                frame.setSize(360, 120);
                frame.setLocationRelativeTo(null);
                frame.setAlwaysOnTop(true);
                frame.setVisible(true);
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void hide() {
        if (frame != null) {
            SwingUtilities.invokeLater(() -> {
                frame.setVisible(false);
                frame.dispose();
                frame = null;
            });
        }
    }
}
