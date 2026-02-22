package dq1.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import mmorpg.game.FeatureRegistry;

/**
 * Unified runtime/editor API facade for tooling and launch integration.
 */
public final class GameAPI {

    private static final StorySystem STORY_EDITOR_MODEL = new StorySystem();

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

    public static List<String> getMapIds() {
        List<String> ids = new ArrayList<>();
        for (Map.Entry<String, TileMap> entry : Resource.getTILE_MAPS().entrySet()) {
            ids.add(entry.getKey());
        }
        Collections.sort(ids);
        return ids;
    }

    public static List<String> getMapSummary(String mapId) {
        List<String> lines = new ArrayList<>();
        try {
            TileMap map = Resource.getTileMap(mapId);
            if (map == null) {
                lines.add("Map not found: " + mapId);
                return lines;
            }
            lines.add("Map ID: " + map.getId());
            lines.add("Name: " + map.getName());
            lines.add("Size: " + map.getCols() + "x" + map.getRows());
            lines.add("Music: " + map.getMusicId());
            lines.add("Dark: " + map.isDark());
            lines.add("Encounters: " + map.isEnemiesEncounterEnabled());
        }
        catch (Exception e) {
            lines.add("Map error: " + e.getMessage());
        }
        return lines;
    }

    public static boolean setCurrentMapTile(int row, int col, int tileId) {
        TileMap map = Game.getCurrentMap();
        if (map == null) {
            return false;
        }
        map.setTile(row, col, tileId);
        return true;
    }

