package dq1.editor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

/**
 * Simple 16x16 pixel tile editor panel with PNG export.
 */
public class PixelTilesetEditorPanel extends JPanel {

    private static final int TILE_SIZE = 16;
    private static final int CELL_SIZE = 22;
    private static final int TRANSPARENT = 0x00000000;
    private static final int[] PALETTE = new int[] {
        0x00000000, 0xFF000000, 0xFFFFFFFF, 0xFFCC0000, 0xFF00AA00, 0xFF0066CC,
        0xFFFFCC00, 0xFFFF8800, 0xFF9900CC, 0xFF00CCCC, 0xFF6E4B2A, 0xFF999999
    };

    private final int[][] pixels = new int[TILE_SIZE][TILE_SIZE];
    private final PixelCanvas canvas = new PixelCanvas();
    private final JLabel status = new JLabel("Ready");
    private final JSpinner brushSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 4, 1));
    private int selectedColor = 0xFF000000;

    public PixelTilesetEditorPanel() {
        super(new BorderLayout(8, 8));
        add(buildTopBar(), BorderLayout.NORTH);
        add(canvas, BorderLayout.CENTER);
        add(status, BorderLayout.SOUTH);
        setPreferredSize(new Dimension(560, 480));
    }

    private JPanel buildTopBar() {
        JPanel top = new JPanel(new BorderLayout(8, 8));
        JPanel palettePanel = new JPanel(new GridLayout(2, 6, 4, 4));
        for (int argb : PALETTE) {
            JButton swatch = new JButton();
            swatch.setPreferredSize(new Dimension(28, 28));
            swatch.setBackground(new Color(argb, true));
            swatch.setOpaque(true);
            swatch.setBorderPainted(true);
            swatch.addActionListener(e -> {
                selectedColor = argb;
                status.setText("Selected color: 0x" + Integer.toHexString(selectedColor).toUpperCase());
            });
            palettePanel.add(swatch);
        }
        top.add(palettePanel, BorderLayout.CENTER);

        JPanel actions = new JPanel();
        actions.add(new JLabel("Brush:"));
        actions.add(brushSpinner);

        JButton clear = new JButton("Clear");
        clear.addActionListener(e -> {
            for (int r = 0; r < TILE_SIZE; r++) {
                for (int c = 0; c < TILE_SIZE; c++) {
                    pixels[r][c] = TRANSPARENT;
                }
            }
            canvas.repaint();
            status.setText("Canvas cleared.");
        });

        JButton fill = new JButton("Fill");
        fill.addActionListener(e -> {
            for (int r = 0; r < TILE_SIZE; r++) {
                for (int c = 0; c < TILE_SIZE; c++) {
                    pixels[r][c] = selectedColor;
                }
            }
            canvas.repaint();
            status.setText("Filled tile.");
        });

        JButton export = new JButton("Export PNG");
        export.addActionListener(e -> exportPng());

        actions.add(clear);
        actions.add(fill);
        actions.add(export);
        top.add(actions, BorderLayout.EAST);

        return top;
    }

    private void exportPng() {
        try {
            BufferedImage out = new BufferedImage(TILE_SIZE, TILE_SIZE, BufferedImage.TYPE_INT_ARGB);
            for (int r = 0; r < TILE_SIZE; r++) {
                for (int c = 0; c < TILE_SIZE; c++) {
                    out.setRGB(c, r, pixels[r][c]);
                }
            }
            File dir = new File("docs/editor_exports/pixels");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            String stamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            File outFile = new File(dir, "tile_" + stamp + ".png");
            ImageIO.write(out, "png", outFile);
            status.setText("Exported: " + outFile.getAbsolutePath());
        }
        catch (Exception ex) {
            status.setText("Export failed: " + ex.getMessage());
        }
    }

    private final class PixelCanvas extends JPanel {
        PixelCanvas() {
            setPreferredSize(new Dimension(TILE_SIZE * CELL_SIZE + 1, TILE_SIZE * CELL_SIZE + 1));
            setBackground(new Color(24, 24, 26));
            MouseAdapter painter = new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    paintAt(e);
                }

                @Override
                public void mouseDragged(MouseEvent e) {
                    paintAt(e);
                }
            };
            addMouseListener(painter);
            addMouseMotionListener(painter);
        }

        private void paintAt(MouseEvent e) {
            int col = e.getX() / CELL_SIZE;
            int row = e.getY() / CELL_SIZE;
            if (row < 0 || col < 0 || row >= TILE_SIZE || col >= TILE_SIZE) {
                return;
            }
            if (e.getButton() == MouseEvent.BUTTON2) {
                selectedColor = pixels[row][col];
                status.setText("Picked color: 0x" + Integer.toHexString(selectedColor).toUpperCase());
                return;
            }
            int brush = (Integer) brushSpinner.getValue();
            int color = (e.getModifiersEx() & MouseEvent.BUTTON3_DOWN_MASK) != 0
                    ? TRANSPARENT : selectedColor;
            for (int rr = row; rr < row + brush; rr++) {
                for (int cc = col; cc < col + brush; cc++) {
                    if (rr >= 0 && cc >= 0 && rr < TILE_SIZE && cc < TILE_SIZE) {
                        pixels[rr][cc] = color;
                    }
                }
            }
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            for (int row = 0; row < TILE_SIZE; row++) {
                for (int col = 0; col < TILE_SIZE; col++) {
                    int x = col * CELL_SIZE;
                    int y = row * CELL_SIZE;
                    if (pixels[row][col] == TRANSPARENT) {
                        g2.setColor(((row + col) & 1) == 0 ? new Color(58, 58, 60) : new Color(74, 74, 76));
                        g2.fillRect(x, y, CELL_SIZE, CELL_SIZE);
                    }
                    else {
                        g2.setColor(new Color(pixels[row][col], true));
                        g2.fillRect(x, y, CELL_SIZE, CELL_SIZE);
                    }
                    g2.setColor(new Color(0, 0, 0, 48));
                    g2.drawRect(x, y, CELL_SIZE, CELL_SIZE);
                }
            }
        }
    }
}
