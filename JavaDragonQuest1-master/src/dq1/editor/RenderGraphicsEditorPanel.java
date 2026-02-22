package dq1.editor;

import dq1.core.GameAPI;
import dq1.core.Settings;
import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * Rendering and graphics tuning panel.
 */
public class RenderGraphicsEditorPanel extends JPanel {
    private final JTextArea output = new JTextArea();
    private final JComboBox<String> displayApi = new JComboBox<>(new String[] { "AUTO", "OPENGL", "DIRECTX", "SOFTWARE" });
    private final JComboBox<String> resolutionPreset = new JComboBox<>(new String[] { "HD", "720p", "1080p", "4K" });
    private final JCheckBox fullscreen = new JCheckBox("Fullscreen");
    private final JCheckBox hdr = new JCheckBox("HDR");
    private final JCheckBox mouse = new JCheckBox("Mouse");
    private final JCheckBox wowOverlay = new JCheckBox("WoW Map Overlay");
    private final JCheckBox skipIntroStory = new JCheckBox("Skip Intro Story");

    public RenderGraphicsEditorPanel() {
        super(new BorderLayout(8, 8));
        output.setEditable(false);
        add(buildTopBar(), BorderLayout.NORTH);
        add(new JScrollPane(output), BorderLayout.CENTER);
        pullFromSettings();
        refreshSummary();
    }

    private JPanel buildTopBar() {
        JPanel top = new JPanel();
        top.add(new JLabel("Display API:"));
        top.add(displayApi);
        top.add(new JLabel("Preset:"));
        top.add(resolutionPreset);
        top.add(fullscreen);
        top.add(hdr);
        top.add(mouse);
        top.add(wowOverlay);
        top.add(skipIntroStory);

        JButton apply = new JButton("Apply");
        apply.addActionListener(e -> {
            Settings.DISPLAY_API = (String) displayApi.getSelectedItem();
            GameAPI.applyDisplayPreset((String) resolutionPreset.getSelectedItem());
            Settings.fullscreen = fullscreen.isSelected();
            Settings.HDR_ENABLED = hdr.isSelected();
            Settings.MOUSE_ENABLED = mouse.isSelected();
            Settings.SHOW_WOW_WORLD_MAP_OVERLAY = wowOverlay.isSelected();
            Settings.SKIP_INTRO_STORY = skipIntroStory.isSelected();
            refreshSummary();
        });
        top.add(apply);

        JButton reload = new JButton("Reload");
        reload.addActionListener(e -> {
            pullFromSettings();
            refreshSummary();
        });
        top.add(reload);

        return top;
    }

    private void pullFromSettings() {
        displayApi.setSelectedItem(Settings.DISPLAY_API);
        fullscreen.setSelected(Settings.fullscreen);
        hdr.setSelected(Settings.HDR_ENABLED);
        mouse.setSelected(Settings.MOUSE_ENABLED);
        wowOverlay.setSelected(Settings.SHOW_WOW_WORLD_MAP_OVERLAY);
        skipIntroStory.setSelected(Settings.SKIP_INTRO_STORY);
    }

    private void refreshSummary() {
        List<String> lines = new ArrayList<>();
        lines.add("Render / Graphics Editor");
        lines.addAll(GameAPI.getEngineSummaryLines());
        output.setText(String.join(System.lineSeparator(), lines));
    }
}
