package dq1.editor.panels;

import javax.swing.*;
import java.awt.*;

public class InspectorPanelStub extends JPanel {
    private final JTextArea props = new JTextArea();
    private final JButton playAudio = new JButton("Play");
    private final JButton clearAudio = new JButton("Clear Audio");
    private final JButton setTile = new JButton("Set Tile");
    private final JTextField tileField = new JTextField(6);
    private int lastRow = -1, lastCol = -1;

    public InspectorPanelStub() {
        setLayout(new BorderLayout());
        JLabel lbl = new JLabel("Inspector / Properties", SwingConstants.CENTER);
        lbl.setFont(lbl.getFont().deriveFont(Font.BOLD, 14f));
        add(lbl, BorderLayout.NORTH);

        props.setEditable(false);
        add(new JScrollPane(props), BorderLayout.CENTER);

        JPanel bottom = new JPanel();
        bottom.add(new JLabel("Tile ID:"));
        bottom.add(tileField);
        bottom.add(setTile);
        bottom.add(playAudio);
        bottom.add(clearAudio);
        add(bottom, BorderLayout.SOUTH);

        setTile.addActionListener(e -> {
            if (lastRow >= 0 && lastCol >= 0) {
                try {
                    int id = Integer.parseInt(tileField.getText().trim());
                    // callback will be wired by GameEditorFrame
                    MapEditorBindings.setTileOnMap(lastRow, lastCol, id);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Invalid tile id");
                }
            }
        });

        clearAudio.addActionListener(e -> {
            if (lastRow >= 0 && lastCol >= 0) MapEditorBindings.setAudioOnMap(lastRow, lastCol, null);
        });

        playAudio.addActionListener(e -> {
            if (lastRow >= 0 && lastCol >= 0) MapEditorBindings.playAudioAtCell(lastRow, lastCol);
        });
    }

    public void showCell(int row, int col, String summary) {
        lastRow = row; lastCol = col;
        props.setText(summary);
    }
}