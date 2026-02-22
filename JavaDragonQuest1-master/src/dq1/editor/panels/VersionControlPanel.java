package dq1.editor.panels;

import javax.swing.*;
import java.awt.*;

public class VersionControlPanel extends JPanel {
    public VersionControlPanel() {
        setLayout(new BorderLayout());
        JLabel lbl = new JLabel("Version Control", SwingConstants.CENTER);
        lbl.setFont(lbl.getFont().deriveFont(Font.BOLD, 14f));
        add(lbl, BorderLayout.NORTH);
        JTextArea t = new JTextArea("Git status / commit / push UI placeholder");
        t.setEditable(false);
        add(new JScrollPane(t), BorderLayout.CENTER);
    }
}
