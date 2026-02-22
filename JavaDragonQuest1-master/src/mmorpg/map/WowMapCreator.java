package mmorpg.map;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Generates a large "wow" world map image using the WorldGenerator and exports PNG.
 * Usage: java -cp "build\classes;lib\*" mmorpg.map.WowMapCreator [width] [height] [seed] [outDir]
 */
public class WowMapCreator {
    public static void main(String[] args) throws Exception {
        int w = 2048;
        int h = 1024;
        long seed = System.currentTimeMillis();
        String outDir = "maps";
        if (args.length >= 1) w = Integer.parseInt(args[0]);
        if (args.length >= 2) h = Integer.parseInt(args[1]);
        if (args.length >= 3) seed = Long.parseLong(args[2]);
        if (args.length >= 4) outDir = args[3];

        File dir = new File(outDir);
        if (!dir.exists()) dir.mkdirs();

        String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));

        System.out.println("Generating WOW world map: " + w + "x" + h + " seed=" + seed);
        WorldGenerator wg = new WorldGenerator(w, h, (int)seed);
        Tile[][] map = wg.generate();

        PngExporter ex = new PngExporter();
        File out = new File(dir, "wow-" + ts + ".png");
        ex.exportTiles(map, out);
        System.out.println("Exported world map to: " + out.getPath());
    }
}
