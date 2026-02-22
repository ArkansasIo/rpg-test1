package mmorpg.map;

import mmorpg.world.World;

/**
 * Represents the world map, with navigation and discovery logic.
 */
public class WorldMap {
    private World world;

    public WorldMap(World world) {
        this.world = world;
    }

    public World getWorld() {
        return world;
    }

    // Add methods for navigation, fast travel, fog of war, etc.
}
