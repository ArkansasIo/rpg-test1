package mmorpg;

import mmorpg.world.*;
import mmorpg.entities.*;
import mmorpg.map.WorldMap;
import mmorpg.map.WorldMapView;
import mmorpg.map.AsciiWorldMapRenderer;
import mmorpg.map.PlayerMapState;
import mmorpg.ui.WowUiFrame;
import mmorpg.world.WoWTileMapSystem;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.nio.file.Path;
import javax.swing.SwingUtilities;

/**
 * Main entry point for the MMORPG demo.
 */
public class Main {
    public static void main(String[] args) {
        if (args != null && args.length > 0 && "--wow-ui".equalsIgnoreCase(args[0])) {
            SwingUtilities.invokeLater(() -> new WowUiFrame().setVisible(true));
            return;
        }
        if (args != null && args.length > 0 && "--wow-maps".equalsIgnoreCase(args[0])) {
            try {
                WoWTileMapSystem mapSystem
                        = WoWTileMapSystem.loadFromGameAssets(Path.of("assets", "res", "map"));
                mapSystem.printCatalogSummary();
                if (args.length > 1) {
                    int mapId = Integer.parseInt(args[1]);
                    mapSystem.printTileCountBreakdown(mapId);
                }
            } catch (Exception e) {
                System.err.println("Failed to load WoW tile map system: " + e.getMessage());
                e.printStackTrace();
            }
            return;
        }

        // Sample monsters and creatures
        Monster goblin = new Monster("Goblin", 5, "Beast", 80, 14, 8);
        Monster dragon = new Monster("Ancient Dragon", 50, "Dragon", 1200, 120, 75);
        Creature deer = new Creature("Forest Deer", "Animal");

        // Sample dungeons, raids, trials
        Dungeon cave = new Dungeon("Goblin Cave", 1);
        Raid dragonRaid = new Raid("Dragon's Lair", 5);
        Trial heroTrial = new Trial("Hero's Trial", 10);

        // SubZone with content
        SubZone forest = new SubZone("Whispering Forest", Arrays.asList(cave), Arrays.asList(dragonRaid), Arrays.asList(heroTrial));
        Zone greenlands = new Zone("Greenlands", Arrays.asList(forest));
        Country eloria = new Country("Eloria", Arrays.asList(greenlands));
        Continent aetheria = new Continent("Aetheria", Arrays.asList(eloria));
        World world = new World("Eternal Realms", Arrays.asList(aetheria));
        WorldMap map = new WorldMap(world);

        // Player
        Player player = new Player("Arin", 1, 100, 50, 12, 6, new ArrayList<Item>());
        PlayerMapState playerMap = new PlayerMapState(aetheria, eloria, greenlands, forest);
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to " + world.getName() + "!");
        System.out.println("You are in continent: " + aetheria.getName());
        System.out.println("Country: " + eloria.getName() + ", Zone: " + greenlands.getName() + ", SubZone: " + forest.getName());
        System.out.println("Nearby monsters: " + goblin.getName() + ", " + dragon.getName());
        System.out.println("Wildlife: " + deer.getName());
        System.out.println("Dungeons: " + cave.getName() + ", Raids: " + dragonRaid.getName() + ", Trials: " + heroTrial.getName());
        System.out.println("Player: " + player.getName() + " (Level " + player.getLevel() + ")");

        // Add more game loop logic here as needed

        // Demonstrate use of WorldMapView to print the world map structure
        WorldMapView mapView = new WorldMapView(map);
        mapView.printMap();

        AsciiWorldMapRenderer asciiMap = new AsciiWorldMapRenderer(world, playerMap.getDiscoveredLocations());
        while (true) {
            asciiMap.render();
            System.out.println("\nCurrent Location: " + playerMap.getCurrentContinent().getName() + " > " + playerMap.getCurrentCountry().getName() + " > " + playerMap.getCurrentZone().getName() + " > " + playerMap.getCurrentSubZone().getName());
            System.out.println("Discovered Locations: " + playerMap.isDiscovered(forest.getName()));
            System.out.println("Options: 1) Move to Dungeon 2) Move to Raid 3) Move to Trial 4) Exit");
            String input = scanner.nextLine();
            if (input.equals("1")) {
                System.out.println("You enter the dungeon: " + cave.getName());
                playerMap.discover(cave.getName());
                // Trigger dungeon gameplay here
            } else if (input.equals("2")) {
                System.out.println("You enter the raid: " + dragonRaid.getName());
                playerMap.discover(dragonRaid.getName());
                // Trigger raid gameplay here
            } else if (input.equals("3")) {
                System.out.println("You enter the trial: " + heroTrial.getName());
                playerMap.discover(heroTrial.getName());
                // Trigger trial gameplay here
            } else if (input.equals("4")) {
                System.out.println("Exiting game.");
                break;
            } else {
                System.out.println("Invalid option.");
            }
        }

            // --- WoW Map Demo ---
            try {
                System.out.println("\n--- WoW Azeroth Map ---");
                World wowWorld = mmorpg.world.WoWWorldLoader.loadWoWWorld();
                WorldMapView wowMapView = new WorldMapView(new WorldMap(wowWorld));
                wowMapView.printMap();
            } catch (Exception e) {
                System.out.println("Failed to load WoW map: " + e.getMessage());
            }
    }
}
