package mmorpg.map;

/**
 * Elevation + moisture field generator using layered noise.
 */
public class ElevationGenerator {

    private final NoiseGenerator noise;

    public ElevationGenerator(int seed) {
        this.noise = new NoiseGenerator(seed);
    }

    public double[][] generateElevation(int width, int height, int octaves) {
        double[][] elev = new double[height][width];
        double scale = Math.max(width, height) / 64.0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                elev[y][x] = fractalNoise(x / scale, y / scale, octaves);
            }
        }
        return elev;
    }

    public double[][] generateMoisture(int width, int height, int octaves) {
        double[][] moist = new double[height][width];
        double scale = Math.max(width, height) / 48.0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                moist[y][x] = fractalNoise(x / scale + 1000, y / scale + 1000, octaves);
            }
        }
        return moist;
    }

    private double fractalNoise(double x, double y, int octaves) {
        double total = 0, amp = 1, freq = 1, max = 0;
        for (int i = 0; i < octaves; i++) {
            total += noise.smoothNoise(x * freq, y * freq) * amp;
            max += amp;
            amp *= 0.5;
            freq *= 2;
        }
        return total / max;
    }
}
