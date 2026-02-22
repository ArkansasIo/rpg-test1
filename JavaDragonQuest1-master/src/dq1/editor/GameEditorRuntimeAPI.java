package dq1.editor;

import dq1.core.GameAPI;
import java.util.List;
import java.util.stream.Collectors;

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

    public static boolean setMapTile(String mapId, int row, int col, int tileId) {
        return GameAPI.setMapTile(mapId, row, col, tileId);
    }

    public static int getCurrentMapTileId(int row, int col) {
        return GameAPI.getCurrentMapTileId(row, col);
    }

    public static int getMapTileId(String mapId, int row, int col) {
        return GameAPI.getMapTileId(mapId, row, col);
    }

    public static List<Integer> getMapTileIds(String mapId) {
        return GameAPI.getMapTileIds(mapId);
    }

    public static String exportMapToCsv(String mapId, String outputPath) {
        return GameAPI.exportMapToCsv(mapId, outputPath);
    }

    public static List<String> listSystemEditors() {
        return GameAPI.listSystemEditorModules();
    }

    // Convenience string-based APIs used by the Swing editor UI
    public static String getOverviewInfo() {
        List<String> lines = GameAPI.getEngineSummaryLines();
        return String.join(System.lineSeparator(), lines);
    }

    public static String getEngineInfo() {
        List<String> lines = GameAPI.getEngineSummaryLines();
        return String.join(System.lineSeparator(), lines);
    }

    public static String getFrameworkInfo() {
        List<String> lines = GameAPI.getFrameworkRuntimeLines();
        return String.join(System.lineSeparator(), lines);
    }

    public static String getFrameworkLog() {
        List<String> lines = GameAPI.getFrameworkLogLines(200);
        return String.join(System.lineSeparator(), lines);
    }

    public static String getZonesInfo() {
        try {
            List<dq1.core.WoWZoneSystem.WoWZone> zones = GameAPI.getZones();
            return zones.stream().map(z -> z.id + ": " + z.name + " (" + z.subZones.size() + " sub)")
                    .collect(Collectors.joining(System.lineSeparator()));
        } catch (Exception e) {
            return "Zones info not available.";
        }
    }

    public static String getDataFilesInfo() {
        // No direct GameAPI helper; provide a lightweight placeholder
        return "Data files info not available in this build.";
    }

    public static String getSystemsInfo() {
        List<String> lines = GameAPI.listSystemEditorModules();
        return String.join(System.lineSeparator(), lines);
    }
}