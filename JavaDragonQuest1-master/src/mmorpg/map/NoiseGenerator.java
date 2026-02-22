package mmorpg.map;

import java.util.Random;

/**
 * Very small deterministic noise generator using value noise.
 */
public class NoiseGenerator {
    private final int seed;
    private final Random rnd;

    public NoiseGenerator(int seed) {
        this.seed = seed;
        this.rnd = new Random(seed);
    }

    // Simple pseudo-random deterministic hash based on coordinates
    private int hash(int x, int y) {
        int h = x * 374761393 + y * 668265263 ^ seed;
        h = (h ^ (h >> 13)) * 1274126177;
        return h;
    }

    /**
     * Returns a noise value in [0,1) for integer coordinates.
     */
    public double noise(int x, int y) {
        int h = hash(x, y);
        return ((h & 0x7fffffff) / (double) Integer.MAX_VALUE);
    }

    /**
     * Smooth noise via bilinear interpolation of neighboring grid points.
     */
    public double smoothNoise(double x, double y) {
        int x0 = (int) Math.floor(x);
        int y0 = (int) Math.floor(y);
        double sx = x - x0;
        double sy = y - y0;
        double n00 = noise(x0, y0);
        double n10 = noise(x0 + 1, y0);
        double n01 = noise(x0, y0 + 1);
        double n11 = noise(x0 + 1, y0 + 1);
        double ix0 = lerp(n00, n10, sx);
        double ix1 = lerp(n01, n11, sx);
        return lerp(ix0, ix1, sy);
    }

    private double lerp(double a, double b, double t) {
        return a + (b - a) * t;
    }
}
