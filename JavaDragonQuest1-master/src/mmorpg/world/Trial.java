package mmorpg.world;

/**
 * Represents a trial (challenge instance) in the world.
 */
public class Trial {
    private String name;
    private int levelRequirement;

    public Trial(String name, int levelRequirement) {
        this.name = name;
        this.levelRequirement = levelRequirement;
    }

    public String getName() {
        return name;
    }

    public int getLevelRequirement() {
        return levelRequirement;
    }
}
