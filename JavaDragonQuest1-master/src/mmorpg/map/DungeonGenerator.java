package mmorpg.map;

import java.util.Random;

/**
 * Small dungeon generator producing rooms connected by corridors.
 * The result is a grid where some tiles become DUNGEON terrain.
 */
public class DungeonGenerator {
    private final int width;
    private final int height;
    private final Random rnd;

    public DungeonGenerator(int width, int height, long seed) {
        this.width = width;
        this.height = height;
        this.rnd = new Random(seed);
    }

    public Tile[][] generate() {
        Tile[][] map = new Tile[height][width];
        // start fully walls (mountain) and carve
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                map[y][x] = new Tile(Tile.Terrain.MOUNTAIN);
            }
        }

        // create several rooms
        int rooms = Math.max(3, (width * height) / 2000);
        int[][] centers = new int[rooms][2];

        for (int i = 0; i < rooms; i++) {
            int rw = 3 + rnd.nextInt(Math.max(3, width / 10));
            int rh = 3 + rnd.nextInt(Math.max(3, height / 10));
            int rx = 1 + rnd.nextInt(Math.max(1, width - rw - 2));
            int ry = 1 + rnd.nextInt(Math.max(1, height - rh - 2));
            carveRect(map, rx, ry, rw, rh);
            centers[i][0] = rx + rw/2;
            centers[i][1] = ry + rh/2;
        }

        // connect centers
        for (int i = 1; i < rooms; i++) {
            int x0 = centers[i-1][0], y0 = centers[i-1][1];
            int x1 = centers[i][0], y1 = centers[i][1];
            carveCorridor(map, x0, y0, x1, y1);
        }

        // mark dungeon tiles
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (map[y][x].getTerrain() != Tile.Terrain.MOUNTAIN) {
                    map[y][x] = new Tile(Tile.Terrain.DUNGEON);
                }
            }
        }
        return map;
    }

    private void carveRect(Tile[][] map, int x, int y, int w, int h) {
        for (int yy = y; yy < y + h && yy < height-1; yy++) {
            for (int xx = x; xx < x + w && xx < width-1; xx++) {
                map[yy][xx] = new Tile(Tile.Terrain.GRASS);
            }
        }
    }

    private void carveCorridor(Tile[][] map, int x0, int y0, int x1, int y1) {
        int x = x0, y = y0;
        while (x != x1) {
            map[y][x] = new Tile(Tile.Terrain.GRASS);
            x += x < x1 ? 1 : -1;
        }
        while (y != y1) {
            map[y][x] = new Tile(Tile.Terrain.GRASS);
            y += y < y1 ? 1 : -1;
        }
    }
}
