package dq1.core;

import java.util.ArrayList;
import java.util.List;

public class WoWZoneSystem {
        public static class Activity {
            public enum Type { DUNGEON, RAID, TRIAL, TOWER }
            public final Type type;
            public final String name;
            public Activity(Type type, String name) {
                this.type = type;
                this.name = name;
            }
        }
    public static final List<WoWZone> zones = new ArrayList<>();

    static {
        // Sample WoW zones, subzones, and biomes
        addZone("Elwynn Forest", "forest", new String[]{"Goldshire", "Northshire Valley", "Stone Cairn Lake"}, "Temperate Forest");
        addZone("Durotar", "desert", new String[]{"Valley of Trials", "Sen'jin Village", "Echo Isles"}, "Arid Desert");
        addZone("Tirisfal Glades", "swamp", new String[]{"Brill", "Deathknell", "Agamand Mills"}, "Swamp");
        addZone("Mulgore", "plains", new String[]{"Bloodhoof Village", "Red Cloud Mesa", "Thunder Bluff"}, "Grassland");
        addZone("Darkshore", "coast", new String[]{"Auberdine", "Cliffspring River", "Ameth'Aran"}, "Coastal");
        // Add more zones as needed
    }

    public static void addZone(String name, String biome, String[] subZones, String biomeTitle) {
        WoWZone zone = new WoWZone(name, biome, biomeTitle);
        for (String sub : subZones) {
            zone.subZones.add(new WoWSubZone(sub));
        }
        zones.add(zone);
    }

    public static class WoWZone {
        public final String name;
        public final String biome;
        public final String biomeTitle;
        public final List<WoWSubZone> subZones = new ArrayList<>();
        public final List<Activity> activities = new ArrayList<>();
        public WoWZone(String name, String biome, String biomeTitle) {
            this.name = name;
            this.biome = biome;
            this.biomeTitle = biomeTitle;
            // Example activities per zone
            activities.add(new Activity(Activity.Type.DUNGEON, name + " Caverns"));
            activities.add(new Activity(Activity.Type.RAID, name + " Citadel"));
            activities.add(new Activity(Activity.Type.TRIAL, name + " Arena"));
            activities.add(new Activity(Activity.Type.TOWER, name + " Tower"));
        }
    }

    public static class WoWSubZone {
        public final String name;
        public final List<Activity> activities = new ArrayList<>();
        public WoWSubZone(String name) {
            this.name = name;
            // Example activities per subzone
            activities.add(new Activity(Activity.Type.DUNGEON, name + " Depths"));
            activities.add(new Activity(Activity.Type.RAID, name + " Fortress"));
            activities.add(new Activity(Activity.Type.TRIAL, name + " Challenge"));
            activities.add(new Activity(Activity.Type.TOWER, name + " Spire"));
        }
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
            biomes.add(zone.biomeTitle);
        }
        return biomes;
    }
}