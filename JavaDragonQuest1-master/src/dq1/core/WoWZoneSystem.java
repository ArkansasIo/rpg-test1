package dq1.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class WoWZoneSystem {

    public static class Activity {
        public enum Type { DUNGEON, RAID, TRIAL, TOWER }
        public final Type type;
        public final String name;

        public Activity(Type type, String name) {
            this.type = type;
            this.name = name;
        }
    }

    public static class WoWSubZone {
        public final int id;
        public final int zoneId;
        public final String name;
        public final String description;
        public final List<Activity> activities = new ArrayList<>();

        public WoWSubZone(int id, int zoneId, String name, String description) {
            this.id = id;
            this.zoneId = zoneId;
            this.name = name;
            this.description = description;
            activities.add(new Activity(Activity.Type.DUNGEON, name + " Depths"));
            activities.add(new Activity(Activity.Type.TRIAL, name + " Challenge"));
        }
    }

    public static class WoWZone {
        public final int id;
        public final String name;
        public final String continent;
        public final String biome;
        public final String biomeTitle;
        public final String dungeon;
        public final int difficulty;
        public final int worldTier;
        public final double enemyScaling;
        public final double lootScaling;
        public final String description;
        public final List<WoWSubZone> subZones = new ArrayList<>();
        public final List<Activity> activities = new ArrayList<>();

        public WoWZone(int id, String name, String continent
                , String biome, String biomeTitle, String dungeon
                , int difficulty, int worldTier
                , double enemyScaling, double lootScaling, String description) {

            this.id = id;
            this.name = name;
            this.continent = continent;
            this.biome = biome;
            this.biomeTitle = biomeTitle;
            this.dungeon = dungeon;
            this.difficulty = difficulty;
            this.worldTier = worldTier;
            this.enemyScaling = enemyScaling;
            this.lootScaling = lootScaling;
            this.description = description;

            if (dungeon != null && !dungeon.equalsIgnoreCase("none")) {
                activities.add(new Activity(Activity.Type.DUNGEON, dungeon));
            }
            activities.add(new Activity(Activity.Type.RAID, name + " Citadel"));
            activities.add(new Activity(Activity.Type.TRIAL, name + " Arena"));
            activities.add(new Activity(Activity.Type.TOWER, name + " Spire"));
        }
    }

    public static final List<WoWZone> zones = new ArrayList<>();
    private static final Map<Integer, WoWZone> zoneById = new HashMap<>();
    private static final Map<String, String> continentDescriptions = new LinkedHashMap<>();

    private WoWZoneSystem() { }

    static {
        loadDefaults();
    }

    public static void reset() {
        zones.clear();
        zoneById.clear();
        continentDescriptions.clear();
    }

    public static void loadDefaults() {
        reset();
        addContinent(1, "Eastern Kingdoms", "Core human/dwarf/undead continent.");
        addContinent(2, "Kalimdor", "Wild lands of orcs, tauren, and night elves.");
        addZone(1, "Elwynn Forest", "Eastern Kingdoms", "forest", "Temperate Forest"
                , "None", 1, 1, 1.0, 1.0
                , "The lush starting zone for humans, home to Stormwind.");
        addSubZone(1, 1, "Northshire Valley", "The starting area for human characters.");
        addSubZone(2, 1, "Goldshire", "A small town in Elwynn Forest.");
    }

    public static void addContinent(int id, String name, String description) {
        continentDescriptions.put(name, description == null ? "" : description);
    }

    public static void addZone(int id, String name, String continent, String biome
            , String biomeTitle, String dungeon, int difficulty, int worldTier
            , double enemyScaling, double lootScaling, String description) {

        WoWZone zone = new WoWZone(id, name, continent, biome, biomeTitle, dungeon
                , difficulty, worldTier, enemyScaling, lootScaling, description);
        zones.add(zone);
        zones.sort(Comparator.comparingInt(z -> z.id));
        zoneById.put(id, zone);
    }

    public static void addSubZone(int id, int zoneId, String name, String description) {
        WoWZone zone = zoneById.get(zoneId);
        if (zone == null) {
            return;
        }
        zone.subZones.add(new WoWSubZone(id, zoneId, name, description));
        zone.subZones.sort(Comparator.comparingInt(s -> s.id));
    }

    public static WoWZone getZoneById(int zoneId) {
        return zoneById.get(zoneId);
    }

    public static List<String> getAllZoneTitles() {
        List<String> titles = new ArrayList<>();
        for (WoWZone zone : zones) {
            titles.add(zone.name);
            for (WoWSubZone sub : zone.subZones) {
                titles.add(zone.name + " - " + sub.name);
            }
        }
        return titles;
    }

    public static List<String> getAllBiomeTitles() {
        List<String> biomes = new ArrayList<>();
        for (WoWZone zone : zones) {
            if (!biomes.contains(zone.biomeTitle)) {
                biomes.add(zone.biomeTitle);
            }
        }
        return biomes;
    }

    public static Map<String, String> getContinentDescriptions() {
        return continentDescriptions;
    }

    public static void loadFromInfAssets(Path infDir) throws IOException {
        reset();

        Path continentsFile = infDir.resolve("continents_wow.inf");
        Path zonesFile = infDir.resolve("zones_wow.inf");
        Path subzonesFile = infDir.resolve("subzones_wow.inf");

        if (Files.exists(continentsFile)) {
            try (BufferedReader br = Files.newBufferedReader(continentsFile, StandardCharsets.UTF_8)) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] parts = parseCsvLine(line);
                    if (parts == null || parts.length < 3) {
                        continue;
                    }
                    int id = parseInt(parts[0], -1);
                    String name = unquote(parts[1]);
                    String desc = unquote(parts[2]);
                    if (id > 0 && !name.isEmpty()) {
                        addContinent(id, name, desc);
                    }
                }
            }
        }

        if (Files.exists(zonesFile)) {
            try (BufferedReader br = Files.newBufferedReader(zonesFile, StandardCharsets.UTF_8)) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] parts = parseCsvLine(line);
                    if (parts == null || parts.length < 10) {
                        continue;
                    }
                    int id = parseInt(parts[0], -1);
                    String name = unquote(parts[1]);
                    String continent = unquote(parts[2]);
                    String biomeTitle = unquote(parts[3]);
                    String dungeon = unquote(parts[4]);
                    int difficulty = parseInt(parts[5], 1);
                    int worldTier = parseInt(parts[6], 1);
                    double enemyScaling = parseDouble(parts[7], 1.0);
                    double lootScaling = parseDouble(parts[8], 1.0);
                    String description = unquote(parts[9]);
                    if (id > 0 && !name.isEmpty()) {
                        addZone(id, name, continent
                                , biomeTitle.toLowerCase(), biomeTitle
                                , dungeon, difficulty, worldTier
                                , enemyScaling, lootScaling, description);
                    }
                }
            }
        }

        if (Files.exists(subzonesFile)) {
            try (BufferedReader br = Files.newBufferedReader(subzonesFile, StandardCharsets.UTF_8)) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] parts = parseCsvLine(line);
                    if (parts == null || parts.length < 4) {
                        continue;
                    }
                    int id = parseInt(parts[0], -1);
                    int zoneId = parseInt(parts[1], -1);
                    String name = unquote(parts[2]);
                    String description = unquote(parts[3]);
                    if (id > 0 && zoneId > 0 && !name.isEmpty()) {
                        addSubZone(id, zoneId, name, description);
                    }
                }
            }
        }

        if (zones.isEmpty()) {
            loadDefaults();
        }
    }

    public static void writeZonesToFile(String filename) {
        try (java.io.FileWriter fw = new java.io.FileWriter(filename)) {
            for (WoWZone zone : zones) {
                fw.write("Zone #" + zone.id + ": " + zone.name + " (" + zone.biomeTitle + ")\n");
                fw.write("  Continent: " + zone.continent + "\n");
                fw.write("  Tier: " + zone.worldTier + " Difficulty: " + zone.difficulty + "\n");
                fw.write("  Scaling: enemy " + zone.enemyScaling + " loot " + zone.lootScaling + "\n");
                for (WoWSubZone sub : zone.subZones) {
                    fw.write("  Subzone #" + sub.id + ": " + sub.name + "\n");
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String[] parseCsvLine(String line) {
        if (line == null) {
            return null;
        }
        line = line.trim();
        if (line.isEmpty() || line.startsWith("#")) {
            return null;
        }
        String[] parts = line.split("\\s*,\\s*");
        return parts.length == 0 ? null : parts;
    }

    private static String unquote(String value) {
        if (value == null) {
            return "";
        }
        value = value.trim();
        if (value.startsWith("\"") && value.endsWith("\"") && value.length() >= 2) {
            return value.substring(1, value.length() - 1);
        }
        return value;
    }

    private static int parseInt(String value, int fallback) {
        try {
            return Integer.parseInt(unquote(value));
        }
        catch (Exception e) {
            return fallback;
        }
    }

    private static double parseDouble(String value, double fallback) {
        try {
            return Double.parseDouble(unquote(value));
        }
        catch (Exception e) {
            return fallback;
        }
    }
}