    public static boolean setMapTile(String mapId, int row, int col, int tileId) {
        try {
            TileMap map = Resource.getTileMap(mapId);
            if (map == null) {
                return false;
            }
            map.setTile(row, col, tileId);
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }

    public static int getCurrentMapTileId(int row, int col) {
        TileMap map = Game.getCurrentMap();
        if (map == null) {
            return -1;
        }
        Tile tile = map.getTile(row, col);
        return tile == null ? -1 : tile.getId();
    }

    public static int getMapTileId(String mapId, int row, int col) {
        try {
            TileMap map = Resource.getTileMap(mapId);
            if (map == null) {
                return -1;
            }
            Tile tile = map.getTile(row, col);
            return tile == null ? -1 : tile.getId();
        }
        catch (Exception e) {
            return -1;
        }
    }

    public static List<Integer> getMapTileIds(String mapId) {
        try {
            TileMap map = Resource.getTileMap(mapId);
            if (map == null) {
                return Collections.emptyList();
            }
            List<Integer> ids = new ArrayList<>(map.getTileSet().keySet());
            ids.sort(Comparator.naturalOrder());
            return ids;
        }
        catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public static String exportMapToCsv(String mapId, String outputRelativePath) {
        try {
            TileMap map = Resource.getTileMap(mapId);
            if (map == null) {
                return "Map not found.";
            }
            Path output = Path.of(outputRelativePath);
            if (output.getParent() != null) {
                Files.createDirectories(output.getParent());
            }
            List<String> lines = new ArrayList<>();
            lines.add("map_id," + map.getId());
            lines.add("map_name," + map.getName());
            lines.add("cols," + map.getCols());
            lines.add("rows," + map.getRows());
            lines.add("tile_data");
            for (int row = 0; row < map.getRows(); row++) {
                StringBuilder sb = new StringBuilder();
                for (int col = 0; col < map.getCols(); col++) {
                    if (col > 0) {
                        sb.append(',');
                    }
                    Tile tile = map.getTile(row, col);
                    sb.append(tile == null ? -1 : tile.getId());
                }
                lines.add(sb.toString());
            }
            Files.write(output, lines, StandardCharsets.UTF_8);
            return "Exported: " + output.toAbsolutePath();
        }
        catch (Exception e) {
            return "Export error: " + e.getMessage();
        }
    }

    public static List<String> listSystemEditorModules() {
        return List.of(
                "Map Editor",
                "Zone/Biome Editor",
                "Quest/Story Editor",
                "Items/Spells Editor",
                "Combat Framework Editor",
                "Display/Engine Settings Editor");
    }

    public static List<String> getFeatureMatrixLines() {
        List<String> lines = new ArrayList<>();
        for (FeatureRegistry.FeatureEntry feature : FeatureRegistry.getAll()) {
            lines.add(feature.getStatus().name() + " | "
                    + feature.getName() + " | " + feature.getArea());
        }
        return lines;
    }

    public static List<String> runBuildTarget(String target) {
        List<String> lines = new ArrayList<>();
        try {
            String antCmd = "apache-ant-1.10.14\\bin\\ant.bat";
            File rootA = new File(".");
            File rootB = new File("JavaDragonQuest1-master");
            File workDir = new File(rootA, "build.xml").exists() ? rootA : rootB;
            File ant = new File(workDir, antCmd);
            if (!ant.exists()) {
                ant = new File("JavaDragonQuest1-master\\" + antCmd);
            }
            if (!ant.exists()) {
                lines.add("Ant not found.");
                return lines;
            }
            String buildFile = new File(workDir, "build.xml").getAbsolutePath();
            ProcessBuilder pb = new ProcessBuilder(
                    ant.getAbsolutePath(), "-f", buildFile, target);
            pb.directory(workDir);
            pb.redirectErrorStream(true);
            Process p = pb.start();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(
                    p.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                int count = 0;
                while ((line = br.readLine()) != null) {
                    if (line.trim().isEmpty()) {
                        continue;
                    }
                    if (count < 80) {
                        lines.add(line);
                    }
                    count++;
                }
            }
            int exit = p.waitFor();
            lines.add("Exit code: " + exit);
        }
        catch (Exception e) {
            lines.add("Build error: " + e.getMessage());
        }
        return lines;
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
        List<StorySystem.Act> result = new ArrayList<>();
        for (int i = 1; i <= StorySystem.ACT_COUNT; i++) {
            StorySystem.Act act = STORY_EDITOR_MODEL.getAct(i);
            if (act != null) {
                result.add(act);
            }
        }
        return result;
    }

    public static void addQuest(int actNum, int chapterNum, String name, String desc) {
        StorySystem.Act act = STORY_EDITOR_MODEL.getAct(actNum);
        if (act != null) {
            StorySystem.Chapter chapter = act.getChapter(chapterNum);
            if (chapter != null) {
                chapter.addQuest(name, desc);
            }
        }
    }

    public static void addSideQuest(int actNum, int chapterNum, String name, String desc) {
        StorySystem.Act act = STORY_EDITOR_MODEL.getAct(actNum);
        if (act != null) {
            StorySystem.Chapter chapter = act.getChapter(chapterNum);
            if (chapter != null) {
                chapter.addSideQuest(name, desc);
            }
        }
    }

    public static List<String> getStorySummaryLines() {
        List<String> lines = new ArrayList<>();
        lines.add("Acts: " + StorySystem.ACT_COUNT + " | Chapters/Act: " + StorySystem.CHAPTERS_PER_ACT);
        for (int i = 1; i <= StorySystem.ACT_COUNT; i++) {
            StorySystem.Act act = STORY_EDITOR_MODEL.getAct(i);
            int mainCount = 0;
            int sideCount = 0;
            if (act != null) {
                for (StorySystem.Chapter chapter : act.chapters) {
                    mainCount += chapter.quests.size();
                    sideCount += chapter.sideQuests.size();
                }
            }
            lines.add("Act " + i + " -> Main Quests: " + mainCount + " | Side Quests: " + sideCount);
        }
        return lines;
    }

    public static List<String> getAudioTrackIds() {
        List<String> ids = new ArrayList<>();
        try {
            File dir = new File("assets/res/audio");
            if (!dir.exists()) {
                dir = new File("JavaDragonQuest1-master/assets/res/audio");
            }
            File[] files = dir.listFiles((d, name) -> name.toLowerCase().endsWith(".mid"));
            if (files != null) {
                for (File file : files) {
                    String n = file.getName();
                    int i = n.lastIndexOf('.');
                    ids.add(i > 0 ? n.substring(0, i) : n);
                }
            }
            Collections.sort(ids);
        }
        catch (Exception ignored) {
        }
        return ids;
    }

    public static String setAudioVolumes(int music, int sound) {
        try {
            int mv = Math.max(0, Math.min(9, music));
            int sv = Math.max(0, Math.min(9, sound));
            Audio.setMusicVolume(mv);
            Audio.setSoundVolume(sv);
            return "Applied volumes -> music=" + mv + ", sound=" + sv;
        }
        catch (Exception e) {
            return "Audio volume error: " + e.getMessage();
        }
    }

    public static String previewMusic(String musicId) {
        try {
            if (musicId == null || musicId.isBlank()) {
                return "Music id is empty.";
            }
            Audio.playMusic(musicId.trim());
            return "Playing music: " + musicId.trim();
        }
        catch (Exception e) {
            return "Play music error: " + e.getMessage();
        }
    }

    public static String pauseMusic() {
        try {
            Audio.pauseMusic();
            return "Music paused.";
        }
        catch (Exception e) {
            return "Pause music error: " + e.getMessage();
        }
    }

    public static String stopMusic() {
        try {
            Audio.stopMusic();
            return "Music stopped.";
        }
        catch (Exception e) {
            return "Stop music error: " + e.getMessage();
        }
    }

    public static String previewSoundEffect(int soundId) {
        try {
            Audio.playSound(soundId);
            return "Played sound effect id: " + soundId;
        }
        catch (Exception e) {
            return "Play SFX error: " + e.getMessage();
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
