package mmorpg.map;

import java.util.Random;

/**
 * River carving: greedy downhill paths from mountain sources to water.
 */
public class RiverGenerator {

    /**
     * Carve up to {@code rivers} rivers into the tile map using the elevation map.
     */
    public void carveRivers(Tile[][] map, double[][] elevation, int rivers, long seed) {
        int h = map.length;
        int w = map[0].length;
        Random rnd = new Random(seed);

        for (int r = 0; r < rivers; r++) {
            // pick a random mountain tile as source
            int sx = -1, sy = -1;
            for (int tries = 0; tries < 2000; tries++) {
                int x = rnd.nextInt(w);
                int y = rnd.nextInt(h);
                if (elevation[y][x] > 0.75) { // mountain source
                    sx = x; sy = y; break;
                }
            }
            if (sx == -1) continue; // no source found

            int x = sx, y = sy;
            int maxSteps = Math.max(w, h) * 6;
            int steps = 0;
            while (steps++ < maxSteps) {
                // if adjacent to ocean/water, stop
                if (map[y][x].getTerrain() == Tile.Terrain.WATER) break;

                // carve current tile into water (river)
                map[y][x] = new Tile(Tile.Terrain.WATER);

                // find neighbor with lowest elevation
                int nx = x, ny = y;
                double best = elevation[y][x];
                for (int dy = -1; dy <= 1; dy++) {
                    for (int dx = -1; dx <= 1; dx++) {
                        if (dx == 0 && dy == 0) continue;
                        int tx = x + dx, ty = y + dy;
                        if (tx < 0 || ty < 0 || tx >= w || ty >= h) continue;
                        double ev = elevation[ty][tx];
                        // prefer strictly lower elevation, but allow gentle descent
                        if (ev < best || (Math.abs(ev - best) < 1e-6 && rnd.nextBoolean())) {
                            best = ev; nx = tx; ny = ty;
                        }
                    }
                }

                // if didn't move (local minima), try random neighbor
                if (nx == x && ny == y) {
                    int dx = rnd.nextInt(3) - 1;
                    int dy = rnd.nextInt(3) - 1;
                    nx = Math.max(0, Math.min(w - 1, x + dx));
                    ny = Math.max(0, Math.min(h - 1, y + dy));
                }

                x = nx; y = ny;
            }
        }
    }
}
