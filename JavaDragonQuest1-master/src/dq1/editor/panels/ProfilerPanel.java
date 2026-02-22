package dq1.editor.panels;

import javax.swing.*;
import java.awt.*;

public class ProfilerPanel extends JPanel {
    public ProfilerPanel() {
        setLayout(new BorderLayout());
        JLabel lbl = new JLabel("Profiler", SwingConstants.CENTER);
        lbl.setFont(lbl.getFont().deriveFont(Font.BOLD, 14f));
        add(lbl, BorderLayout.NORTH);
        JTextArea t = new JTextArea("Frame time graphs would show here (placeholder)");
        t.setEditable(false);
        add(new JScrollPane(t), BorderLayout.CENTER);
    }
}
