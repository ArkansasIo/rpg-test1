package mmorpg.map;

/**
 * Small CLI runner to produce maps and print them to stdout.
 * Usage: run this class from the IDE or add a small Ant target that runs it.
 */
public class MapCliRunner {
    public static void main(String[] args) {
        int w = 80;
        int h = 30;
        int seed = 12345;
        // parse args simple
        if (args.length >= 1) {
            try { w = Integer.parseInt(args[0]); } catch (Exception ignored) {}
        }
        if (args.length >= 2) {
            try { h = Integer.parseInt(args[1]); } catch (Exception ignored) {}
        }
        if (args.length >= 3) {
            try { seed = Integer.parseInt(args[2]); } catch (Exception ignored) {}
        }

        WorldGenerator wg = new WorldGenerator(w, h, seed);
        Tile[][] map = wg.generate();

        // place some towns
        KingdomGenerator kg = new KingdomGenerator(seed + 1);
        kg.placeTown(map, w/4, h/3);
        kg.placeTown(map, w*3/5, h*2/3);
        kg.placeRoad(map, w/4, h/3, w*3/5, h*2/3);

        // print
        for (int y = 0; y < h; y++) {
            StringBuilder sb = new StringBuilder();
            for (int x = 0; x < w; x++) {
                sb.append(map[y][x].toChar());
            }
            System.out.println(sb.toString());
        }

        // small dungeon example
        System.out.println("\nSmall dungeon:\n");
        DungeonGenerator dg = new DungeonGenerator(40, 20, seed + 2);
        Tile[][] dungeon = dg.generate();
        for (int y = 0; y < dungeon.length; y++) {
            StringBuilder sb = new StringBuilder();
            for (int x = 0; x < dungeon[0].length; x++) {
                sb.append(dungeon[y][x].toChar());
            }
            System.out.println(sb.toString());
        }
    }
}
