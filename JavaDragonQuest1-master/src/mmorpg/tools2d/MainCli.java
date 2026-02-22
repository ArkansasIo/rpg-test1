package mmorpg.tools2d;

import java.io.File;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.util.List;
import java.util.ArrayList;

public class MainCli {
    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            printHelp();
            return;
        }
        String cmd = args[0];
        if (cmd.equals("slice")) {
            if (args.length < 5) { printHelp(); return; }
            File in = new File(args[1]);
            int w = Integer.parseInt(args[2]);
            int h = Integer.parseInt(args[3]);
            File outDir = new File(args[4]);
            if (!outDir.exists()) outDir.mkdirs();
            List<Sprite> sprites = SpriteSheetSlicer.slice(in, w, h, "sprite");
            int i=0; for (Sprite s: sprites) {
                ImageIO.write(s.getImage(), "png", new File(outDir, s.getName()+".png"));
                i++;
            }
            System.out.println("Sliced " + i + " sprites to " + outDir.getPath());
        } else if (cmd.equals("pack")) {
            if (args.length < 4) { printHelp(); return; }
            File dir = new File(args[1]);
            int atlasW = Integer.parseInt(args[2]);
            File out = new File(args[3]);
            File[] files = dir.listFiles((d,n)->n.toLowerCase().endsWith(".png"));
            List<Sprite> sprites = new ArrayList<>();
            for (File f: files) {
                BufferedImage img = ImageIO.read(f);
                sprites.add(new Sprite(f.getName().replaceAll("\\\\.png$",""), img));
            }
            BufferedImage atlas = AtlasPacker.pack(sprites, atlasW, 2);
            AtlasPacker.saveAtlas(atlas, out);
            System.out.println("Wrote atlas: " + out.getPath());
        } else if (cmd.equals("flipbook")) {
            if (args.length < 4) { printHelp(); return; }
            File dir = new File(args[1]);
            double ft = Double.parseDouble(args[2]);
            String name = args[3];
            File[] files = dir.listFiles((d,n)->n.toLowerCase().endsWith(".png"));
            List<Sprite> sprites = new ArrayList<>();
            for (File f: files) {
                BufferedImage img = ImageIO.read(f);
                sprites.add(new Sprite(f.getName().replaceAll("\\\\.png$",""), img));
            }
            Flipbook fb = FlipbookGenerator.fromSprites(name, sprites, ft);
            System.out.println("Created flipbook '"+fb.getName()+"' with " + fb.getFrameCount() + " frames (in-memory)");
        } else {
            printHelp();
        }
    }

    private static void printHelp() {
        System.out.println("Tools2D CLI - commands:");
        System.out.println("  slice <input.png> <tileW> <tileH> <outDir>  - slice a sprite sheet into tiles");
        System.out.println("  pack <spritesDir> <atlasWidth> <out.png>   - pack PNGs in directory into an atlas");
        System.out.println("  flipbook <spritesDir> <frameTime> <name>  - create flipbook (in-memory)");
    }
}
