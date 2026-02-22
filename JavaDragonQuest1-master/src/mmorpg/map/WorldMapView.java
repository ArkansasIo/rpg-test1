package mmorpg.map;

import mmorpg.world.*;
import java.util.*;

/**
 * Provides a textual or graphical view of the world map, including navigation and discovery.
 */
public class WorldMapView {
    private WorldMap worldMap;

    public WorldMapView(WorldMap worldMap) {
        this.worldMap = worldMap;
    }

    /**
     * Prints a hierarchical view of the world map to the console.
     */
    public void printMap() {
        World world = worldMap.getWorld();
        System.out.println("World: " + world.getName());
        for (Continent continent : world.getContinents()) {
            System.out.println("  Continent: " + continent.getName());
            for (Country country : continent.getCountries()) {
                System.out.println("    Country: " + country.getName());
                for (Zone zone : country.getZones()) {
                    System.out.println("      Zone: " + zone.getName());
                    for (SubZone subZone : zone.getSubZones()) {
                        System.out.println("        SubZone: " + subZone.getName());
                        // Optionally print dungeons, raids, trials
                    }
                }
            }
        }
    }
}
