package mmorpg.world;

/**
 * Represents a raid in the world.
 */
public class Raid {
    private String name;
    private int tier;

    public Raid(String name, int tier) {
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
