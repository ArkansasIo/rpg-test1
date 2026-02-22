package dq1.editor;

import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.*;
import javax.swing.*;

/**
 * Minimal Visual Scripting Editor inspired by Unreal Blueprints.
 * Allows adding nodes, connecting them, and basic graph execution.
 */
public class VisualScriptingPanel extends JPanel {
    private java.util.List<Node> nodes = new ArrayList<>();
    private java.util.List<Connection> connections = new ArrayList<>();
    private Node selectedNode = null;
    private Point dragOffset = null;
    private Node connectingFrom = null;
    private Point mousePoint = null;

    // For external dialog integration: save/load buttons
    public JPanel createToolbar() {
        JPanel panel = new JPanel();
        JButton saveBtn = new JButton("Save Graph");
        JButton loadBtn = new JButton("Load Graph");
        JButton execBtn = new JButton("Run Graph");
        panel.add(saveBtn);
        panel.add(loadBtn);
        panel.add(execBtn);
        saveBtn.addActionListener(e -> saveGraphToFile());
        loadBtn.addActionListener(e -> loadGraphFromFile());
        execBtn.addActionListener(e -> executeGraph());
        return panel;
    }

    public void clearGraph() {
        nodes.clear();
        connections.clear();
    }

    public void addGraphNode(String label, int x, int y, NodeType type) {
        nodes.add(new Node(label, x, y, type));
    }

    public int getGraphNodeCount() {
        return nodes.size();
    }

    public void addGraphConnection(int fromIdx, int toIdx) {
        if (fromIdx >= 0 && fromIdx < nodes.size() && toIdx >= 0 && toIdx < nodes.size()) {
            connections.add(new Connection(nodes.get(fromIdx), nodes.get(toIdx)));
        }
    }

