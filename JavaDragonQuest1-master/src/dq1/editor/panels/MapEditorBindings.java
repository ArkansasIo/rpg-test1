package dq1.editor.panels;

import dq1.editor.MapEditorCanvasPanel;
import dq1.editor.audio.EditorAudioAPI;

public class MapEditorBindings {
    // These methods are simple static bridges wired by GameEditorFrame at startup
    public static MapEditorCanvasPanel mapCanvas;

    public static void setTileOnMap(int row, int col, int tileId) {
        if (mapCanvas != null) {
            mapCanvas.beginStrokePublic();
            mapCanvas.changeCellPublic(row, col, tileId);
            mapCanvas.commitStrokePublic();
            mapCanvas.repaint();
        }
    }

    public static void setAudioOnMap(int row, int col, String fileName) {
        if (mapCanvas != null) {
            mapCanvas.setAudioAttachment(row, col, fileName);
            mapCanvas.repaint();
        }
    }

    public static void playAudioAtCell(int row, int col) {
        if (mapCanvas != null) {
            String attached = mapCanvas.getAudioAttachment(row, col);
            if (attached != null) EditorAudioAPI.playFileByName(attached);
        }
    }
}