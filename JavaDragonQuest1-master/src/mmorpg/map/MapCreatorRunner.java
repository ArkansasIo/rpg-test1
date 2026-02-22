package mmorpg.map;

import java.io.File;

/**
 * Map Creator runner inspired by rollforfantasy map-creator features.
 * Generates elevation + moisture, carves rivers, chooses biomes, and exports PNGs.
 */
public class MapCreatorRunner {
    public static void main(String[] args) throws Exception {
        int w = 512, h = 256, seed = 12345;
        if (args.length >= 1) w = Integer.parseInt(args[0]);
        if (args.length >= 2) h = Integer.parseInt(args[1]);
        if (args.length >= 3) seed = Integer.parseInt(args[2]);

        ElevationGenerator eg = new ElevationGenerator(seed);
        double[][] elev = eg.generateElevation(w, h, 5);
        double[][] moist = eg.generateMoisture(w, h, 4);

        WorldGenerator wg = new WorldGenerator(w, h, seed);
        Tile[][] map = wg.generate();

        // overlay elevation thresholds onto terrain (simple mapping)
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                double e = elev[y][x];
                double m = moist[y][x];
                if (e < 0.2) map[y][x] = new Tile(Tile.Terrain.WATER);
                else if (e < 0.25) map[y][x] = new Tile(Tile.Terrain.SAND);
                else if (e < 0.45) map[y][x] = new Tile(Tile.Terrain.GRASS);
                else if (e < 0.62) map[y][x] = new Tile(Tile.Terrain.FOREST);
                else if (e < 0.8) map[y][x] = new Tile(Tile.Terrain.HILL);
                else map[y][x] = new Tile(Tile.Terrain.MOUNTAIN);
                // swamp rule
                if (e > 0.2 && m > 0.78) map[y][x] = new Tile(Tile.Terrain.SAND);
            }
        }

        RiverGenerator rg = new RiverGenerator();
        rg.carveRivers(map, elev, Math.max(1, (w*h)/(512*128)), seed + 7);

        KingdomGenerator kg = new KingdomGenerator(seed + 13);
        kg.placeTown(map, w/4, h/3);
        kg.placeTown(map, w*3/5, h*2/3);
        kg.placeRoad(map, w/4, h/3, w*3/5, h*2/3);

        PngExporter ex = new PngExporter();
        ex.exportField(elev, new File("elevation.png"));
        ex.exportField(moist, new File("moisture.png"));
        ex.exportTiles(map, new File("world.png"));

        System.out.println("Exported elevation.png, moisture.png, world.png");
    }
}
