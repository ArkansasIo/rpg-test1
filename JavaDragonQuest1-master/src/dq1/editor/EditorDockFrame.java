package dq1.editor;

import dq1.editor.audio.AudioToolsPanel;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.io.File;

public class EditorDockFrame extends JFrame {

    private AssetBrowserPanel browser;
    private InspectorPanel inspector;

    public EditorDockFrame() {
        super("Editor Dock");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        browser = new AssetBrowserPanel();
        inspector = new InspectorPanel();

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, browser, inspector);
        split.setDividerLocation(300);
        add(split, BorderLayout.CENTER);

        // Add a small tools panel (audio tools) docked at bottom
        add(new AudioToolsPanel(), BorderLayout.SOUTH);

        browser.addSelectionListener(e -> onBrowserSelection(e));

        pack();
        setSize(900, 700);
        setLocationRelativeTo(null);
    }

    private void onBrowserSelection(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            File f = browser.getSelected();
            inspector.inspect(f);
        }
    }

    public void showDock() {
        setVisible(true);
    }
}
