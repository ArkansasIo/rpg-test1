package mmorpg.world;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

/**
 * Utility to load WoW-style world, continents, zones, and subzones from inf files.
 */
public class WoWWorldLoader {
    public static World loadWoWWorld() throws Exception {
        Map<Integer, String> continentNames = new HashMap<>();
        Map<Integer, String> zoneNames = new HashMap<>();
        Map<Integer, Integer> zoneToContinent = new HashMap<>();
        Map<Integer, List<Integer>> continentToZones = new HashMap<>();
        Map<Integer, List<Integer>> zoneToSubZones = new HashMap<>();
        Map<Integer, String> subZoneNames = new HashMap<>();
        // --- Load continents ---
        try (BufferedReader br = getReader("/res/inf/continents_wow.inf")) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                String[] parts = line.split(",", 3);
                int id = Integer.parseInt(parts[0].trim());
                String name = parts[1].replaceAll("^\s*\"|\"\s*$", "").trim();
                continentNames.put(id, name);
                continentToZones.put(id, new ArrayList<>());
            }
        }
        // --- Load zones ---
        try (BufferedReader br = getReader("/res/inf/zones_wow.inf")) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                String[] parts = line.split(",", 5);
                int id = Integer.parseInt(parts[0].trim());
                String name = parts[1].replaceAll("^\s*\"|\"\s*$", "").trim();
                String continent = parts[2].replaceAll("^\s*\"|\"\s*$", "").trim();
                int continentId = continentNames.entrySet().stream().filter(e -> e.getValue().equals(continent)).map(Map.Entry::getKey).findFirst().orElse(-1);
                if (continentId != -1) {
                    continentToZones.get(continentId).add(id);
                    zoneToContinent.put(id, continentId);
                }
                zoneNames.put(id, name);
                zoneToSubZones.put(id, new ArrayList<>());
            }
        }
        // --- Load subzones ---
        try (BufferedReader br = getReader("/res/inf/subzones_wow.inf")) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                String[] parts = line.split(",", 4);
                int id = Integer.parseInt(parts[0].trim());
                int zoneId = Integer.parseInt(parts[1].trim());
                String name = parts[2].replaceAll("^\s*\"|\"\s*$", "").trim();
                subZoneNames.put(id, name);
                if (zoneToSubZones.containsKey(zoneId)) {
                    zoneToSubZones.get(zoneId).add(id);
                }
            }
        }
        // --- Build objects ---
        List<Continent> continents = new ArrayList<>();
        for (Map.Entry<Integer, String> cont : continentNames.entrySet()) {
            List<Country> countries = new ArrayList<>();
            for (int zoneId : continentToZones.get(cont.getKey())) {
                List<Zone> zones = new ArrayList<>();
                List<SubZone> subZones = new ArrayList<>();
                for (int subZoneId : zoneToSubZones.get(zoneId)) {
                    subZones.add(new SubZone(subZoneNames.get(subZoneId), Collections.emptyList(), Collections.emptyList(), Collections.emptyList()));
                }
                zones.add(new Zone(zoneNames.get(zoneId), subZones));
                countries.add(new Country(zoneNames.get(zoneId), zones));
            }
            continents.add(new Continent(cont.getValue(), countries));
        }
        return new World("Azeroth (WoW)", continents);
    }
    private static BufferedReader getReader(String resource) throws Exception {
        InputStream in = WoWWorldLoader.class.getResourceAsStream(resource);
        if (in == null) throw new RuntimeException("Resource not found: " + resource);
        return new BufferedReader(new InputStreamReader(in, "UTF-8"));
    }
}
