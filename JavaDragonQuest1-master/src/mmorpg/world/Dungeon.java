package mmorpg.world;

/**
 * Represents a dungeon in the world.
 */
public class Dungeon {
    private String name;
    private int tier;

    public Dungeon(String name, int tier) {
        this.name = name;
        this.tier = tier;
    }

    public String getName() {
        return name;
    }

    public int getTier() {
        return tier;
    }
}
