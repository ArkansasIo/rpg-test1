package dq1.editor.panels;

import javax.swing.*;
import java.awt.*;

public class DocsViewerPanel extends JPanel {
    public DocsViewerPanel() {
        setLayout(new BorderLayout());
        JLabel lbl = new JLabel("Documentation", SwingConstants.CENTER);
        lbl.setFont(lbl.getFont().deriveFont(Font.BOLD, 14f));
        add(lbl, BorderLayout.NORTH);
        JTextArea t = new JTextArea("Editor docs and tutorials placeholder\n\nYou can add markdown->HTML viewer later.");
        t.setEditable(false);
        add(new JScrollPane(t), BorderLayout.CENTER);
    }
}
