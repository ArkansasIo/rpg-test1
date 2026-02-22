package dq1.core;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

/**
 *
 * @author admin
 */
public class Credits {

    static String msg = "\n\nBy: Stephen Deline Jr\n";

    public static void main(String[] args) throws Exception {
        BitmapFont font = new BitmapFont();
        BufferedImage result = new BufferedImage(256, 240, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = (Graphics2D) result.getGraphics();
        String[] lines = msg.split("\n");
        int lineNumber = 0;
        for (String line : lines) {
            BitmapFont.drawText(g, line, 3, lineNumber);
            lineNumber++;
        }
        ImageIO.write(result, "png", new File("d:/dq1_credits.png"));
    }

}