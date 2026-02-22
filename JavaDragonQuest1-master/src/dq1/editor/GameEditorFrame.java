package dq1.editor;

import dq1.core.GameAPI;
import dq1.core.WoWZoneSystem;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JCheckBox;
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
    private final PixelTilesetEditorPanel pixelEditorPanel = new PixelTilesetEditorPanel();
    private final MapEditorCanvasPanel mapCanvasPanel = new MapEditorCanvasPanel();
    private JComboBox<String> mapSelector;
    private JComboBox<MapEditorCanvasPanel.Tool> toolSelector;
    private JComboBox<MapEditorCanvasPanel.Layer> layerSelector;
    private JComboBox<Integer> tilePaletteSelector;
    private JSpinner brushTileSpinner;
    private JSpinner zoomSpinner;
    private JCheckBox gridToggle;

    // Editor panels for entities
    private final MonsterEditorPanel monsterEditorPanel = new MonsterEditorPanel();
    private final ItemEditorPanel itemEditorPanel = new ItemEditorPanel();
    private final WeaponEditorPanel weaponEditorPanel = new WeaponEditorPanel();
    private final ArmorEditorPanel armorEditorPanel = new ArmorEditorPanel();
    private final AudioEditorPanel audioEditorPanel = new AudioEditorPanel();
    private final RenderGraphicsEditorPanel renderGraphicsEditorPanel = new RenderGraphicsEditorPanel();
    private final StorySystemsEditorPanel storySystemsEditorPanel = new StorySystemsEditorPanel();

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
        tabs.addTab("Pixels", pixelEditorPanel);
        tabs.addTab("Audio", audioEditorPanel);
        tabs.addTab("Graphics", renderGraphicsEditorPanel);
        tabs.addTab("Story", storySystemsEditorPanel);
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

        top.add(new JLabel("Tool:"));
        toolSelector = new JComboBox<>(MapEditorCanvasPanel.Tool.values());
        toolSelector.addActionListener(e ->
                mapCanvasPanel.setTool((MapEditorCanvasPanel.Tool) toolSelector.getSelectedItem()));
        top.add(toolSelector);

        top.add(new JLabel("Layer:"));
        layerSelector = new JComboBox<>(MapEditorCanvasPanel.Layer.values());
        layerSelector.addActionListener(e ->
                mapCanvasPanel.setLayer((MapEditorCanvasPanel.Layer) layerSelector.getSelectedItem()));
        top.add(layerSelector);

        top.add(new JLabel("Brush Tile ID:"));
        brushTileSpinner = new JSpinner(new SpinnerNumberModel(1, 0, 9999, 1));
        brushTileSpinner.addChangeListener(e ->
                mapCanvasPanel.setBrushTileId((Integer) brushTileSpinner.getValue()));
        top.add(brushTileSpinner);

        top.add(new JLabel("Palette:"));
        tilePaletteSelector = new JComboBox<>();
        tilePaletteSelector.addActionListener(e -> {
            Integer id = (Integer) tilePaletteSelector.getSelectedItem();
            if (id != null) {
                brushTileSpinner.setValue(id);
            }
        });
        top.add(tilePaletteSelector);

        top.add(new JLabel("Zoom:"));
        zoomSpinner = new JSpinner(new SpinnerNumberModel(2, 1, 4, 1));
        zoomSpinner.addChangeListener(e -> mapCanvasPanel.setZoom((Integer) zoomSpinner.getValue()));
        top.add(zoomSpinner);

        gridToggle = new JCheckBox("Grid", true);
        gridToggle.addActionListener(e -> mapCanvasPanel.setShowGrid(gridToggle.isSelected()));
        top.add(gridToggle);

        JButton apply = new JButton("Apply");
        apply.addActionListener(e -> applyMapSelection());
        top.add(apply);

        JButton undo = new JButton("Undo");
        undo.addActionListener(e -> {
            mapCanvasPanel.undo();
            refreshMapDesignInfo();
        });
        top.add(undo);

        JButton redo = new JButton("Redo");
        redo.addActionListener(e -> {
            mapCanvasPanel.redo();
            refreshMapDesignInfo();
        });
        top.add(redo);

        JButton clearLayer = new JButton("Clear Layer");
        clearLayer.addActionListener(e -> {
            mapCanvasPanel.clearActiveLayer();
            refreshMapDesignInfo();
        });
        top.add(clearLayer);

        JButton export = new JButton("Export Session CSV");
        export.addActionListener(e -> {
            String mapId = (String) mapSelector.getSelectedItem();
            if (mapId == null || mapId.isBlank()) {
                return;
            }
            String msg = mapCanvasPanel.exportSessionCsv(
                    "docs/editor_exports/" + mapId + "_designer_layers.csv");
            mapDesignInfoArea.setText(msg + System.lineSeparator() + mapDesignInfoArea.getText());
        });
        top.add(export);

        panel.add(top, BorderLayout.NORTH);
        panel.add(new JScrollPane(mapCanvasPanel), BorderLayout.CENTER);
        panel.add(new JScrollPane(mapDesignInfoArea), BorderLayout.SOUTH);

        mapSelector.addActionListener(e -> {
            applyMapSelection();
            refreshMapDesignInfo();
        });
        mapCanvasPanel.setTilePickListener(id -> brushTileSpinner.setValue(id));
        return panel;
    }

    private void applyMapSelection() {
        mapCanvasPanel.setBrushTileId((Integer) brushTileSpinner.getValue());
        mapCanvasPanel.setZoom((Integer) zoomSpinner.getValue());
        mapCanvasPanel.setShowGrid(gridToggle.isSelected());
        mapCanvasPanel.setTool((MapEditorCanvasPanel.Tool) toolSelector.getSelectedItem());
        mapCanvasPanel.setLayer((MapEditorCanvasPanel.Layer) layerSelector.getSelectedItem());
        String mapId = (String) mapSelector.getSelectedItem();
        if (mapId != null) {
            mapCanvasPanel.loadMap(mapId);
            refreshTilePalette(mapId);
        }
        refreshMapDesignInfo();
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
        if (mapSelector == null || brushTileSpinner == null || zoomSpinner == null
                || toolSelector == null || layerSelector == null) {
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
            refreshTilePalette(mapId);
        }
        refreshMapDesignInfo();
    }

    private void refreshTilePalette(String mapId) {
        if (tilePaletteSelector == null) {
            return;
        }
        tilePaletteSelector.removeAllItems();
        List<Integer> ids = GameEditorRuntimeAPI.getMapTileIds(mapId);
        for (Integer id : ids) {
            tilePaletteSelector.addItem(id);
        }
        int currentBrush = (Integer) brushTileSpinner.getValue();
        if (ids.contains(currentBrush)) {
            tilePaletteSelector.setSelectedItem(currentBrush);
        }
        else if (!ids.isEmpty()) {
            tilePaletteSelector.setSelectedIndex(0);
        }
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
        lines.add("Tools: Paint, Erase, Fill, Rect, Eyedropper.");
        lines.add("Layers: Base, Decoration, Collision.");
        lines.add("Map: " + mapId);
        lines.add("Tool: " + (toolSelector == null ? "Paint" : toolSelector.getSelectedItem()));
        lines.add("Layer: " + (layerSelector == null ? "Base" : layerSelector.getSelectedItem()));
        lines.add("Brush Tile ID: " + (brushTileSpinner == null ? "1" : brushTileSpinner.getValue()));
        lines.addAll(mapCanvasPanel.getEditorSummaryLines());
        lines.addAll(GameEditorRuntimeAPI.mapSummary(mapId));
        mapDesignInfoArea.setText(String.join(System.lineSeparator(), lines));
    }
}

