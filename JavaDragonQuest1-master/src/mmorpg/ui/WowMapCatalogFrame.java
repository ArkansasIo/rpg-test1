package mmorpg.ui;

import mmorpg.world.WoWTileMapSystem;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Graphical WoW-style map catalog with title, id, biome, size and tile breakdown.
 */
public class WowMapCatalogFrame extends JFrame {

    private final DefaultListModel<String> mapListModel = new DefaultListModel<>();
    private final JList<String> mapList = new JList<>(mapListModel);
    private final JTextArea detailsArea = new JTextArea();

    private List<WoWTileMapSystem.MapRecord> maps = new ArrayList<>();

    public WowMapCatalogFrame() {
        super("WoW Map Catalog");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(960, 640));
        setLayout(new BorderLayout(8, 8));
        getContentPane().setBackground(new Color(18, 20, 26));

        JSplitPane splitPane = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                buildListPanel(),
                buildDetailsPanel());
        splitPane.setResizeWeight(0.35);
        splitPane.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        add(splitPane, BorderLayout.CENTER);
        setLocationRelativeTo(null);

        loadData();
    }

    private JPanel buildListPanel() {
        JPanel panel = new JPanel(new BorderLayout(6, 6));
        panel.setBorder(BorderFactory.createTitledBorder("Maps"));
        panel.setBackground(new Color(30, 33, 40));

        mapList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        mapList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int idx = mapList.getSelectedIndex();
                if (idx >= 0 && idx < maps.size()) {
                    showMapDetails(maps.get(idx));
                }
            }
        });

        panel.add(new JScrollPane(mapList), BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildDetailsPanel() {
        JPanel panel = new JPanel(new BorderLayout(6, 6));
        panel.setBorder(BorderFactory.createTitledBorder("Map Details"));
        panel.setBackground(new Color(30, 33, 40));

        detailsArea.setEditable(false);
        detailsArea.setBackground(new Color(20, 22, 28));
        detailsArea.setForeground(new Color(220, 220, 220));
        detailsArea.setLineWrap(true);
        detailsArea.setWrapStyleWord(true);

        panel.add(new JScrollPane(detailsArea), BorderLayout.CENTER);
        return panel;
    }

    private void loadData() {
        try {
            WoWTileMapSystem mapSystem = WoWTileMapSystem
                    .loadFromGameAssets(Path.of("assets", "res", "map"));
            maps = mapSystem.getMaps();
            mapListModel.clear();
            for (WoWTileMapSystem.MapRecord map : maps) {
                mapListModel.addElement("#" + map.getMapId() + " " + map.getMapTitle()
                        + " [" + map.getBiome() + "]");
            }
            if (!maps.isEmpty()) {
                mapList.setSelectedIndex(0);
            }
            else {
                detailsArea.setText("No map data found.");
            }
        } catch (Exception ex) {
            detailsArea.setText("Failed to load map catalog.\n" + ex.getMessage());
        }
    }

    private void showMapDetails(WoWTileMapSystem.MapRecord map) {
        StringBuilder sb = new StringBuilder();
        sb.append("Map ID: ").append(map.getMapId()).append('\n');
        sb.append("File ID: ").append(map.getMapFileId()).append('\n');
        sb.append("Title: ").append(map.getMapTitle()).append('\n');
        sb.append("Biome: ").append(map.getBiome()).append('\n');
        sb.append("Dimensions: ").append(map.getRows()).append(" x ").append(map.getCols()).append('\n');
        sb.append("Tile Count: ").append(map.getTileCount()).append('\n');
        sb.append('\n');
        sb.append("Top Tile IDs by Frequency:\n");

        map.getTileIdCounts().entrySet().stream()
                .sorted(Map.Entry.<Integer, Integer>comparingByValue(Comparator.reverseOrder()))
                .limit(12)
                .forEach(entry -> sb.append("  tileId ")
                        .append(entry.getKey())
                        .append(" -> ")
                        .append(entry.getValue())
                        .append('\n'));

        detailsArea.setText(sb.toString());
        detailsArea.setCaretPosition(0);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new WowMapCatalogFrame().setVisible(true));
    }
}
