package dq1.core;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

/**
 * Visual WoW map view with clickable regions and icons.
 */
public class WoWMapView extends JPanel {
    private Image mapImage;
    private List<Region> regions = new ArrayList<>();
    private Region hoveredRegion = null;

    public WoWMapView(Image mapImage, List<Region> regions) {
        this.mapImage = mapImage;
        this.regions = regions;
        setPreferredSize(new Dimension(mapImage.getWidth(null), mapImage.getHeight(null)));
        addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                for (Region r : regions) {
                    if (r.contains(e.getX(), e.getY())) {
                        JOptionPane.showMessageDialog(WoWMapView.this, "Clicked: " + r.name);
                    }
                }
            }
        });
        addMouseMotionListener(new MouseAdapter() {
            public void mouseMoved(MouseEvent e) {
                hoveredRegion = null;
                for (Region r : regions) {
                    if (r.contains(e.getX(), e.getY())) {
                        hoveredRegion = r;
                        break;
                    }
                }
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(mapImage, 0, 0, this);
        for (Region r : regions) {
            g.setColor(r == hoveredRegion ? Color.YELLOW : Color.RED);
            g.drawOval(r.x - 8, r.y - 8, 16, 16);
            g.drawString(r.name, r.x + 10, r.y);
        }
    }

    public static class Region {
        public String name;
        public int x, y, radius;
        public Region(String name, int x, int y, int radius) {
            this.name = name; this.x = x; this.y = y; this.radius = radius;
        }
        public boolean contains(int mx, int my) {
            int dx = mx - x, dy = my - y;
            return dx*dx + dy*dy <= radius*radius;
        }
    }
}
