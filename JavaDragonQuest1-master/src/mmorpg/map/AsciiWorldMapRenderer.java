package mmorpg.map;

import mmorpg.world.*;
import java.util.*;

/**
 * Renders a simple ASCII/console map of the world, showing discovered and undiscovered locations.
 */
public class AsciiWorldMapRenderer {
    private World world;
    private Set<String> discovered;

    public AsciiWorldMapRenderer(World world, Set<String> discovered) {
        this.world = world;
        this.discovered = discovered;
    }

    public void render() {
        System.out.println("\n=== WORLD MAP ===");
        for (Continent continent : world.getContinents()) {
            printLocation(continent.getName(), 0);
            for (Country country : continent.getCountries()) {
                printLocation(country.getName(), 1);
                for (Zone zone : country.getZones()) {
                    printLocation(zone.getName(), 2);
                    for (SubZone subZone : zone.getSubZones()) {
                        printLocation(subZone.getName(), 3);
                    }
                }
            }
        }
        System.out.println("=================\n");
    }

    private void printLocation(String name, int indent) {
        String prefix = discovered.contains(name) ? "[X] " : "[ ] ";
        for (int i = 0; i < indent; i++) System.out.print("  ");
        System.out.println(prefix + name);
    }
}
