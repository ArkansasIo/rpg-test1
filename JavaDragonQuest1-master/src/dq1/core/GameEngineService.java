package dq1.core;

import java.util.ArrayList;
import java.util.List;
import mmorpg.framework.spec.CombatFrameworkModule;

/**
 * Engine-level diagnostics and configuration facade.
 */
public final class GameEngineService {

    private GameEngineService() { }

    public static List<String> buildEngineSummaryLines() {
        List<String> lines = new ArrayList<>();
        lines.add("Title: " + Settings.GAME_TITLE);
        lines.add("Version: " + Settings.GAME_VERSION);
        lines.add("Display API: " + Settings.DISPLAY_API);
        lines.add("Resolution: " + Settings.screenWidth + "x" + Settings.screenHeight);
        lines.add("Fullscreen: " + (Settings.fullscreen ? "ON" : "OFF"));
        lines.add("HDR: " + (Settings.HDR_ENABLED ? "ON" : "OFF"));
        lines.add("Mouse: " + (Settings.MOUSE_ENABLED ? "ON" : "OFF"));
        lines.add("Skip Story: " + (Settings.SKIP_INTRO_STORY ? "ON" : "OFF"));
        lines.add("WoW Map Overlay: " + (Settings.SHOW_WOW_WORLD_MAP_OVERLAY ? "ON" : "OFF"));
        lines.add("State: " + Game.getState().name());
        Object mapId = Script.getGlobalValue("$$current_map_id");
        lines.add("Map ID: " + (mapId == null ? "(none)" : mapId.toString()));
        return lines;
    }

    public static void applyDisplayPreset(String preset) {
        if (preset == null) {
            return;
        }
        switch (preset.trim().toUpperCase()) {
            case "HD":
            case "1366X768":
                Settings.screenWidth = 1366;
                Settings.screenHeight = 768;
                break;
            case "720P":
            case "1280X720":
                Settings.screenWidth = 1280;
                Settings.screenHeight = 720;
                break;
            case "1080P":
            case "1920X1080":
                Settings.screenWidth = 1920;
                Settings.screenHeight = 1080;
                break;
            case "4K":
            case "3840X2160":
                Settings.screenWidth = 3840;
                Settings.screenHeight = 2160;
                break;
            default:
                break;
        }
    }

    public static List<String> buildFrameworkRuntimeLines() {
        return CombatFrameworkModule.runDemoTick();
    }

    public static List<String> buildFrameworkLogLines(int max) {
        return CombatFrameworkModule.tailGameLogLines(max);
    }
}
