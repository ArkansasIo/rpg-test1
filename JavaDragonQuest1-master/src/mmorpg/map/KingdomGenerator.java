package mmorpg.map;

import java.util.Random;

/**
 * Kingdom / settlement generator: place towns and roads on top of a world map.
 * This is a lightweight helper that modifies an existing Tile[][] map.
 */
public class KingdomGenerator {
    private final Random rnd;

    public KingdomGenerator(long seed) {
        this.rnd = new Random(seed);
    }

    public void placeTown(Tile[][] map, int tx, int ty) {
        int h = map.length, w = map[0].length;
        if (tx <= 0 || ty <= 0 || tx >= w-1 || ty >= h-1) return;
        map[ty][tx] = new Tile(Tile.Terrain.TOWN);
        // create small plaza
        for (int dy = -1; dy <= 1; dy++) {
            for (int dx = -1; dx <= 1; dx++) {
                int nx = tx + dx, ny = ty + dy;
                if (nx >= 0 && ny >= 0 && nx < w && ny < h) {
                    map[ny][nx] = new Tile(Tile.Terrain.ROAD);
                }
            }
        }
    }

    public void placeRoad(Tile[][] map, int x0, int y0, int x1, int y1) {
        // simple straight L-shaped road
        int x = x0, y = y0;
        while (x != x1) {
            if (inBounds(map, x, y)) map[y][x] = new Tile(Tile.Terrain.ROAD);
            x += x < x1 ? 1 : -1;
        }
        while (y != y1) {
            if (inBounds(map, x, y)) map[y][x] = new Tile(Tile.Terrain.ROAD);
            y += y < y1 ? 1 : -1;
        }
    }

    private boolean inBounds(Tile[][] map, int x, int y) {
        return y >=0 && y < map.length && x >= 0 && x < map[0].length;
    }
}
