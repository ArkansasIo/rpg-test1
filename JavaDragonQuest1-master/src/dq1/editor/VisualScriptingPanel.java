package dq1.editor;

import dq1.editor.audio.EditorAudioAPI;
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
 * Extended with blueprint-like nodes: PLAY_SOUND, DELAY, SET_VAR, BRANCH.
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
                    pw.println("NODE," + n.label + "," + n.x + "," + n.y + "," + n.type + (n.actionParam != null ? (","+n.actionParam) : ""));
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
                        String[] parts = l.split(",", 6);
                        if (parts.length >= 5) {
                            String label = parts[1];
                            int x = Integer.parseInt(parts[2]);
                            int y = Integer.parseInt(parts[3]);
                            NodeType type = NodeType.valueOf(parts[4]);
                            Node n = new Node(label, x, y, type);
                            if (parts.length >= 6) n.actionParam = parts[5];
                            nodes.add(n);
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
            String param = JOptionPane.showInputDialog(this, "Enter action param (e.g. play:presetName or playfile:filename):");
            Node n = new Node("Action", p.x, p.y, NodeType.ACTION);
            n.actionParam = param;
            nodes.add(n);
            repaint();
        });
        JMenuItem addPlay = new JMenuItem("Add PlaySound Node");
        addPlay.addActionListener(e -> {
            String param = JOptionPane.showInputDialog(this, "Enter preset name or filename to play:");
            Node n = new Node("PlaySound", p.x, p.y, NodeType.PLAY_SOUND);
            n.actionParam = param;
            nodes.add(n);
            repaint();
        });
        JMenuItem addDelay = new JMenuItem("Add Delay Node");
        addDelay.addActionListener(e -> {
            String param = JOptionPane.showInputDialog(this, "Enter delay milliseconds:");
            Node n = new Node("Delay", p.x, p.y, NodeType.DELAY);
            n.actionParam = param;
            nodes.add(n);
            repaint();
        });
        JMenuItem addSetVar = new JMenuItem("Add SetVar Node");
        addSetVar.addActionListener(e -> {
            String param = JOptionPane.showInputDialog(this, "Enter varName=value:");
            Node n = new Node("SetVar", p.x, p.y, NodeType.SET_VAR);
            n.actionParam = param;
            nodes.add(n);
            repaint();
        });
        JMenuItem addBranch = new JMenuItem("Add Branch Node");
        addBranch.addActionListener(e -> {
            String param = JOptionPane.showInputDialog(this, "Enter condition (e.g. var==value):");
            Node n = new Node("Branch", p.x, p.y, NodeType.BRANCH);
            n.actionParam = param;
            nodes.add(n);
            repaint();
        });
        JMenuItem addVariable = new JMenuItem("Add Variable Node");
        addVariable.addActionListener(e -> {
            nodes.add(new Node("Variable", p.x, p.y, NodeType.VARIABLE));
            repaint();
        });
        menu.add(addEvent);
        menu.add(addAction);
        menu.add(addPlay);
        menu.add(addDelay);
        menu.add(addSetVar);
        menu.add(addBranch);
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
        String actionParam; // used by ACTION nodes to store e.g., preset name
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
                case PLAY_SOUND: color = new Color(0x9C27B0); break;
                case DELAY: color = new Color(0x795548); break;
                case SET_VAR: color = new Color(0x3F51B5); break;
                case BRANCH: color = new Color(0xF44336); break;
                default: color = Color.GRAY;
            }
            g2.setColor(color);
            g2.fillRoundRect(x, y, WIDTH, HEIGHT, 16, 16);
            g2.setColor(Color.BLACK);
            g2.drawRoundRect(x, y, WIDTH, HEIGHT, 16, 16);
            g2.setColor(Color.WHITE);
            g2.drawString(label, x + 10, y + 20);
            if ((type == NodeType.ACTION || type == NodeType.PLAY_SOUND || type == NodeType.SET_VAR || type == NodeType.BRANCH || type == NodeType.DELAY) && actionParam != null) {
                g2.setColor(Color.WHITE);
                String preview = actionParam.length() > 20 ? actionParam.substring(0, 20) + "..." : actionParam;
                g2.drawString("-> " + preview, x + 10, y + 36);
            }
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

    enum NodeType { EVENT, ACTION, VARIABLE, PLAY_SOUND, DELAY, SET_VAR, BRANCH }

    // Example: execute the graph (very basic, supports new node types)
    public void executeGraph() {
        Set<Node> visited = new HashSet<>();
        // runtime variable table
        Map<String, String> vars = new HashMap<>();
        for (Node node : nodes) {
            if (node.type == NodeType.EVENT) {
                executeFrom(node, visited, vars);
            }
        }
    }
    private void executeFrom(Node node, Set<Node> visited, Map<String,String> vars) {
        if (!visited.add(node)) return;
        System.out.println("Executing node: " + node.label + " (" + node.type + ")");
        try {
            switch (node.type) {
                case ACTION:
                    if (node.actionParam != null) {
                        // support direct play or generic commands
                        if (node.actionParam.startsWith("play:") ) {
                            String arg = node.actionParam.substring(5);
                            EditorAudioAPI.playPreset(arg);
                        } else if (node.actionParam.startsWith("playfile:")) {
                            String fn = node.actionParam.substring(9);
                            EditorAudioAPI.playFileByName(fn);
                        } else {
                            System.out.println("Action param: " + node.actionParam);
                        }
                    }
                    break;
                case PLAY_SOUND:
                    if (node.actionParam != null) {
                        // try preset first, else file
                        String p = node.actionParam;
                        if (p.contains(".")) {
                            EditorAudioAPI.playFileByName(p);
                        } else {
                            EditorAudioAPI.playPreset(p);
                        }
                    }
                    break;
                case DELAY:
                    try {
                        long ms = Long.parseLong(node.actionParam == null ? "0" : node.actionParam.trim());
                        Thread.sleep(Math.max(0, ms));
                    } catch (Exception ignored) {}
                    break;
                case SET_VAR:
                    if (node.actionParam != null && node.actionParam.contains("=")) {
                        String[] parts = node.actionParam.split("=",2);
                        vars.put(parts[0].trim(), parts[1].trim());
                        System.out.println("Set var: " + parts[0].trim() + " = " + parts[1].trim());
                    }
                    break;
                case BRANCH:
                    boolean takeFirst = true; // default
                    if (node.actionParam != null && node.actionParam.contains("==")) {
                        String[] parts = node.actionParam.split("==",2);
                        String v = vars.get(parts[0].trim());
                        takeFirst = parts[1].trim().equals(v == null ? "" : v);
                        System.out.println("Branch eval: " + parts[0].trim() + " -> '" + v + "' == '" + parts[1].trim() + "' => " + takeFirst);
                    }
                    // for branch, choose successor: first = true, second = false
                    java.util.List<Node> succs = getSuccessors(node);
                    if (takeFirst && succs.size() > 0) executeFrom(succs.get(0), visited, vars);
                    if (!takeFirst && succs.size() > 1) executeFrom(succs.get(1), visited, vars);
                    return; // do not auto-traverse other successors
                default:
                    break;
            }
        } catch (Exception ex) {
            System.err.println("Runtime error on node: " + ex.getMessage());
        }
        // continue to all successors
        for (Connection c : connections) {
            if (c.from == node) {
                executeFrom(c.to, visited, vars);
            }
        }
    }

    private java.util.List<Node> getSuccessors(Node n) {
        java.util.List<Node> out = new ArrayList<>();
        for (Connection c : connections) {
            if (c.from == n) out.add(c.to);
        }
        return out;
    }
}
