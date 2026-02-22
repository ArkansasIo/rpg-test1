package dq1.editor.panels;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

// Simple canvas implementing nodes and links. Lightweight and self-contained.
public class VisualScriptingCanvas extends JPanel {
    private final GraphModel model = new GraphModel();
    private final GraphHistory history = new GraphHistory();
    private Point dragOffset = null;
    private Node selectedNode = null;
    private Node linkStartNode = null;

    public VisualScriptingCanvas() {
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(1200, 800));

        MouseAdapter ma = new MouseAdapter() {
            private Point lastDragPoint = null;
            private int origX, origY;
            @Override
            public void mousePressed(MouseEvent e) {
                Node n = model.findNodeAt(e.getPoint());
                if (n != null) {
                    selectedNode = n;
                    dragOffset = new Point(e.getX() - n.x, e.getY() - n.y);
                    origX = n.x; origY = n.y;
                    if (SwingUtilities.isRightMouseButton(e)) {
                        // start linking
                        linkStartNode = n;
                    }
                } else {
                    selectedNode = null;
                }
                repaint();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (selectedNode != null && dragOffset != null) {
                    int newX = e.getX() - dragOffset.x;
                    int newY = e.getY() - dragOffset.y;
                    selectedNode.x = newX;
                    selectedNode.y = newY;
                    lastDragPoint = e.getPoint();
                    repaint();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (linkStartNode != null && SwingUtilities.isRightMouseButton(e)) {
                    Node target = model.findNodeAt(e.getPoint());
                    if (target != null && target != linkStartNode) {
                        AddLinkCommand cmd = new AddLinkCommand(model, linkStartNode.id, target.id);
                        history.execute(cmd);
                    }
                }
                if (selectedNode != null && lastDragPoint != null) {
                    // push move command
                    int dx = selectedNode.x - origX;
                    int dy = selectedNode.y - origY;
                    if (dx != 0 || dy != 0) {
                        history.execute(new MoveNodeCommand(model, selectedNode.id, origX, origY, selectedNode.x, selectedNode.y));
                    }
                }
                dragOffset = null;
                linkStartNode = null;
                lastDragPoint = null;
                repaint();
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(e)) {
                    // double click to add node
                    int x = e.getX(); int y = e.getY();
                    AddNodeCommand cmd = new AddNodeCommand(model, "Node" + (model.nodes.size() + 1), x - 60, y - 20);
                    history.execute(cmd);
                    repaint();
                }
            }
        };

        addMouseListener(ma);
        addMouseMotionListener(ma);
    }

    public GraphModel getModel() {
        return model;
    }

    public void createNodeAt(int x, int y) {
        AddNodeCommand cmd = new AddNodeCommand(model, "Node" + (model.nodes.size() + 1), x - 60, y - 20);
        history.execute(cmd);
        repaint();
    }

    public void createNodeAtCenter() {
        Dimension s = getPreferredSize();
        createNodeAt(s.width / 2, s.height / 2);
    }

    public void undo() { history.undo(); repaint(); }
    public void redo() { history.redo(); repaint(); }

    @Override
    protected void paintComponent(Graphics g0) {
        super.paintComponent(g0);
        Graphics2D g = (Graphics2D) g0;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // draw links
        g.setColor(Color.GRAY);
        for (GraphModel.Link link : model.links) {
            GraphModel.Node a = model.getNode(link.from);
            GraphModel.Node b = model.getNode(link.to);
            if (a != null && b != null) {
                g.drawLine(a.x + 80, a.y + 15, b.x, b.y + 15);
            }
        }

        // draw nodes
        for (GraphModel.Node n : model.nodes) {
            g.setColor(n == selectedNode ? new Color(200, 230, 255) : new Color(230, 230, 250));
            g.fillRoundRect(n.x, n.y, 120, 30, 8, 8);
            g.setColor(Color.BLACK);
            g.drawRoundRect(n.x, n.y, 120, 30, 8, 8);
            g.drawString(n.title, n.x + 8, n.y + 18);
        }
    }

    // Command implementations
    private static class AddNodeCommand implements GraphHistory.Command {
        private final GraphModel model;
        private final String title;
        private final int x,y;
        private String createdId;
        AddNodeCommand(GraphModel model, String title, int x, int y) { this.model = model; this.title = title; this.x = x; this.y = y; }
        public void execute() { GraphModel.Node n = model.addNode(title, x, y); createdId = n.id; }
        public void undo() { if (createdId != null) model.removeNode(createdId); }
    }

    private static class MoveNodeCommand implements GraphHistory.Command {
        private final GraphModel model; private final String id; private final int fromX, fromY, toX, toY;
        MoveNodeCommand(GraphModel model, String id, int fromX, int fromY, int toX, int toY) { this.model = model; this.id = id; this.fromX = fromX; this.fromY = fromY; this.toX = toX; this.toY = toY; }
        public void execute() { GraphModel.Node n = model.getNode(id); if (n != null) { n.x = toX; n.y = toY; } }
        public void undo() { GraphModel.Node n = model.getNode(id); if (n != null) { n.x = fromX; n.y = fromY; } }
        public boolean mergeWith(GraphHistory.Command other) { return false; }
    }

    private static class AddLinkCommand implements GraphHistory.Command {
        private final GraphModel model; private final String from, to;
        AddLinkCommand(GraphModel model, String from, String to) { this.model = model; this.from = from; this.to = to; }
        public void execute() { model.addLink(from, to); }
        public void undo() { model.removeLink(from, to); }
    }
}