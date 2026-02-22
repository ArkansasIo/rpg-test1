package mmorpg.map;

import java.awt.image.BufferedImage;
import java.awt.Color;
import javax.imageio.ImageIO;
import java.io.File;

/**
 * Simple PNG exporter for Tile[][] maps and double[][] fields.
 */
public class PngExporter {

    public void exportTiles(Tile[][] map, File out) throws Exception {
        int h = map.length;
        int w = map[0].length;
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int rgb = terrainColor(map[y][x].getTerrain()).getRGB();
                img.setRGB(x, y, rgb);
            }
        }
        ImageIO.write(img, "png", out);
    }

    public void exportField(double[][] field, File out) throws Exception {
        int h = field.length;
        int w = field[0].length;
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int v = (int) (clamp(field[y][x], 0, 1) * 255);
                int rgb = new Color(v, v, v).getRGB();
                img.setRGB(x, y, rgb);
            }
        }
        ImageIO.write(img, "png", out);
    }

    private Color terrainColor(Tile.Terrain t) {
        switch (t) {
            case WATER: return new Color(42, 119, 255);
            case SAND: return new Color(230, 215, 160);
            case GRASS: return new Color(99, 179, 92);
            case FOREST: return new Color(34, 110, 28);
            case HILL: return new Color(150, 130, 90);
            case MOUNTAIN: return new Color(140, 140, 140);
            case ROAD: return new Color(120, 100, 80);
            case DUNGEON: return new Color(90, 60, 120);
            case TOWN: return new Color(220, 80, 80);
            default: return Color.MAGENTA;
        }
    }

    private double clamp(double v, double a, double b) {
        return v < a ? a : v > b ? b : v;
    }
}
