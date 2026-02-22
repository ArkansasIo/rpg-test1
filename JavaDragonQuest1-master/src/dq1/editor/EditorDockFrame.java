package dq1.editor;

import dq1.editor.audio.AudioToolsPanel;
import dq1.editor.audio.EditorAudioAPI;
import dq1.editor.audio.AudioPlaybackUtil;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.io.File;

public class EditorDockFrame extends JFrame {

    private AssetBrowserPanel browser;
    private InspectorPanel inspector;
    private MapEditorCanvasPanel mapCanvas;

    public EditorDockFrame() {
        super("Editor Dock");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        browser = new AssetBrowserPanel();
        inspector = new InspectorPanel();
        mapCanvas = new MapEditorCanvasPanel();

        JSplitPane rightSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, mapCanvas, inspector);
        rightSplit.setDividerLocation(520);

        JSplitPane mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, browser, rightSplit);
        mainSplit.setDividerLocation(300);
        add(mainSplit, BorderLayout.CENTER);

        // Add a small tools panel (audio tools) docked at bottom
        add(new AudioToolsPanel(), BorderLayout.SOUTH);

        browser.addSelectionListener(e -> onBrowserSelection(e));

        // wire map cell selection to inspector to show audio attachment
        mapCanvas.setCellSelectionListener((row, col) -> {
            String attached = mapCanvas.getAudioAttachment(row, col);
            if (attached != null) {
                File f = EditorAudioAPI.getAudioFile(attached);
                if (f != null) {
                    inspector.inspect(f);
                } else {
                    inspector.inspect(null);
                }
            } else {
                inspector.inspect(null);
            }
        });

        pack();
        setSize(1200, 800);
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