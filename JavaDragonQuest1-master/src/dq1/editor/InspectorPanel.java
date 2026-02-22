package dq1.editor;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;

public class InspectorPanel extends JPanel {

    private JLabel nameLabel = new JLabel();
    private JLabel sizeLabel = new JLabel();
    private JLabel modifiedLabel = new JLabel();

    public InspectorPanel() {
        setLayout(new GridLayout(0,1));
        add(new JLabel("Inspector"));
        add(nameLabel);
        add(sizeLabel);
        add(modifiedLabel);
    }

    public void inspect(File f) {
        if (f == null) {
            nameLabel.setText("Name: (none)");
            sizeLabel.setText("Size: -");
            modifiedLabel.setText("Modified: -");
            return;
        }
        nameLabel.setText("Name: " + f.getName());
        try {
            long size = Files.size(f.toPath());
            sizeLabel.setText("Size: " + size + " bytes");
        } catch (Exception e) {
            sizeLabel.setText("Size: ?");
        }
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        modifiedLabel.setText("Modified: " + fmt.format(new Date(f.lastModified())));
    }
}
