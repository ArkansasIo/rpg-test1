package dq1.editor;

import dq1.core.GameAPI;
import dq1.core.WoWZoneSystem;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import javax.swing.JToolBar;
import javax.swing.JToggleButton;
import javax.swing.JTextField;
import javax.swing.JOptionPane;
import dq1.editor.panels.ContentBrowserPanel;
import dq1.editor.panels.WorldOutlinerPanel;
import dq1.editor.panels.InspectorPanelStub;
import dq1.editor.panels.TilePalettePanel;
import dq1.editor.panels.AnimationPanel;
import dq1.editor.panels.VisualScriptingPanelStub;
import dq1.editor.panels.DocsViewerPanel;
import dq1.editor.panels.ConsolePanel;
import dq1.editor.panels.ProfilerPanel;
import dq1.editor.panels.AudioMixerPanel;
import dq1.editor.panels.VersionControlPanel;

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
    private final TilePalettePanel tilePalettePanel = new TilePalettePanel();
    private final InspectorPanelStub inspectorPanel = new InspectorPanelStub();
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

    // Track undocked frames -> original tab info
    private final Map<JFrame, UndockedInfo> undockedFrames = new HashMap<>();
+
+    private static class UndockedInfo {
+        Component component;
+        String title;
+        int originalIndex;
+        UndockedInfo(Component c, String t, int i) {
+            component = c; title = t; originalIndex = i;
+        }
+    }

    public GameEditorFrame() {
        super("Eldrion Legends - 2D RPG Engine & Editor");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(1100, 760));
        setLayout(new BorderLayout());

        setJMenuBar(buildMenuBar());
+        // Add Unreal-style top toolbar (below the menu bar)
+        add(buildToolBar(), BorderLayout.NORTH);
        JTabbedPane mainTabs = buildMainTabs();
        add(mainTabs, BorderLayout.CENTER);
        installTabUndockHandler(mainTabs);
        // wire map bindings
        MapEditorBindings.mapCanvas = mapCanvasPanel;
        // tile palette -> map
        tilePalettePanel.setTileSelectedListener(id -> mapCanvasPanel.setBrushTileId(id));
        // when a cell is selected in canvas, show inspector details
        mapCanvasPanel.setCellSelectionListener((r,c) -> {
            String summary = "Cell (" + r + "," + c + ")";
            if (mapCanvasPanel.getAudioAttachment(r,c) != null) summary += "\nAudio: " + mapCanvasPanel.getAudioAttachment(r,c);
            summary += "\nTile: " + mapCanvasPanel.getVisibleTileIdAt(r,c);
            inspectorPanel.showCell(r,c, summary);
            tilePalettePanel.populateTiles(GameEditorRuntimeAPI.getMapTileIds(mapCanvasPanel.mapId == null ? "world" : mapCanvasPanel.mapId));
        });

        setLocationRelativeTo(null);

        refreshAllPanels();
    }

