package dq1.editor;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 * Small Editor API to open auxiliary editor windows (dock, standalone map editor).
 * This keeps the original in-editor panels intact while allowing external windows
 * like a dock or standalone map editor to be opened, similar to Unreal-style tools.
 */
public class EditorAPI {

    private static EditorDockFrame dockFrame;
    private static JFrame mapFrame;

    public static void showDock() {
        SwingUtilities.invokeLater(() -> {
            try {
                if (dockFrame == null) {
                    dockFrame = new EditorDockFrame();
                }
                dockFrame.showDock();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    public static void showMapEditorStandalone() {
        SwingUtilities.invokeLater(() -> {
            try {
                // create a fresh MapEditorCanvasPanel instance so we don't re-parent
                // the in-editor instance; this is safer and behaves like a separate tool window
                MapEditorCanvasPanel mapPanel = new MapEditorCanvasPanel();
                if (mapFrame != null) {
                    mapFrame.setVisible(false);
                    mapFrame.dispose();
                }
                mapFrame = new JFrame("Standalone Map Editor");
                mapFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                mapFrame.getContentPane().add(mapPanel);
                mapFrame.pack();
                mapFrame.setSize(1000, 700);
                mapFrame.setLocationRelativeTo(null);
                mapFrame.setVisible(true);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

}
