package mmorpg.tools2d;

import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import java.util.ArrayList;
import java.util.List;

public class SpriteSheetSlicer {
    public static List<Sprite> slice(File file, int tileW, int tileH, String baseName) throws Exception {
        BufferedImage img = ImageIO.read(file);
        int cols = img.getWidth() / tileW;
        int rows = img.getHeight() / tileH;
        List<Sprite> out = new ArrayList<>();
        int idx = 0;
        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                BufferedImage sub = img.getSubimage(x*tileW, y*tileH, tileW, tileH);
                String name = baseName + "_" + idx++;
                out.add(new Sprite(name, sub));
            }
        }
        return out;
    }
}
