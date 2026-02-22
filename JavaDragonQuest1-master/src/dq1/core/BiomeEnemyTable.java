package dq1.core;

import java.util.*;

public class BiomeEnemyTable {
    // Map biome id to enemy spawn table
    public static final Map<Integer, List<EnemySpawn>> biomeEnemySpawns = new HashMap<>();

    static {
        // Example: Assign enemies to biomes
        addEnemyToBiome(0, "Forest Wolf", 1, 10); // Temperate Forest
        addEnemyToBiome(0, "Forest Slime", 1, 5);
        addEnemyToBiome(1, "Sand Scorpion", 1, 8); // Arid Desert
        addEnemyToBiome(1, "Desert Bandit", 2, 4);
        addEnemyToBiome(2, "Swamp Lizard", 1, 7); // Swamp
        addEnemyToBiome(2, "Swamp Hag", 3, 2);
        // ...repeat for all biomes
    }

    public static void addEnemyToBiome(int biomeId, String enemyName, int minLevel, int spawnWeight) {
        List<EnemySpawn> table = biomeEnemySpawns.computeIfAbsent(biomeId, k -> new ArrayList<>());
        table.add(new EnemySpawn(enemyName, minLevel, spawnWeight));
    }

    public static List<EnemySpawn> getSpawnsForBiome(int biomeId) {
        return biomeEnemySpawns.getOrDefault(biomeId, Collections.emptyList());
    }

    public static class EnemySpawn {
        public final String name;
        public final int minLevel;
        public final int weight;
        public EnemySpawn(String name, int minLevel, int weight) {
            this.name = name;
            this.minLevel = minLevel;
            this.weight = weight;
        }
    }
}