    private void saveGraphToFile() {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new java.io.File("graph.vsgraph"));
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (PrintWriter pw = new PrintWriter(chooser.getSelectedFile())) {
                // Save nodes
                for (Node n : nodes) {
                    pw.println("NODE," + n.label + "," + n.x + "," + n.y + "," + n.type);
                }
                // Save connections (by node index)
                for (Connection c : connections) {
                    int fromIdx = nodes.indexOf(c.from);
                    int toIdx = nodes.indexOf(c.to);
                    if (fromIdx >= 0 && toIdx >= 0) {
                        pw.println("CONN," + fromIdx + "," + toIdx);
                    }
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error saving graph: " + ex.getMessage());
            }
        }
    }

    private void loadGraphFromFile() {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new java.io.File("graph.vsgraph"));
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (BufferedReader br = new BufferedReader(new FileReader(chooser.getSelectedFile()))) {
                nodes.clear();
                connections.clear();
                java.util.List<String> lines = new ArrayList<>();
                String line;
                while ((line = br.readLine()) != null) lines.add(line);
                // First pass: nodes
                for (String l : lines) {
                    if (l.startsWith("NODE,")) {
                        String[] parts = l.split(",");
                        if (parts.length >= 5) {
                            String label = parts[1];
                            int x = Integer.parseInt(parts[2]);
                            int y = Integer.parseInt(parts[3]);
                            NodeType type = NodeType.valueOf(parts[4]);
                            nodes.add(new Node(label, x, y, type));
                        }
                    }
                }
                // Second pass: connections
                for (String l : lines) {
                    if (l.startsWith("CONN,")) {
                        String[] parts = l.split(",");
                        if (parts.length >= 3) {
                            int fromIdx = Integer.parseInt(parts[1]);
                            int toIdx = Integer.parseInt(parts[2]);
                            if (fromIdx >= 0 && fromIdx < nodes.size() && toIdx >= 0 && toIdx < nodes.size()) {
                                connections.add(new Connection(nodes.get(fromIdx), nodes.get(toIdx)));
                            }
                        }
                    }
                }
                repaint();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error loading graph: " + ex.getMessage());
            }
        }
    }

    public VisualScriptingPanel() {
        setBackground(Color.DARK_GRAY);
        setFocusable(true);
        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                Node node = getNodeAt(e.getPoint());
                if (SwingUtilities.isLeftMouseButton(e)) {
                    if (node != null) {
                        selectedNode = node;
                        dragOffset = new Point(e.getX() - node.x, e.getY() - node.y);
                    } else {
                        selectedNode = null;
                    }
                } else if (SwingUtilities.isRightMouseButton(e)) {
                    showContextMenu(e.getPoint());
                } else if (SwingUtilities.isMiddleMouseButton(e)) {
                    // Start connection from node
                    if (node != null) {
                        connectingFrom = node;
                        mousePoint = e.getPoint();
                    }
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (connectingFrom != null) {
                    Node target = getNodeAt(e.getPoint());
                    if (target != null && target != connectingFrom) {
                        connections.add(new Connection(connectingFrom, target));
                    }
                    connectingFrom = null;
                    repaint();
                }
                selectedNode = null;
                dragOffset = null;
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (selectedNode != null && dragOffset != null) {
                    selectedNode.x = e.getX() - dragOffset.x;
                    selectedNode.y = e.getY() - dragOffset.y;
                    repaint();
                }
                if (connectingFrom != null) {
                    mousePoint = e.getPoint();
                    repaint();
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                mousePoint = e.getPoint();
            }
        };
        addMouseListener(mouseAdapter);
        addMouseMotionListener(mouseAdapter);
    }

    private void showContextMenu(Point p) {
        JPopupMenu menu = new JPopupMenu();
        JMenuItem addEvent = new JMenuItem("Add Event Node");
        addEvent.addActionListener(e -> {
            nodes.add(new Node("Event", p.x, p.y, NodeType.EVENT));
            repaint();
        });
        JMenuItem addAction = new JMenuItem("Add Action Node");
        addAction.addActionListener(e -> {
            nodes.add(new Node("Action", p.x, p.y, NodeType.ACTION));
            repaint();
        });
        JMenuItem addVariable = new JMenuItem("Add Variable Node");
        addVariable.addActionListener(e -> {
            nodes.add(new Node("Variable", p.x, p.y, NodeType.VARIABLE));
            repaint();
        });
        menu.add(addEvent);
        menu.add(addAction);
        menu.add(addVariable);
        menu.show(this, p.x, p.y);
    }

    private Node getNodeAt(Point p) {
        for (int i = nodes.size() - 1; i >= 0; i--) {
            Node node = nodes.get(i);
            if (node.contains(p)) return node;
        }
        return null;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        // Draw connections
        g2.setStroke(new BasicStroke(2));
        g2.setColor(Color.LIGHT_GRAY);
        for (Connection c : connections) {
            g2.drawLine(c.from.x + Node.WIDTH / 2, c.from.y + Node.HEIGHT / 2,
                        c.to.x + Node.WIDTH / 2, c.to.y + Node.HEIGHT / 2);
        }
        // Draw nodes
        for (Node node : nodes) {
            node.draw(g2);
        }
        // Draw connection in progress
        if (connectingFrom != null && mousePoint != null) {
            g2.setColor(Color.YELLOW);
            g2.drawLine(connectingFrom.x + Node.WIDTH / 2, connectingFrom.y + Node.HEIGHT / 2,
                        mousePoint.x, mousePoint.y);
        }
    }

    // Node class
    static class Node {
        static final int WIDTH = 100, HEIGHT = 50;
        String label;
        int x, y;
        NodeType type;
        public Node(String label, int x, int y, NodeType type) {
            this.label = label;
            this.x = x;
            this.y = y;
            this.type = type;
        }
        public boolean contains(Point p) {
            return p.x >= x && p.x <= x + WIDTH && p.y >= y && p.y <= y + HEIGHT;
        }
        public void draw(Graphics2D g2) {
            Color color;
            switch (type) {
                case EVENT: color = new Color(0x4CAF50); break;
                case ACTION: color = new Color(0x2196F3); break;
                case VARIABLE: color = new Color(0xFFC107); break;
                default: color = Color.GRAY;
            }
            g2.setColor(color);
            g2.fillRoundRect(x, y, WIDTH, HEIGHT, 16, 16);
            g2.setColor(Color.BLACK);
            g2.drawRoundRect(x, y, WIDTH, HEIGHT, 16, 16);
            g2.setColor(Color.WHITE);
            g2.drawString(label, x + 10, y + 25);
        }
    }

    // Connection class
    static class Connection {
        Node from, to;
        public Connection(Node from, Node to) {
            this.from = from;
            this.to = to;
        }
    }

    enum NodeType { EVENT, ACTION, VARIABLE }

    // Example: execute the graph (very basic, just prints execution order)
    public void executeGraph() {
        Set<Node> visited = new HashSet<>();
        for (Node node : nodes) {
            if (node.type == NodeType.EVENT) {
                executeFrom(node, visited);
            }
        }
    }
    private void executeFrom(Node node, Set<Node> visited) {
        if (!visited.add(node)) return;
        System.out.println("Executing node: " + node.label);
        for (Connection c : connections) {
            if (c.from == node) {
                executeFrom(c.to, visited);
            }
        }
    }
}
