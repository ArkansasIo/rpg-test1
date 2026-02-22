package mmorpg.world;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Catalogs all game map files with WoW-style map metadata and per-tile details.
 */
public class WoWTileMapSystem {

    public static final class TileRecord {
        private final int tileNumber;
        private final int row;
        private final int col;
        private final int tileId;

        public TileRecord(int tileNumber, int row, int col, int tileId) {
            this.tileNumber = tileNumber;
            this.row = row;
            this.col = col;
            this.tileId = tileId;
        }

        public int getTileNumber() {
            return tileNumber;
        }

        public int getRow() {
            return row;
        }

        public int getCol() {
            return col;
        }

        public int getTileId() {
            return tileId;
        }
    }

    public static final class MapRecord {
        private final int mapId;
        private final String mapFileId;
        private final String mapTitle;
        private final String biome;
        private final int rows;
        private final int cols;
        private final List<TileRecord> tiles;
        private final Map<Integer, Integer> tileIdCounts;

        public MapRecord(
                int mapId,
                String mapFileId,
                String mapTitle,
                String biome,
                int rows,
                int cols,
                List<TileRecord> tiles,
                Map<Integer, Integer> tileIdCounts) {

            this.mapId = mapId;
            this.mapFileId = mapFileId;
            this.mapTitle = mapTitle;
            this.biome = biome;
            this.rows = rows;
            this.cols = cols;
            this.tiles = tiles;
            this.tileIdCounts = tileIdCounts;
        }

        public int getMapId() {
            return mapId;
        }

        public String getMapFileId() {
            return mapFileId;
        }

        public String getMapTitle() {
            return mapTitle;
        }

        public String getBiome() {
            return biome;
        }

        public int getRows() {
            return rows;
        }

        public int getCols() {
            return cols;
        }

        public int getTileCount() {
            return tiles.size();
        }

        public List<TileRecord> getTiles() {
            return Collections.unmodifiableList(tiles);
        }

        public Map<Integer, Integer> getTileIdCounts() {
            return Collections.unmodifiableMap(tileIdCounts);
        }
    }

    private final List<MapRecord> maps = new ArrayList<>();
    private final Map<Integer, MapRecord> mapIdIndex = new HashMap<>();
    private final Map<String, MapRecord> mapFileIdIndex = new HashMap<>();

    public List<MapRecord> getMaps() {
        return Collections.unmodifiableList(maps);
    }

    public MapRecord getMapById(int mapId) {
        return mapIdIndex.get(mapId);
    }

    public MapRecord getMapByFileId(String mapFileId) {
        return mapFileIdIndex.get(mapFileId);
    }

    public static WoWTileMapSystem loadFromGameAssets(Path mapsDirectory) throws IOException {
        WoWTileMapSystem system = new WoWTileMapSystem();
        List<Path> mapFiles = new ArrayList<>();
        try (var stream = Files.list(mapsDirectory)) {
            stream.filter(p -> p.getFileName().toString().toLowerCase().endsWith(".map"))
                    .sorted()
                    .forEach(mapFiles::add);
        }

        int nextMapId = 1;
        for (Path mapFile : mapFiles) {
            MapRecord record = parseMapFile(nextMapId++, mapFile);
            system.maps.add(record);
            system.mapIdIndex.put(record.getMapId(), record);
            system.mapFileIdIndex.put(record.getMapFileId(), record);
        }
        return system;
    }

