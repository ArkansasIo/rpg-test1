package dq1.editor;

import dq1.core.GameAPI;
import dq1.core.Resource;
import dq1.core.Tile;
import dq1.core.TileMap;
import dq1.core.WoWZoneSystem;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;

/**
 * Updated editor shell for game, engine, and framework tooling.
 */

public class GameEditorFrame extends JFrame {
    private final JTextArea overviewArea = createTextArea();
    private final JTextArea engineArea = createTextArea();
    private final JTextArea frameworkArea = createTextArea();
    private final JTextArea frameworkLogArea = createTextArea();
    private final JTextArea zonesArea = createTextArea();
    private final JTextArea dataArea = createTextArea();
    private final JTextArea ideArea = createTextArea();
    private final JTextArea systemsArea = createTextArea();
    private final JTextArea mapDesignInfoArea = createTextArea();
    private final MapCanvasPanel mapCanvasPanel = new MapCanvasPanel();
    private JComboBox<String> mapSelector;
    private JSpinner brushTileSpinner;
    private JSpinner zoomSpinner;

    // Editor panels for entities
    private final MonsterEditorPanel monsterEditorPanel = new MonsterEditorPanel();
    private final ItemEditorPanel itemEditorPanel = new ItemEditorPanel();
    private final WeaponEditorPanel weaponEditorPanel = new WeaponEditorPanel();
    private final ArmorEditorPanel armorEditorPanel = new ArmorEditorPanel();

    public GameEditorFrame() {
        super("Dragon Warrior Engine Editor");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(1100, 760));
        setLayout(new BorderLayout());

        setJMenuBar(buildMenuBar());
        add(buildMainTabs(), BorderLayout.CENTER);
        setLocationRelativeTo(null);

