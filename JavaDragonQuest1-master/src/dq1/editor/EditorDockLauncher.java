package dq1.editor;

import javax.swing.SwingUtilities;

public class EditorDockLauncher {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            EditorDockFrame dock = new EditorDockFrame();
            dock.showDock();
        });
    }
}