    private static MapRecord parseMapFile(int mapId, Path mapFile) throws IOException {
        String fileName = mapFile.getFileName().toString();
        String mapFileId = fileName.substring(0, fileName.length() - 4);
        String mapTitle = mapFileId;
        int rows = 0;
        int cols = 0;
        boolean readingMapData = false;
        List<String> dataLines = new ArrayList<>();

        try (BufferedReader reader = Files.newBufferedReader(mapFile, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                if (line.startsWith("map_name ")) {
                    mapTitle = line.substring("map_name ".length()).trim();
                    continue;
                }
                if (line.startsWith("map_rows ")) {
                    rows = Integer.parseInt(line.substring("map_rows ".length()).trim());
                    continue;
                }
                if (line.startsWith("map_cols ")) {
                    cols = Integer.parseInt(line.substring("map_cols ".length()).trim());
                    continue;
                }
                if (line.equals("map_data")) {
                    readingMapData = true;
                    continue;
                }
                if (readingMapData) {
                    if (rows > 0 && dataLines.size() >= rows) {
                        readingMapData = false;
                    }
                    else if (line.indexOf(',') >= 0) {
                        dataLines.add(line);
                    }
                    else {
                        readingMapData = false;
                    }
                }
            }
        }

        String biome = inferBiome(mapFileId, mapTitle);
        List<TileRecord> tiles = new ArrayList<>();
        Map<Integer, Integer> tileIdCounts = new LinkedHashMap<>();

        int tileNumber = 1;
        for (int row = 0; row < dataLines.size(); row++) {
            String[] colsData = dataLines.get(row).split(",");
            int maxCols = cols > 0 ? Math.min(cols, colsData.length) : colsData.length;
            for (int col = 0; col < maxCols; col++) {
                int tileId = Integer.parseInt(colsData[col].trim());
                tiles.add(new TileRecord(tileNumber++, row, col, tileId));
                tileIdCounts.put(tileId, tileIdCounts.getOrDefault(tileId, 0) + 1);
            }
        }

        if (rows == 0) {
            rows = dataLines.size();
        }
        if (cols == 0 && !dataLines.isEmpty()) {
            cols = dataLines.get(0).split(",").length;
        }

        return new MapRecord(
                mapId,
                mapFileId,
                toTitleCase(mapTitle),
                biome,
                rows,
                cols,
                tiles,
                tileIdCounts
        );
    }

    private static String inferBiome(String mapFileId, String mapTitle) {
        String key = (mapFileId + " " + mapTitle).toLowerCase();
        if (key.contains("swamp")) {
            return "Swamp";
        }
        if (key.contains("cave")) {
            return "Cave";
        }
        if (key.contains("castle")) {
            return "Castle";
        }
        if (key.contains("shrine")) {
            return "Temple";
        }
        if (key.contains("world") || key.contains("overworld")) {
            return "Overworld";
        }
        if (key.contains("mountain")) {
            return "Mountain";
        }
        if (key.contains("town") || key.contains("brecconary")
                || key.contains("garinham") || key.contains("rimuldar")
                || key.contains("cantlin") || key.contains("kol")) {
            return "Town";
        }
        return "Unknown";
    }

    private static String toTitleCase(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        String normalized = input.replace('_', ' ').trim();
        String[] words = normalized.split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < words.length; i++) {
            String word = words[i];
            if (word.isEmpty()) {
                continue;
            }
            if (i > 0) {
                sb.append(' ');
            }
            sb.append(Character.toUpperCase(word.charAt(0)));
            if (word.length() > 1) {
                sb.append(word.substring(1).toLowerCase());
            }
        }
        return sb.toString();
    }

    public void printCatalogSummary() {
        System.out.println("WoW Tile Map Catalog");
        System.out.println("====================");
        for (MapRecord map : maps) {
            System.out.println("#" + map.getMapId()
                    + " | fileId=" + map.getMapFileId()
                    + " | title=" + map.getMapTitle()
                    + " | biome=" + map.getBiome()
                    + " | size=" + map.getRows() + "x" + map.getCols()
                    + " | tiles=" + map.getTileCount());
        }
    }

    public void printTileCountBreakdown(int mapId) {
        MapRecord map = getMapById(mapId);
        if (map == null) {
            System.out.println("Map id not found: " + mapId);
            return;
        }
        System.out.println("Tile ID breakdown for map #" + map.getMapId()
                + " (" + map.getMapTitle() + ")");
        System.out.println("----------------------------------------------");
        for (Map.Entry<Integer, Integer> entry : map.getTileIdCounts().entrySet()) {
            System.out.println("tileId=" + entry.getKey() + " count=" + entry.getValue());
        }
    }
}
