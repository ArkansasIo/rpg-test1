package dq1.editor.panels;

import javax.swing.*;
import java.awt.*;

public class WorldOutlinerPanel extends JPanel {
    public WorldOutlinerPanel() {
        setLayout(new BorderLayout());
        JLabel lbl = new JLabel("World Outliner", SwingConstants.CENTER);
        lbl.setFont(lbl.getFont().deriveFont(Font.BOLD, 14f));
        add(lbl, BorderLayout.NORTH);

        JTree tree = new JTree(new Object[]{"World", new Object[]{"Layer: Base", "Layer: Deco"}});
        add(new JScrollPane(tree), BorderLayout.CENTER);
    }
}
