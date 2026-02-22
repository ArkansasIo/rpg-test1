package dq1.editor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

/**
 * Lightweight game editor shell.
 * Keeps an extensible editor entrypoint while avoiding hard dependencies
 * on experimental runtime classes.
 */
public class GameEditorFrame extends JFrame {

    public GameEditorFrame() {
        super("Game Engine Editor");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(1000, 700));
        setLayout(new BorderLayout());

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Zones", createTextPanel("Zones editor placeholder"));
        tabs.addTab("Story", createTextPanel("Story editor placeholder"));
        tabs.addTab("Items", createTextPanel("Items editor placeholder"));
        tabs.addTab("Spells", createTextPanel("Spells editor placeholder"));
        tabs.addTab("Bosses", createTextPanel("Bosses editor placeholder"));

        add(tabs, BorderLayout.CENTER);
        setLocationRelativeTo(null);
    }

    private JPanel createTextPanel(String title) {
        JPanel panel = new JPanel(new BorderLayout());
        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        area.setText(title + "\n\n"
                + "This editor surface is intentionally minimal.\n"
                + "Hook your specific data providers and save/load actions here.");
        panel.add(new JScrollPane(area), BorderLayout.CENTER);
        return panel;
    }

    public static void showEditor() {
        SwingUtilities.invokeLater(() -> new GameEditorFrame().setVisible(true));
    }
}