        refreshAllPanels();
    }

    public static void showEditor() {
        SwingUtilities.invokeLater(() -> new GameEditorFrame().setVisible(true));
    }

    private javax.swing.JMenuBar buildMenuBar() {
        javax.swing.JMenuBar menuBar = new javax.swing.JMenuBar();

        javax.swing.JMenu file = new javax.swing.JMenu("File");
        javax.swing.JMenuItem refresh = new javax.swing.JMenuItem("Refresh");
        refresh.addActionListener(e -> refreshAllPanels());
        file.add(refresh);
        javax.swing.JMenuItem close = new javax.swing.JMenuItem("Close");
        close.addActionListener(e -> dispose());
        file.add(close);
        menuBar.add(file);

        javax.swing.JMenu engine = new javax.swing.JMenu("Engine");
        javax.swing.JMenuItem runTick = new javax.swing.JMenuItem("Run Framework Tick");
        runTick.addActionListener(e -> {
            refreshFrameworkPanel();
            refreshFrameworkLogPanel();
        });
        engine.add(runTick);
        menuBar.add(engine);
        return menuBar;
    }

    private JTabbedPane buildMainTabs() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Overview", buildOverviewPanel());
        tabs.addTab("IDE", buildIdePanel());
        tabs.addTab("Engine", buildEnginePanel());
        tabs.addTab("Framework", buildFrameworkPanel());
        tabs.addTab("Systems", buildSystemsPanel());
        tabs.addTab("Map Design", buildMapDesignPanel());
        tabs.addTab("Zones", buildZonesPanel());
        tabs.addTab("Data", buildDataPanel());
        // Add entity editors as tabs
        tabs.addTab("Monsters", monsterEditorPanel);
        tabs.addTab("Items", itemEditorPanel);
        tabs.addTab("Weapons", weaponEditorPanel);
        tabs.addTab("Armor", armorEditorPanel);
        return tabs;
    }

    private JPanel buildIdePanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.add(new JScrollPane(ideArea), BorderLayout.CENTER);
        JPanel bottom = new JPanel();
        JButton compile = new JButton("Compile");
        compile.addActionListener(e -> ideArea.setText(String.join(System.lineSeparator(),
                GameEditorRuntimeAPI.compileProject())));
        JButton build = new JButton("Build");
        build.addActionListener(e -> ideArea.setText(String.join(System.lineSeparator(),
                GameEditorRuntimeAPI.buildProject())));
        JButton run = new JButton("Run");
        run.addActionListener(e -> ideArea.setText(String.join(System.lineSeparator(),
                GameEditorRuntimeAPI.runProject())));
        JButton mapInfo = new JButton("Map Summary");
        mapInfo.addActionListener(e -> {
            String mapId = GameAPI.getCurrentMapId();
            if (mapId == null || mapId.isBlank()) {
                mapId = "world";
            }
            ideArea.setText(String.join(System.lineSeparator(),
                    GameEditorRuntimeAPI.mapSummary(mapId)));
        });
        JButton export = new JButton("Export Map CSV");
        export.addActionListener(e -> {
            String mapId = GameAPI.getCurrentMapId();
            if (mapId == null || mapId.isBlank()) {
                mapId = "world";
            }
            String msg = GameEditorRuntimeAPI.exportMapToCsv(
                    mapId, "docs/editor_exports/" + mapId + "_editor_export.csv");
            ideArea.setText(msg);
        });
        bottom.add(compile);
        bottom.add(build);
        bottom.add(run);
        bottom.add(mapInfo);
        bottom.add(export);
        panel.add(bottom, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildOverviewPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.add(new JScrollPane(overviewArea), BorderLayout.CENTER);
        JButton refresh = new JButton("Refresh Overview");
        refresh.addActionListener(e -> refreshOverviewPanel());
        panel.add(refresh, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildEnginePanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.add(new JScrollPane(engineArea), BorderLayout.CENTER);

        JPanel bottom = new JPanel();
        bottom.add(new JLabel("Display Preset:"));
        JComboBox<String> preset = new JComboBox<>(new String[] { "HD", "720p", "1080p", "4K" });
        bottom.add(preset);
        JButton apply = new JButton("Apply");
        apply.addActionListener(e -> {
            Object selected = preset.getSelectedItem();
            if (selected != null) {
                GameAPI.applyDisplayPreset(selected.toString());
            }
            refreshEnginePanel();
        });
        JButton refresh = new JButton("Refresh");
        refresh.addActionListener(e -> refreshEnginePanel());
        bottom.add(apply);
        bottom.add(refresh);
        panel.add(bottom, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildFrameworkPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));

        JTabbedPane nested = new JTabbedPane();
        nested.addTab("Runtime Tick", new JScrollPane(frameworkArea));
        nested.addTab("Game Log", new JScrollPane(frameworkLogArea));
        panel.add(nested, BorderLayout.CENTER);

        JPanel controls = new JPanel();
        JButton tick = new JButton("Run Tick");
        tick.addActionListener(e -> {
            refreshFrameworkPanel();
            refreshFrameworkLogPanel();
        });
        JButton logs = new JButton("Refresh Logs");
        logs.addActionListener(e -> refreshFrameworkLogPanel());
        controls.add(tick);
        controls.add(logs);
        panel.add(controls, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildZonesPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.add(new JScrollPane(zonesArea), BorderLayout.CENTER);
        JButton refresh = new JButton("Refresh Zones");
        refresh.addActionListener(e -> refreshZonesPanel());
        panel.add(refresh, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildDataPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.add(new JScrollPane(dataArea), BorderLayout.CENTER);
        JButton refresh = new JButton("Reload Data Files");
        refresh.addActionListener(e -> refreshDataPanel());
        panel.add(refresh, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildSystemsPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.add(new JScrollPane(systemsArea), BorderLayout.CENTER);
        JButton refresh = new JButton("Refresh Systems");
        refresh.addActionListener(e -> refreshSystemsPanel());
        panel.add(refresh, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildMapDesignPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));

        JPanel top = new JPanel();
        top.add(new JLabel("Map:"));
        mapSelector = new JComboBox<>();
        top.add(mapSelector);

        top.add(new JLabel("Brush Tile ID:"));
        brushTileSpinner = new JSpinner(new SpinnerNumberModel(1, 0, 9999, 1));
        top.add(brushTileSpinner);

        top.add(new JLabel("Zoom:"));
        zoomSpinner = new JSpinner(new SpinnerNumberModel(2, 1, 4, 1));
        top.add(zoomSpinner);

        JButton apply = new JButton("Apply");
        apply.addActionListener(e -> {
            mapCanvasPanel.setBrushTileId((Integer) brushTileSpinner.getValue());
            mapCanvasPanel.setZoom((Integer) zoomSpinner.getValue());
            String mapId = (String) mapSelector.getSelectedItem();
            if (mapId != null) {
                mapCanvasPanel.loadMap(mapId);
            }
            refreshMapDesignInfo();
        });
        top.add(apply);

        JButton export = new JButton("Export CSV");
        export.addActionListener(e -> {
            String mapId = (String) mapSelector.getSelectedItem();
            if (mapId == null || mapId.isBlank()) {
                return;
            }
            String msg = GameEditorRuntimeAPI.exportMapToCsv(
                    mapId, "docs/editor_exports/" + mapId + "_designer_export.csv");
            mapDesignInfoArea.setText(msg + System.lineSeparator() + mapDesignInfoArea.getText());
        });
        top.add(export);

        panel.add(top, BorderLayout.NORTH);
        panel.add(new JScrollPane(mapCanvasPanel), BorderLayout.CENTER);
        panel.add(new JScrollPane(mapDesignInfoArea), BorderLayout.SOUTH);

        mapSelector.addActionListener(e -> {
            String mapId = (String) mapSelector.getSelectedItem();
            if (mapId != null) {
                mapCanvasPanel.loadMap(mapId);
                refreshMapDesignInfo();
            }
        });
        return panel;
    }

    private static JTextArea createTextArea() {
        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        area.setLineWrap(false);
        return area;
    }

    private void refreshAllPanels() {
        refreshOverviewPanel();
        refreshIdePanel();
        refreshEnginePanel();
        refreshFrameworkPanel();
        refreshFrameworkLogPanel();
        refreshSystemsPanel();
        refreshMapDesignPanel();
        refreshZonesPanel();
        refreshDataPanel();
    }

    private void refreshOverviewPanel() {
        List<String> lines = new ArrayList<>();
        lines.add("Game: " + GameAPI.getDisplayTitle());
        lines.add("State: " + GameAPI.getCurrentState());
        lines.add("Current Map: " + GameAPI.getCurrentMapId());
        lines.add("");
        lines.add("Engine Summary:");
        lines.addAll(GameAPI.getEngineSummaryLines());
        overviewArea.setText(String.join(System.lineSeparator(), lines));
    }

    private void refreshEnginePanel() {
        engineArea.setText(String.join(System.lineSeparator(), GameAPI.getEngineSummaryLines()));
    }

    private void refreshIdePanel() {
        List<String> lines = new ArrayList<>();
        lines.add("IDE Runtime API");
        lines.add("Map IDs loaded: " + GameEditorRuntimeAPI.listMapIds().size());
        lines.add("Current Map: " + GameAPI.getCurrentMapId());
        lines.add("");
        lines.add("System Editors:");
        lines.addAll(GameEditorRuntimeAPI.listSystemEditors());
        ideArea.setText(String.join(System.lineSeparator(), lines));
    }

    private void refreshFrameworkPanel() {
        frameworkArea.setText(String.join(System.lineSeparator(), GameAPI.getFrameworkRuntimeLines()));
    }

    private void refreshFrameworkLogPanel() {
        frameworkLogArea.setText(String.join(System.lineSeparator(), GameAPI.getFrameworkLogLines(30)));
    }

    private void refreshZonesPanel() {
        List<String> lines = new ArrayList<>();
        lines.add("WoW Zones: " + WoWZoneSystem.zones.size());
        lines.add("");
        for (WoWZoneSystem.WoWZone zone : WoWZoneSystem.zones) {
            lines.add("#" + zone.id + " " + zone.name + " [" + zone.continent + "] "
                    + "Biome=" + zone.biomeTitle + " Tier=" + zone.worldTier
                    + " Diff=" + zone.difficulty + " SubZones=" + zone.subZones.size());
        }
        zonesArea.setText(String.join(System.lineSeparator(), lines));
    }

    private void refreshDataPanel() {
        String[] files = new String[] {
            "docs/data/combat_framework_items.csv",
            "docs/data/combat_framework_enemies.csv",
            "docs/data/combat_framework_bosses.csv",
            "docs/data/combat_framework_loot_tables.csv"
        };
        List<String> lines = new ArrayList<>();
        for (String path : files) {
            lines.add("== " + path + " ==");
            File file = new File(path);
            if (!file.exists()) {
                lines.add("File not found.");
                lines.add("");
                continue;
            }
            int shown = 0;
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = br.readLine()) != null && shown < 8) {
                    lines.add(line);
                    shown++;
                }
            }
            catch (Exception ex) {
                lines.add("Read error: " + ex.getMessage());
            }
            lines.add("");
        }
        dataArea.setText(String.join(System.lineSeparator(), lines));
    }

    private void refreshSystemsPanel() {
        List<String> lines = new ArrayList<>();
        lines.add("RPG/MMORPG Feature Matrix");
        lines.addAll(GameAPI.getFeatureMatrixLines());
        lines.add("");
        lines.add("Framework Runtime Snapshot");
        lines.addAll(GameAPI.getFrameworkRuntimeLines());
        systemsArea.setText(String.join(System.lineSeparator(), lines));
    }

    private void refreshMapDesignPanel() {
        if (mapSelector == null || brushTileSpinner == null || zoomSpinner == null) {
            return;
        }
        List<String> mapIds = GameEditorRuntimeAPI.listMapIds();
        String selected = (String) mapSelector.getSelectedItem();
        mapSelector.removeAllItems();
        for (String mapId : mapIds) {
            mapSelector.addItem(mapId);
        }
        if (selected != null && mapIds.contains(selected)) {
            mapSelector.setSelectedItem(selected);
        }
        else if (!mapIds.isEmpty()) {
            mapSelector.setSelectedIndex(0);
        }
        mapCanvasPanel.setBrushTileId((Integer) brushTileSpinner.getValue());
        mapCanvasPanel.setZoom((Integer) zoomSpinner.getValue());
        String mapId = (String) mapSelector.getSelectedItem();
        if (mapId != null) {
            mapCanvasPanel.loadMap(mapId);
        }
        refreshMapDesignInfo();
    }

    private void refreshMapDesignInfo() {
        if (mapSelector == null) {
            return;
        }
        String mapId = (String) mapSelector.getSelectedItem();
        if (mapId == null) {
            mapDesignInfoArea.setText("No map selected.");
            return;
        }
        List<String> lines = new ArrayList<>();
        lines.add("Map design uses the same tile graphics as the game.");
        lines.add("Use mouse left-click/drag to paint tiles.");
        lines.add("Map: " + mapId);
        lines.add("Brush Tile ID: " + (brushTileSpinner == null ? "1" : brushTileSpinner.getValue()));
        lines.addAll(GameEditorRuntimeAPI.mapSummary(mapId));
        mapDesignInfoArea.setText(String.join(System.lineSeparator(), lines));
    }

    private static final class MapCanvasPanel extends JPanel {
        private TileMap map;
        private int brushTileId = 1;
        private int zoom = 2;

        MapCanvasPanel() {
            setBackground(new Color(24, 24, 26));
            java.awt.event.MouseAdapter painter = new java.awt.event.MouseAdapter() {
                @Override
                public void mousePressed(java.awt.event.MouseEvent e) {
                    paintAt(e.getX(), e.getY());
                }

                @Override
                public void mouseDragged(java.awt.event.MouseEvent e) {
                    paintAt(e.getX(), e.getY());
                }
            };
            addMouseListener(painter);
            addMouseMotionListener(painter);
        }

        void setBrushTileId(int value) {
            brushTileId = Math.max(0, value);
        }

        void setZoom(int value) {
            zoom = Math.max(1, Math.min(4, value));
            refreshPreferredSize();
            repaint();
        }

        void loadMap(String mapId) {
            try {
                map = Resource.getTileMap(mapId);
            }
            catch (Exception ignored) {
                map = null;
            }
            refreshPreferredSize();
            repaint();
        }

        private void paintAt(int px, int py) {
            if (map == null) {
                return;
            }
            int tileSize = 16 * zoom;
            int col = px / tileSize;
            int row = py / tileSize;
            if (row < 0 || col < 0 || row >= map.getRows() || col >= map.getCols()) {
                return;
            }
            map.setTile(row, col, brushTileId);
            repaint();
        }

        private void refreshPreferredSize() {
            if (map == null) {
                setPreferredSize(new Dimension(640, 480));
            }
            else {
                setPreferredSize(new Dimension(map.getCols() * 16 * zoom, map.getRows() * 16 * zoom));
            }
            revalidate();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            if (map == null) {
                g2.setColor(new Color(220, 220, 220));
                g2.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
                g2.drawString("Map not loaded.", 20, 36);
                return;
            }
            int tileSize = 16 * zoom;
            for (int row = 0; row < map.getRows(); row++) {
                for (int col = 0; col < map.getCols(); col++) {
                    Tile tile = map.getTile(row, col);
                    if (tile != null && tile.getImage() != null) {
                        g2.drawImage(tile.getImage(), col * tileSize, row * tileSize, tileSize, tileSize, null);
                    }
                    g2.setColor(new Color(0, 0, 0, 28));
                    g2.drawRect(col * tileSize, row * tileSize, tileSize, tileSize);
                }
            }
        }
    }
}
