package mmorpg.tools2d;

import java.util.ArrayList;
import java.util.List;
import java.awt.image.BufferedImage;

public class Flipbook {
    private final String name;
    private final List<BufferedImage> frames = new ArrayList<>();
    private double frameTime = 0.08;

    public Flipbook(String name) { this.name = name; }
    public void addFrame(BufferedImage f) { frames.add(f); }
    public void setFrameTime(double t) { frameTime = t; }
    public String getName() { return name; }
    public int getFrameCount() { return frames.size(); }
}
