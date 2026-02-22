package dq1.tools;

import dq1.core.BitmapFont;
import dq1.core.Resource;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class CreditsImageGenerator {
    public static void main(String[] args) throws Exception {
        // load background asset if available
        BufferedImage bg = Resource.getImage("title_shine");
        if (bg == null) {
            bg = new BufferedImage(512, 256, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = (Graphics2D) bg.getGraphics();
            g.fillRect(0, 0, bg.getWidth(), bg.getHeight());
        }
        // create canvas same as bg
        BufferedImage canvas = new BufferedImage(bg.getWidth(), bg.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = (Graphics2D) canvas.getGraphics();
        g2.drawImage(bg, 0, 0, null);

        // draw title image centered
        BufferedImage title = Resource.getImage("title");
        if (title != null) {
            int tx = (canvas.getWidth() - title.getWidth()) / 2;
            g2.drawImage(title, tx, 16, null);
        }

        // draw signature using BitmapFont at bottom
        String text = "By: Stephen Deline Jr";
        int col = 4;
        int row = (canvas.getHeight() / BitmapFont.letterHeight) - 3;
        BitmapFont.drawText(g2, text, col, row);

        // ensure assets dir exists
        File outDir = new File("assets/res/image");
        if (!outDir.exists()) outDir.mkdirs();
        File out = new File(outDir, "credits_wow.png");
        ImageIO.write(canvas, "png", out);
        System.out.println("Wrote: " + out.getAbsolutePath());
    }
}
