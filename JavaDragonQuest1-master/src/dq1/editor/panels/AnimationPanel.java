package dq1.editor.panels;

import javax.swing.*;
import java.awt.*;

public class AnimationPanel extends JPanel {
    public AnimationPanel() {
        setLayout(new BorderLayout());
        JLabel lbl = new JLabel("Animation / Timeline", SwingConstants.CENTER);
        lbl.setFont(lbl.getFont().deriveFont(Font.BOLD, 14f));
        add(lbl, BorderLayout.NORTH);

        JTextArea info = new JTextArea("Sprite frames and timeline will appear here.");
        info.setEditable(false);
        add(new JScrollPane(info), BorderLayout.CENTER);
    }
}
