package mmorpg.map;

import java.util.Random;

/**
 * WorldGenerator: creates a simple 2D map of Tiles using NoiseGenerator.
 */
public class WorldGenerator {
    private final int width;
    private final int height;
    private final NoiseGenerator noise;

    public WorldGenerator(int width, int height, int seed) {
        this.width = width;
        this.height = height;
        this.noise = new NoiseGenerator(seed);
    }

    public Tile[][] generate() {
        Tile[][] map = new Tile[height][width];

        double scale = Math.max(width, height) / 64.0; // coarser for large maps

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                double v = octaveNoise(x / scale, y / scale, 3);
                Tile.Terrain t = pickTerrain(v);
                map[y][x] = new Tile(t);
            }
        }
        return map;
    }

    private double octaveNoise(double x, double y, int octaves) {
        double amp = 1.0;
        double freq = 1.0;
        double total = 0.0;
        double max = 0.0;
        for (int i = 0; i < octaves; i++) {
            total += noise.smoothNoise(x * freq, y * freq) * amp;
            max += amp;
            amp *= 0.5;
            freq *= 2.0;
        }
        return total / max; // normalized 0..1
    }

    private Tile.Terrain pickTerrain(double v) {
        if (v < 0.2) return Tile.Terrain.WATER;
        if (v < 0.25) return Tile.Terrain.SAND;
        if (v < 0.45) return Tile.Terrain.GRASS;
        if (v < 0.6) return Tile.Terrain.FOREST;
        if (v < 0.75) return Tile.Terrain.HILL;
        return Tile.Terrain.MOUNTAIN;
    }
}
