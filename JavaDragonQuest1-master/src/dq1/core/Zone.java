// ...existing code...
package dq1.core;

public class Zone {
    private final int id;
    private final String name;
    private final String biome;
    private final String dungeon;
    private final int difficulty;
    private final int worldTier;
    private final double enemyScaling;
    private final double lootScaling;
    private final String description;

    public Zone(int id, String name, String biome, String dungeon, int difficulty, int worldTier, double enemyScaling, double lootScaling, String description) {
        this.id = id;
        this.name = name;
        this.biome = biome;
        this.dungeon = dungeon;
        this.difficulty = difficulty;
        this.worldTier = worldTier;
        this.enemyScaling = enemyScaling;
        this.lootScaling = lootScaling;
        this.description = description;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getBiome() { return biome; }
    public String getDungeon() { return dungeon; }
    public int getDifficulty() { return difficulty; }
    public int getWorldTier() { return worldTier; }
    public double getEnemyScaling() { return enemyScaling; }
    public double getLootScaling() { return lootScaling; }
    public String getDescription() { return description; }
}
