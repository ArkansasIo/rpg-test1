package dq1.editor;

import dq1.core.*;
import java.util.List;

/**
 * Compatibility wrapper. Use {@link dq1.core.GameAPI} as the unified API.
 */
@Deprecated
public class GameEditorAPI {

    public List<WoWZoneSystem.WoWZone> getZones() {
        return GameAPI.getZones();
    }

    public void addZone(String name, String biome, String[] subZones, String biomeTitle) {
        GameAPI.addZone(name, biome, subZones, biomeTitle);
    }

    public List<StorySystem.Act> getActs() {
        return GameAPI.getActs();
    }

    public void addQuest(int actNum, int chapterNum, String name, String desc) {
        GameAPI.addQuest(actNum, chapterNum, name, desc);
    }

    public List<Item> getItems() {
        return GameAPI.getItems();
    }

    public List<Spell> getSpells() {
        return GameAPI.getSpells();
    }

    public List<Boss> getBosses() {
        return GameAPI.getBosses();
    }

    public List<String> getEngineSummaryLines() {
        return GameAPI.getEngineSummaryLines();
    }

    public List<String> getFrameworkRuntimeLines() {
        return GameAPI.getFrameworkRuntimeLines();
    }

    public List<String> getFrameworkLogLines(int max) {
        return GameAPI.getFrameworkLogLines(max);
    }

    public void applyDisplayPreset(String preset) {
        GameAPI.applyDisplayPreset(preset);
    }

    public List<String> compileProject() {
        return GameEditorRuntimeAPI.compileProject();
    }

    public List<String> buildProject() {
        return GameEditorRuntimeAPI.buildProject();
    }

    public List<String> runProject() {
        return GameEditorRuntimeAPI.runProject();
    }

    public List<String> listMapIds() {
        return GameEditorRuntimeAPI.listMapIds();
    }

    public List<String> mapSummary(String mapId) {
        return GameEditorRuntimeAPI.mapSummary(mapId);
    }

    public boolean setCurrentMapTile(int row, int col, int tileId) {
        return GameEditorRuntimeAPI.setCurrentMapTile(row, col, tileId);
    }

    public int getCurrentMapTileId(int row, int col) {
        return GameEditorRuntimeAPI.getCurrentMapTileId(row, col);
    }

    public String exportMapToCsv(String mapId, String outputPath) {
        return GameEditorRuntimeAPI.exportMapToCsv(mapId, outputPath);
    }

    public List<String> listSystemEditors() {
        return GameEditorRuntimeAPI.listSystemEditors();
    }
}
