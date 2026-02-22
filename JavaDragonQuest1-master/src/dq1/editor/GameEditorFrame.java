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
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
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
        tabs.addTab("Engine", buildEnginePanel());
        tabs.addTab("Framework", buildFrameworkPanel());
        tabs.addTab("Zones", buildZonesPanel());
        tabs.addTab("Data", buildDataPanel());
        return tabs;
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

    private static JTextArea createTextArea() {
        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        area.setLineWrap(false);
        return area;
    }

    private void refreshAllPanels() {
        refreshOverviewPanel();
        refreshEnginePanel();
        refreshFrameworkPanel();
        refreshFrameworkLogPanel();
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
}
