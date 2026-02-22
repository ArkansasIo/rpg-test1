package mmorpg.world;

import java.util.List;

/**
 * Represents the entire game world, containing continents and global logic.
 */
public class World {
    private List<Continent> continents;
    private String name;

    public World(String name, List<Continent> continents) {
        this.name = name;
        this.continents = continents;
    }

    public List<Continent> getContinents() {
        return continents;
    }

    public String getName() {
        return name;
    }
}