+    private void installTabUndockHandler(JTabbedPane tabs) {
+        tabs.addMouseListener(new MouseAdapter() {
+            @Override
+            public void mousePressed(MouseEvent e) {
+                if (e.isPopupTrigger() || e.getButton() == MouseEvent.BUTTON3) {
+                    int idx = tabs.indexAtLocation(e.getX(), e.getY());
+                    if (idx >= 0) {
+                        javax.swing.JPopupMenu popup = new javax.swing.JPopupMenu();
+                        javax.swing.JMenuItem undock = new javax.swing.JMenuItem("Undock Tab");
+                        undock.addActionListener(ae -> undockTab(tabs, idx));
+                        popup.add(undock);
+                        popup.show(tabs, e.getX(), e.getY());
+                    }
+                }
+            }
+        });
+    }
+
+    private void undockTab(JTabbedPane tabs, int index) {
+        try {
+            Component comp = tabs.getComponentAt(index);
+            String title = tabs.getTitleAt(index);
+            // remove from tabs
+            tabs.remove(index);
+
+            JFrame frame = new JFrame(title);
+            frame.getContentPane().add(comp);
+            frame.pack();
+            frame.setSize(Math.max(600, comp.getWidth()), Math.max(400, comp.getHeight()));
+            frame.setLocationRelativeTo(this);
+            frame.setVisible(true);
+
+            // track for re-docking
+            undockedFrames.put(frame, new UndockedInfo(comp, title, index));
+
+            frame.addWindowListener(new WindowAdapter() {
+                @Override
+                public void windowClosing(WindowEvent e) {
+                    // re-dock when the window is closed
+                    reDockFrame(frame);
+                }
+            });
+        } catch (Exception ex) {
+            ex.printStackTrace();
+        }
+    }
+
+    private void reDockFrame(JFrame frame) {
+        UndockedInfo info = undockedFrames.remove(frame);
+        if (info == null) return;
+        try {
+            Component comp = info.component;
+            String title = info.title;
+            // add back to the main tabbed pane at end
+            java.awt.Container content = comp.getParent();
+            if (content != null) content.remove(comp);
+            JTabbedPane tabs = findMainTabbedPane();
+            if (tabs != null) {
+                tabs.addTab(title, comp);
+                tabs.setSelectedComponent(comp);
+            }
+            frame.dispose();
+        } catch (Exception ex) {
+            ex.printStackTrace();
+        }
+    }
+
+    private JTabbedPane findMainTabbedPane() {
+        // main tabbed pane is the first JTabbedPane in the content hierarchy
+        for (Component c : getContentPane().getComponents()) {
+            if (c instanceof JTabbedPane) return (JTabbedPane) c;
+        }
+        return null;
+    }

    public static void showEditor() {
        SwingUtilities.invokeLater(() -> {
            GameEditorFrame editor = new GameEditorFrame();
            try {
                javax.swing.JFrame gameFrame = null;
                try {
                    // Use Game.getGameFrame() if available at runtime
                    Class<?> gameCls = Class.forName("dq1.core.Game");
                    java.lang.reflect.Method m = gameCls.getMethod("getGameFrame");
                    Object gf = m.invoke(null);
                    if (gf instanceof javax.swing.JFrame) {
                        gameFrame = (javax.swing.JFrame) gf;
                    }
                } catch (Throwable t) {
                    // ignore - game frame may not be available
                }

                if (gameFrame != null && gameFrame.isVisible()) {
                    // Position editor to the right of the game window when possible
                    java.awt.Rectangle gameBounds = gameFrame.getBounds();
                    java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();

                    int x = gameBounds.x + gameBounds.width + 8; // small gap
                    int y = gameBounds.y;

                    // Ensure editor fits horizontally; if not, place to left of game
                    if (x + editor.getWidth() > screenSize.width) {
                        x = gameBounds.x - editor.getWidth() - 8;
                    }
                    // Clamp y into screen
                    if (y + editor.getHeight() > screenSize.height) {
                        y = Math.max(0, screenSize.height - editor.getHeight());
                    }

                    editor.setLocation(x, y);
                } else {
                    // Fallback: center on screen
                    editor.setLocationRelativeTo(null);
                }
            } catch (Exception ex) {
                // If anything goes wrong, just center the window
                editor.setLocationRelativeTo(null);
            }
            editor.setVisible(true);
        });
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

        javax.swing.JMenu window = new javax.swing.JMenu("Window");
        javax.swing.JMenuItem showDock = new javax.swing.JMenuItem("Show Editor Dock");
        showDock.addActionListener(e -> EditorAPI.showDock());
        window.add(showDock);

        javax.swing.JMenuItem showMapEditor = new javax.swing.JMenuItem("Open Standalone Map Editor");
        showMapEditor.addActionListener(e -> EditorAPI.showMapEditorStandalone());
        window.add(showMapEditor);

        javax.swing.JMenuItem dockAll = new javax.swing.JMenuItem("Dock All");
        dockAll.addActionListener(e -> {
            for (JFrame frame : new ArrayList<>(undockedFrames.keySet())) {
                reDockFrame(frame);
            }
        });
        window.add(dockAll);
+
+        javax.swing.JMenu layouts = new javax.swing.JMenu("Layouts");
+        javax.swing.JMenuItem saveLayout = new javax.swing.JMenuItem("Save Layout");
+        saveLayout.addActionListener(e -> {
+            try {
+                java.nio.file.Path out = java.nio.file.Path.of("build/editor_layout.json");
+                java.nio.file.Files.createDirectories(out.getParent());
+                java.util.Map<String,Object> m = new java.util.HashMap<>();
+                m.put("undockedCount", undockedFrames.size());
+                try (java.io.Writer w = java.nio.file.Files.newBufferedWriter(out)) {
+                    w.write(m.toString());
+                }
+                javax.swing.JOptionPane.showMessageDialog(this, "Saved layout to " + out.toString());
+            } catch (Exception ex) { javax.swing.JOptionPane.showMessageDialog(this, "Save layout error: " + ex.getMessage()); }
+        });
+        layouts.add(saveLayout);
+        javax.swing.JMenuItem loadLayout = new javax.swing.JMenuItem("Load Layout");
+        loadLayout.addActionListener(e -> {
+            javax.swing.JOptionPane.showMessageDialog(this, "Layout loading is a placeholder (no-op) in this build.");
+        });
+        layouts.add(loadLayout);
+        window.add(layouts);

        menuBar.add(window);

        javax.swing.JMenu engine = new javax.swing.JMenu("Engine");
        javax.swing.JMenuItem runTick = new javax.swing.JMenuItem("Run Framework Tick");
        runTick.addActionListener(e -> {
            refreshFrameworkPanel();
            refreshFrameworkLogPanel();
        });
        engine.add(runTick);
        menuBar.add(engine);
+        javax.swing.JMenu help = new javax.swing.JMenu("Help");
+        javax.swing.JMenuItem docs = new javax.swing.JMenuItem("Documentation");
+        docs.addActionListener(e -> {
+            EditorAPI.showDock();
+            javax.swing.JOptionPane.showMessageDialog(this, "Documentation panel is available under Window->Panels or Docs tab.");
+        });
+        help.add(docs);
+        javax.swing.JMenuItem about = new javax.swing.JMenuItem("About");
+        about.addActionListener(e -> {
+            javax.swing.JOptionPane.showMessageDialog(this, "Eldrion Legends Editor\nVersion: dev\nAuthor: Project Team");
+        });
+        help.add(about);
+        menuBar.add(help);
         return menuBar;
    }

    private JTabbedPane buildMainTabs() {
        JTabbedPane tabs = new JTabbedPane();
-        tabs.addTab("Overview", buildOverviewPanel());
-        tabs.addTab("IDE", buildIdePanel());
-        tabs.addTab("Engine", buildEnginePanel());
-        tabs.addTab("Framework", buildFrameworkPanel());
-        tabs.addTab("Systems", buildSystemsPanel());
-        tabs.addTab("Map Design", buildMapDesignPanel());
-        tabs.addTab("Pixels", pixelEditorPanel);
-        tabs.addTab("Audio", audioEditorPanel);
-        tabs.addTab("Graphics", renderGraphicsEditorPanel);
-        tabs.addTab("Story", storySystemsEditorPanel);
-        tabs.addTab("Zones", buildZonesPanel());
-        tabs.addTab("Data", buildDataPanel());
+        tabs.addTab("Overview", buildOverviewPanel());
+        tabs.addTab("IDE", buildIdePanel());
+        tabs.addTab("Engine", buildEnginePanel());
+        tabs.addTab("Framework", buildFrameworkPanel());
+        tabs.addTab("Systems", buildSystemsPanel());
+        tabs.addTab("Map Design", buildMapDesignPanel());
+        tabs.addTab("Pixels", pixelEditorPanel);
+        tabs.addTab("Audio", audioEditorPanel);
+        tabs.addTab("Graphics", renderGraphicsEditorPanel);
+        tabs.addTab("Story", storySystemsEditorPanel);
+        tabs.addTab("Zones", buildZonesPanel());
+        tabs.addTab("Data", buildDataPanel());
+        // New panels (Unreal-like editor windows)
+        tabs.addTab("Content", new ContentBrowserPanel());
+        tabs.addTab("Outliner", new WorldOutlinerPanel());
+        tabs.addTab("Inspector", new InspectorPanelStub());
+        tabs.addTab("Palette", tilePalettePanel);
+        tabs.addTab("Animation", new AnimationPanel());
+        tabs.addTab("Visual Scripting", new VisualScriptingPanelStub());
+        tabs.addTab("Console", new ConsolePanel());
+        tabs.addTab("Profiler", new ProfilerPanel());
+        tabs.addTab("Audio Mixer", new AudioMixerPanel());
+        tabs.addTab("Version Control", new VersionControlPanel());
+        tabs.addTab("Docs", new DocsViewerPanel());
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
        if (mapId != null && !mapId.isBlank()) {
            mapCanvasPanel.loadMap(mapId);
            GameAPI.setCurrentMapId(mapId);
        }
        refreshMapDesignInfo();
    }

    private JTextArea createTextArea() {
        JTextArea area = new JTextArea();
        area.setFont(new Font("Monospaced", Font.PLAIN, 12));
        area.setEditable(false);
        return area;
    }

    private void refreshOverviewPanel() {
        overviewArea.setText(GameEditorRuntimeAPI.getOverviewInfo());
    }

    private void refreshEnginePanel() {
        engineArea.setText(GameEditorRuntimeAPI.getEngineInfo());
    }

    private void refreshFrameworkPanel() {
        frameworkArea.setText(GameEditorRuntimeAPI.getFrameworkInfo());
    }

    private void refreshFrameworkLogPanel() {
        frameworkLogArea.setText(GameEditorRuntimeAPI.getFrameworkLog());
    }

    private void refreshZonesPanel() {
        zonesArea.setText(GameEditorRuntimeAPI.getZonesInfo());
    }

    private void refreshDataPanel() {
        dataArea.setText(GameEditorRuntimeAPI.getDataFilesInfo());
    }

    private void refreshSystemsPanel() {
        systemsArea.setText(GameEditorRuntimeAPI.getSystemsInfo());
    }

    private void refreshMapDesignInfo() {
        mapDesignInfoArea.setText(mapCanvasPanel.getMapDesignInfo());
    }

    private void refreshAllPanels() {
        refreshOverviewPanel();
        refreshEnginePanel();
        refreshFrameworkPanel();
        refreshFrameworkLogPanel();
        refreshZonesPanel();
        refreshDataPanel();
        refreshSystemsPanel();
-        mapCanvasPanel.refresh();
+        mapCanvasPanel.repaint();
     }
