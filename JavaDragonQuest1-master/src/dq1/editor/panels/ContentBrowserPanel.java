package dq1.editor.panels;

import javax.swing.*;
import java.awt.*;

public class ContentBrowserPanel extends JPanel {
    public ContentBrowserPanel() {
        setLayout(new BorderLayout());
        JLabel lbl = new JLabel("Content Browser (Assets)", SwingConstants.CENTER);
        lbl.setFont(lbl.getFont().deriveFont(Font.BOLD, 14f));
        add(lbl, BorderLayout.NORTH);

        JList<String> list = new JList<>(new String[]{"assets/res/sprites/tiles.png", "assets/res/audio/music.ogg"});
        add(new JScrollPane(list), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottom.add(new JButton("Import"));
        bottom.add(new JButton("Reimport"));
        add(bottom, BorderLayout.SOUTH);
    }
}
