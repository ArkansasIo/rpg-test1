package dq1.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Unified runtime/editor API facade for tooling and launch integration.
 */
public final class GameAPI {

    private GameAPI() { }

    public static String getGameTitle() {
        return Settings.GAME_TITLE;
    }

    public static String getGameVersion() {
        return Settings.GAME_VERSION;
    }

    public static String getDisplayTitle() {
        return Settings.GAME_TITLE + " (" + Settings.GAME_VERSION + ")";
    }

    public static String getCurrentState() {
        return Game.getState().name();
    }

    public static String getCurrentMapId() {
        Object mapId = Script.getGlobalValue("$$current_map_id");
        return mapId == null ? "" : mapId.toString();
    }

    public static List<String> getEngineSummaryLines() {
        return GameEngineService.buildEngineSummaryLines();
    }

    public static List<String> getFrameworkRuntimeLines() {
        return GameEngineService.buildFrameworkRuntimeLines();
    }

    public static List<String> getFrameworkLogLines(int max) {
        return GameEngineService.buildFrameworkLogLines(max);
    }

    public static void applyDisplayPreset(String preset) {
        GameEngineService.applyDisplayPreset(preset);
    }

    public static void launch() throws Exception {
        Game.start();
    }

    public static void launch(String[] args) throws Exception {
        Game.main(args == null ? new String[0] : args);
    }

    // Unified content API (replaces separate editor API surface)
    public static List<WoWZoneSystem.WoWZone> getZones() {
        return WoWZoneSystem.zones;
    }

    public static void addZone(String name, String biome, String[] subZones, String biomeTitle) {
        int nextZoneId = 1;
        for (WoWZoneSystem.WoWZone z : WoWZoneSystem.zones) {
            if (z.id >= nextZoneId) {
                nextZoneId = z.id + 1;
            }
        }
        WoWZoneSystem.addZone(nextZoneId, name, "Custom", biome, biomeTitle
                , "None", 1, 1, 1.0, 1.0, "Custom added zone");
        int subId = 1;
        for (WoWZoneSystem.WoWZone z : WoWZoneSystem.zones) {
            for (WoWZoneSystem.WoWSubZone s : z.subZones) {
                if (s.id >= subId) {
                    subId = s.id + 1;
                }
            }
        }
        if (subZones != null) {
            for (String sub : subZones) {
                if (sub != null && !sub.trim().isEmpty()) {
                    WoWZoneSystem.addSubZone(subId++, nextZoneId, sub.trim(), "Custom sub-zone");
                }
            }
        }
    }

    public static List<StorySystem.Act> getActs() {
        StorySystem story = new StorySystem();
        List<StorySystem.Act> result = new ArrayList<>();
        for (int i = 1; i <= StorySystem.ACT_COUNT; i++) {
            StorySystem.Act act = story.getAct(i);
            if (act != null) {
                result.add(act);
            }
        }
        return result;
    }

    public static void addQuest(int actNum, int chapterNum, String name, String desc) {
        StorySystem story = new StorySystem();
        StorySystem.Act act = story.getAct(actNum);
        if (act != null) {
            StorySystem.Chapter chapter = act.getChapter(chapterNum);
            if (chapter != null) {
                chapter.addQuest(name, desc);
            }
        }
    }

    public static List<Item> getItems() {
        return new ArrayList<>(Resource.getITEMS().values());
    }

    public static List<Spell> getSpells() {
        return new ArrayList<>(Resource.getSPELLS().values());
    }

    public static List<Boss> getBosses() {
        return Collections.emptyList();
    }

    public static void openEditor() {
        dq1.editor.GameEditorFrame.showEditor();
    }
}
