package dq1.editor;

import dq1.core.GameAPI;
import java.util.List;

/**
 * Runtime editor API bridge used by in-game IDE and Swing editor.
 */
public final class GameEditorRuntimeAPI {

    private GameEditorRuntimeAPI() { }

    public static List<String> compileProject() {
        return GameAPI.runBuildTarget("compile");
    }

    public static List<String> buildProject() {
        return GameAPI.runBuildTarget("jar");
    }

    public static List<String> runProject() {
        return GameAPI.runBuildTarget("run");
    }

    public static List<String> listMapIds() {
        return GameAPI.getMapIds();
    }

    public static List<String> mapSummary(String mapId) {
        return GameAPI.getMapSummary(mapId);
    }

    public static boolean setCurrentMapTile(int row, int col, int tileId) {
        return GameAPI.setCurrentMapTile(row, col, tileId);
    }

    public static int getCurrentMapTileId(int row, int col) {
        return GameAPI.getCurrentMapTileId(row, col);
    }

    public static String exportMapToCsv(String mapId, String outputPath) {
        return GameAPI.exportMapToCsv(mapId, outputPath);
    }

    public static List<String> listSystemEditors() {
        return GameAPI.listSystemEditorModules();
    }
}
