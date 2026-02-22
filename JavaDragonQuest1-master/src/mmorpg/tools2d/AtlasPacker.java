package mmorpg.tools2d;

import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.io.File;
import java.util.List;
import javax.imageio.ImageIO;

public class AtlasPacker {
    // Simple grid packer: pack into rows of fixed width
    public static BufferedImage pack(List<Sprite> sprites, int atlasWidth, int padding) {
        int x=0,y=0,rowH=0;
        BufferedImage atlas = new BufferedImage(atlasWidth, 4096, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = atlas.createGraphics();
        for (Sprite s: sprites) {
            BufferedImage img = s.getImage();
            if (x + img.getWidth() + padding > atlasWidth) {
                x = 0;
                y += rowH + padding;
                rowH = 0;
            }
            g.drawImage(img, x, y, null);
            x += img.getWidth() + padding;
            rowH = Math.max(rowH, img.getHeight());
        }
        g.dispose();
        return atlas;
    }

    public static void saveAtlas(BufferedImage atlas, File out) throws Exception {
        ImageIO.write(atlas, "png", out);
    }
}
