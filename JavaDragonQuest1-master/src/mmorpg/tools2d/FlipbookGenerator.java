package mmorpg.tools2d;

import java.util.List;
import java.awt.image.BufferedImage;

public class FlipbookGenerator {
    public static Flipbook fromSprites(String name, List<Sprite> sprites, double frameTime) {
        Flipbook f = new Flipbook(name);
        f.setFrameTime(frameTime);
        for (Sprite s: sprites) f.addFrame(s.getImage());
        return f;
    }
}
