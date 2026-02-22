package dq1.editor.panels;

import javax.swing.*;
import java.awt.*;

public class ConsolePanel extends JPanel {
    private final JTextArea out = new JTextArea();
    public ConsolePanel() {
        setLayout(new BorderLayout());
        JLabel lbl = new JLabel("Console / Log", SwingConstants.CENTER);
        lbl.setFont(lbl.getFont().deriveFont(Font.BOLD, 14f));
        add(lbl, BorderLayout.NORTH);
        out.setEditable(false);
        out.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        add(new JScrollPane(out), BorderLayout.CENTER);
    }

    public void appendLine(String s) { out.append(s + "\n"); }
}