+
+    // Build a compact Unreal-like toolbar for common editor actions
+    private JToolBar buildToolBar() {
+        JToolBar tb = new JToolBar();
+        tb.setFloatable(false);
+
+        JButton save = new JButton("Save");
+        save.addActionListener(e -> {
+            // Use buildProject as a proxy for Save/Package in this skeleton
+            String out = GameEditorRuntimeAPI.buildProject();
+            JOptionPane.showMessageDialog(this, "Build result:\n" + out, "Save/Build", JOptionPane.INFORMATION_MESSAGE);
+        });
+        tb.add(save);
+
+        JButton undoBtn = new JButton("Undo");
+        undoBtn.addActionListener(e -> {
+            try { mapCanvasPanel.undo(); } catch (Exception ex) { ex.printStackTrace(); }
+        });
+        tb.add(undoBtn);
+
+        JButton redoBtn = new JButton("Redo");
+        redoBtn.addActionListener(e -> {
+            try { mapCanvasPanel.redo(); } catch (Exception ex) { ex.printStackTrace(); }
+        });
+        tb.add(redoBtn);
+
+        tb.addSeparator();
+
+        JButton play = new JButton("Play");
+        play.addActionListener(e -> {
+            // Run the project in editor (Play In Editor)
+            GameEditorRuntimeAPI.runProject();
+        });
+        tb.add(play);
+
+        JButton simulate = new JButton("Simulate");
+        simulate.addActionListener(e -> JOptionPane.showMessageDialog(this, "Simulate (placeholder)"));
+        tb.add(simulate);
+
+        tb.addSeparator();
+
+        JToggleButton gridBtn = new JToggleButton("Grid", true);
+        gridBtn.addActionListener(e -> mapCanvasPanel.setShowGrid(gridBtn.isSelected()));
+        tb.add(gridBtn);
+
+        JToggleButton snapBtn = new JToggleButton("Snap", false);
+        snapBtn.addActionListener(e -> {
+            // For now just show status; future: wire snap to tools
+            JOptionPane.showMessageDialog(this, "Snap: " + snapBtn.isSelected());
+        });
+        tb.add(snapBtn);
+
+        tb.addSeparator();
+
+        tb.add(new JLabel("Layout:"));
+        JComboBox<String> layout = new JComboBox<>(new String[]{"Default", "2-Column", "Large-Preview"});
+        layout.addActionListener(e -> JOptionPane.showMessageDialog(this, "Switch layout: " + layout.getSelectedItem()));
+        tb.add(layout);
+
+        tb.addSeparator();
+
+        tb.add(new JLabel(" Search:"));
+        JTextField search = new JTextField();
+        search.setColumns(12);
+        search.addActionListener(e -> JOptionPane.showMessageDialog(this, "Search: " + search.getText()));
+        tb.add(search);
+
+        return tb;
+    }
 }