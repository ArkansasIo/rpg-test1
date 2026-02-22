package mmorpg.tools2d;

import java.awt.image.BufferedImage;
import java.awt.Rectangle;

public class Sprite {
    private final String name;
    private final BufferedImage image;
    private final Rectangle bounds;

    public Sprite(String name, BufferedImage image) {
        this.name = name;
        this.image = image;
        this.bounds = new Rectangle(0, 0, image.getWidth(), image.getHeight());
    }

    public String getName() { return name; }
    public BufferedImage getImage() { return image; }
    public int getWidth() { return image.getWidth(); }
    public int getHeight() { return image.getHeight(); }
    public Rectangle getBounds() { return bounds; }
}
