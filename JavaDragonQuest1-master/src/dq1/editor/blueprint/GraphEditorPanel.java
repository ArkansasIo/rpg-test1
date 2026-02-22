package dq1.editor.blueprint;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Minimal Swing-based graph editor panel that shows nodes as boxes and allows dragging.
 * This is intentionally tiny — use it as a starting point for editor features.
 */
public class GraphEditorPanel extends JPanel {
    private final BlueprintGraph graph;
    private Node draggingNode = null;
    private float dragOffsetX, dragOffsetY;

    public GraphEditorPanel(BlueprintGraph graph) {
        this.graph = graph;
        setBackground(Color.DARK_GRAY);
        setPreferredSize(new Dimension(800,600));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                for (Node n : graph.getNodes()) {
                    Rectangle r = new Rectangle((int)n.getX(), (int)n.getY(), 140, 36);
                    if (r.contains(e.getPoint())) {
                        draggingNode = n;
                        dragOffsetX = e.getX() - n.getX();
                        dragOffsetY = e.getY() - n.getY();
                        return;
                    }
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                draggingNode = null;
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (draggingNode != null) {
                    draggingNode.setPosition(e.getX() - dragOffsetX, e.getY() - dragOffsetY);
                    repaint();
                }
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();

        // draw links
        g2.setColor(Color.LIGHT_GRAY);
        for (Link link : graph.getLinks()) {
            Node from = graph.getNode(link.getFromNodeId());
            Node to = graph.getNode(link.getToNodeId());
            if (from == null || to == null) continue;
            int x1 = (int) from.getX() + 140;
            int y1 = (int) from.getY() + 18;
            int x2 = (int) to.getX();
            int y2 = (int) to.getY() + 18;
            g2.drawLine(x1, y1, x2, y2);
        }

        // draw nodes
        for (Node n : graph.getNodes()) {
            int x = (int)n.getX();
            int y = (int)n.getY();
            g2.setColor(new Color(60,63,65));
            g2.fillRect(x, y, 140, 36);
            g2.setColor(Color.WHITE);
            g2.drawRect(x, y, 140, 36);
            g2.drawString(n.getTitle(), x + 6, y + 18);
        }

        g2.dispose();
    }
}
