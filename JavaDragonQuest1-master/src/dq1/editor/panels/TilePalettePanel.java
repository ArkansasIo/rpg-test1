package dq1.editor.panels;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.function.IntConsumer;

public class TilePalettePanel extends JPanel {
    private IntConsumer listener;
    private JPanel grid;

    public TilePalettePanel() {
        setLayout(new BorderLayout());
        JLabel lbl = new JLabel("Tile Palette", SwingConstants.CENTER);
        lbl.setFont(lbl.getFont().deriveFont(Font.BOLD, 14f));
        add(lbl, BorderLayout.NORTH);

        grid = new JPanel(new GridLayout(0, 4, 4, 4));
        add(new JScrollPane(grid), BorderLayout.CENTER);
    }

    public void setTileSelectedListener(IntConsumer l) {
        this.listener = l;
    }

    public void populateTiles(List<Integer> ids) {
        grid.removeAll();
        if (ids == null || ids.isEmpty()) {
            grid.add(new JLabel("No tiles"));
        } else {
            for (Integer id : ids) {
                JButton b = new JButton(id == null ? "-" : id.toString());
                b.addActionListener(e -> {
                    if (listener != null && id != null) listener.accept(id);
                });
                grid.add(b);
            }
        }
        revalidate();
        repaint();
    }
}