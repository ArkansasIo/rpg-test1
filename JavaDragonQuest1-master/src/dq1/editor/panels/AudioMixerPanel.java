package dq1.editor.panels;

import javax.swing.*;
import java.awt.*;

public class AudioMixerPanel extends JPanel {
    public AudioMixerPanel() {
        setLayout(new BorderLayout());
        JLabel lbl = new JLabel("Audio Mixer", SwingConstants.CENTER);
        lbl.setFont(lbl.getFont().deriveFont(Font.BOLD, 14f));
        add(lbl, BorderLayout.NORTH);
        JTextArea t = new JTextArea("Mixer controls placeholder");
        t.setEditable(false);
        add(new JScrollPane(t), BorderLayout.CENTER);
    }
}
