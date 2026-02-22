package mmorpg.game;

import dq1.core.Resource;
import dq1.core.TileMap;
import java.util.*;

/**
 * Main game logic for the MMORPG, now using dq1 maps.
 */
public class GameLogic {
    public static void main(String[] args) {
        // Load all dq1 maps
        Map<String, TileMap> dq1Maps = Resource.getTILE_MAPS();
        if (dq1Maps.isEmpty()) {
            // Force load some maps if not already loaded
            String[] mapIds = {"world", "tantegel_castle", "brecconary", "kol", "rimuldar", "charlock_castle"};
            for (String id : mapIds) {
                try { Resource.getTileMap(id); } catch (Exception ignored) {}
            }
            dq1Maps = Resource.getTILE_MAPS();
        }

        List<String> mapKeys = new ArrayList<>(dq1Maps.keySet());
        if (mapKeys.isEmpty()) {
            System.out.println("No dq1 maps found!");
            return;
        }

        Scanner scanner = new Scanner(System.in);
        int currentMapIdx = 0;
        dq1.core.Player.start(); // Initialize dq1 player
        int playerRow = 10, playerCol = 10; // Starting position
        Set<String> discoveredTiles = new HashSet<>();
        // Try to load discoveredTiles from file if exists
        java.io.File fogFile = new java.io.File("discovered_tiles.sav");
        if (fogFile.exists()) {
            try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(fogFile))) {
                String line;
                while ((line = br.readLine()) != null) {
                    discoveredTiles.add(line.trim());
                }
            } catch (Exception e) { System.out.println("Could not load fog of war: " + e.getMessage()); }
        }
        boolean running = true;
        Random rng = new Random();
        while (running) {
            TileMap map = dq1Maps.get(mapKeys.get(currentMapIdx));
            System.out.println("\nMap: " + map.getName() + " (ID: " + map.getId() + ")");
            System.out.println("Player position: (row=" + playerRow + ", col=" + playerCol + ")");
            // Show player stats
            System.out.println("Player: Lv " + dq1.core.Player.getLV() + " HP: " + dq1.core.Player.getHP() + "/" + dq1.core.Player.getMaxHP() + " MP: " + dq1.core.Player.getMP() + "/" + dq1.core.Player.getMaxMP() + " Gold: " + dq1.core.Player.getG());

            // Fog of war: mark current tile as discovered
            String tileKey = map.getId() + ":" + playerRow + ":" + playerCol;
            discoveredTiles.add(tileKey);

            // Show ASCII minimap (15x15 window centered on player, fog of war)
            int mapRows = map.getRows();
            int mapCols = map.getCols();
            int winSize = 15;
            int half = winSize / 2;
            int minRow = Math.max(0, playerRow - half);
            int maxRow = Math.min(mapRows - 1, playerRow + half);
            int minCol = Math.max(0, playerCol - half);
            int maxCol = Math.min(mapCols - 1, playerCol + half);
            System.out.println("Minimap:");
            for (int r = minRow; r <= maxRow; r++) {
                StringBuilder sb = new StringBuilder();
                for (int c = minCol; c <= maxCol; c++) {
                    String key = map.getId() + ":" + r + ":" + c;
                    if (r == playerRow && c == playerCol) {
                        sb.append('@');
                    } else if (!discoveredTiles.contains(key)) {
                        sb.append(' ');
                    } else {
                        dq1.core.Tile t = map.getTile(r, c);
                        sb.append(t.isBlocked() ? '#' : '.');
                    }
                }
                System.out.println(sb.toString());
            }
            System.out.println("Options:");
            System.out.println("W/A/S/D: Move | I: Items | L: Spells | V: Save | O: Load | M: Map List | N: Next Map | P: Prev Map | H: Help | Q: Quit");
            System.out.print("Choose: ");
            String input = scanner.nextLine().trim().toLowerCase();
            switch (input) {
                case "w":
                    if (playerRow > 0) playerRow--;
                    break;
                case "s":
                    if (playerRow < map.getRows() - 1) playerRow++;
                    break;
                case "a":
                    if (playerCol > 0) playerCol--;
                    break;
                case "d":
                    if (playerCol < map.getCols() - 1) playerCol++;
                    break;
                case "n":
                    currentMapIdx = (currentMapIdx + 1) % mapKeys.size();
                    break;
                case "p":
                    currentMapIdx = (currentMapIdx - 1 + mapKeys.size()) % mapKeys.size();
                    break;
                case "m":
                    System.out.println("Available dq1 Maps:");
                    for (int i = 0; i < mapKeys.size(); i++) {
                        TileMap m = dq1Maps.get(mapKeys.get(i));
                        System.out.println(i + ": " + m.getName() + " (ID: " + m.getId() + ")");
                    }
                    System.out.print("Enter map number to view or blank to cancel: ");
                    String sel = scanner.nextLine();
                    if (!sel.isEmpty()) {
                        try {
                            int idx = Integer.parseInt(sel);
                            if (idx >= 0 && idx < mapKeys.size()) {
                                currentMapIdx = idx;
                                playerRow = 10; playerCol = 10;
                            }
                        } catch (NumberFormatException ignored) {}
                    }
                    break;
                case "i":
                    try {
                        dq1.core.TileMap.showMainMenu();
                    } catch (Exception e) {
                        System.out.println("Item menu error: " + e.getMessage());
                    }
                    break;
                case "l":
                    try {
                        // Spell menu is part of main menu in dq1, so reuse
                        dq1.core.TileMap.showMainMenu();
                    } catch (Exception e) {
                        System.out.println("Spell menu error: " + e.getMessage());
                    }
                    break;
                case "v":
                    try {
                        dq1.core.Game.saveGame();
                        // Save discoveredTiles to file
                        try (java.io.PrintWriter pw = new java.io.PrintWriter(fogFile)) {
                            for (String key : discoveredTiles) pw.println(key);
                        }
                        System.out.println("Game and exploration progress saved (if confirmed in menu).\n");
                    } catch (Exception e) {
                        System.out.println("Save error: " + e.getMessage());
                    }
                    break;
                case "o":
                    try {
                        if (dq1.core.Game.loadGame()) {
                            // Reload discoveredTiles from file
                            discoveredTiles.clear();
                            if (fogFile.exists()) {
                                try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(fogFile))) {
                                    String line;
                                    while ((line = br.readLine()) != null) {
                                        discoveredTiles.add(line.trim());
                                    }
                                }
                            }
                            System.out.println("Game and exploration progress loaded successfully.");
                        } else {
                            System.out.println("Game load failed.");
                        }
                    } catch (Exception e) {
                        System.out.println("Load error: " + e.getMessage());
                    }
                    break;
                case "h":
                    System.out.println("Controls:");
                    System.out.println("W/A/S/D: Move");
                    System.out.println("I: Items | L: Spells | V: Save | O: Load");
                    System.out.println("M: Map List | N: Next Map | P: Prev Map");
                    System.out.println("Q: Quit");
                    break;
                case "q":
                    running = false;
                    break;
                default:
                    System.out.println("Invalid input.");
                    continue;
            }
            // Random encounter (10% chance per move)
            if (rng.nextInt(10) == 0) {
                dq1.core.Tile tile = map.getTile(playerRow, playerCol);
                System.out.println("A wild enemy appears on tile: " + tile + ", Enemy Prob: " + tile.getEnemyProbabilityNumerator() + "/" + tile.getEnemyProbabilityDenominator());
                dq1.core.Enemy enemy = null;
                if (map.canPlayerEncounterEnemyAtCurrentLocation()) {
                    enemy = map.getZoneEnemy();
                }
                if (enemy != null) {
                    System.out.println("Encountered: " + enemy.getName());
                    try {
                        int result = dq1.core.Battle.start(enemy, tile, false, false, map.getMusicId());
                        if (result == 0) {
                            System.out.println("You were defeated!");
                            running = false;
                        } else if (result == 1) {
                            System.out.println("You won the battle!");
                        } else if (result == 2) {
                            System.out.println("You ran away!");
                        } else if (result == 3) {
                            System.out.println("Enemy ran away!");
                        }
                    } catch (Exception e) {
                        System.out.println("Battle error: " + e.getMessage());
                    }
                } else {
                    System.out.println("No enemy found for this tile.");
                }
            }

            // Trigger dq1 events/dialogs at current location
            try {
                map.checkEventTriggered();
            } catch (Exception e) {
                System.out.println("Event error: " + e.getMessage());
            }
        }
        System.out.println("Thanks for exploring dq1 maps in MMORPG!");
    }
}
