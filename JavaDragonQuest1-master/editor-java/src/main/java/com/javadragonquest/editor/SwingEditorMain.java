package com.javadragonquest.editor;

import com.javadragonquest.editor.model.Actor;
import com.javadragonquest.editor.model.LevelIO;
import com.javadragonquest.editor.model.SceneModel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.UUID;

/**
 * Minimal Swing-based editor used as a fallback or when a Swing editor is preferred.
 * Integrated with the editor model (SceneModel/Actor/LevelIO).
 */
public class SwingEditorMain {

    private static SceneModel scene = new SceneModel();
    private static DefaultTreeModel treeModel;
    private static JTree outliner;
    private static JPanel viewport;
    private static JTextField nameField;
    private static JTextField posField;
    private static Actor selectedActor = null;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}

            JFrame frame = new JFrame("JavaDragonQuest Editor (Swing)");
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setSize(1100, 720);

            // Menu
            JMenuBar mb = new JMenuBar();
            JMenu file = new JMenu("File");
            JMenuItem openItem = new JMenuItem("Open Level...");
            JMenuItem saveItem = new JMenuItem("Save...");

            openItem.addActionListener(e -> doOpen(frame));
            saveItem.addActionListener(e -> doSave(frame));

            file.add(openItem);
            file.add(saveItem);
            mb.add(file);
            frame.setJMenuBar(mb);

            // Toolbar
            JToolBar tb = new JToolBar();
            JButton btnPlay = new JButton("Play");
            btnPlay.addActionListener(e -> JOptionPane.showMessageDialog(frame, "Play pressed (stub)"));
            tb.add(btnPlay);

            // Outliner (left)
            DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(scene.getName());
            treeModel = new DefaultTreeModel(rootNode);
            outliner = new JTree(treeModel);
            JScrollPane outlinerScroll = new JScrollPane(outliner);
            outliner.getSelectionModel().addTreeSelectionListener(new TreeSelectionListener() {
                @Override
                public void valueChanged(TreeSelectionEvent e) {
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) outliner.getLastSelectedPathComponent();
                    if (node == null) return;
                    Object uo = node.getUserObject();
                    if (uo instanceof Actor) {
                        selectActor((Actor) uo);
                    } else {
                        selectActor(null);
                    }
                }
            });

            // Viewport (center)
            viewport = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2 = (Graphics2D) g.create();
                    // draw grid
                    g2.setColor(new Color(0xE6E6E6));
                    int w = getWidth();
                    int h = getHeight();
                    for (int x = 0; x < w; x += 32) g2.drawLine(x, 0, x, h);
                    for (int y = 0; y < h; y += 32) g2.drawLine(0, y, w, y);
                    // draw actors from model
                    for (Actor a : scene.getActors()) {
                        int ax = Math.round(a.getX());
                        int ay = Math.round(a.getY());
                        g2.setColor(a == selectedActor ? Color.ORANGE : Color.BLUE);
                        g2.fillOval(ax - 12, ay - 12, 24, 24);
                        g2.setColor(Color.WHITE);
                        g2.drawString(a.getName(), ax + 14, ay + 6);
                    }
                    g2.dispose();
                }
            };
            viewport.setBackground(Color.WHITE);
            viewport.setPreferredSize(new Dimension(800, 600));

            viewport.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(e)) {
                        // create actor at click
                        Actor a = new Actor(UUID.randomUUID().toString(), "Actor_" + (scene.getActors().size() + 1), e.getX(), e.getY());
                        scene.addActor(a);
                        refreshOutliner();
                        selectActor(a);
                        viewport.repaint();
                    } else if (e.getClickCount() == 1 && SwingUtilities.isLeftMouseButton(e)) {
                        // selection by proximity
                        Actor hit = findActorAt(e.getX(), e.getY());
                        selectActor(hit);
                        viewport.repaint();
                    }
                }
            });

            // Details (right)
            JPanel details = new JPanel();
            details.setLayout(new BoxLayout(details, BoxLayout.Y_AXIS));
            details.setBorder(new EmptyBorder(8,8,8,8));
            details.add(new JLabel("Details"));
            details.add(Box.createVerticalStrut(8));
            details.add(new JLabel("Name:"));
            nameField = new JTextField(12);
            details.add(nameField);
            details.add(Box.createVerticalStrut(8));
            details.add(new JLabel("Position (x,y):"));
            posField = new JTextField(12);
            details.add(posField);
            details.add(Box.createVerticalStrut(8));
            JButton applyBtn = new JButton("Apply");
            applyBtn.addActionListener(e -> applyDetails());
            details.add(applyBtn);

            // Split panes
            JSplitPane rightSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, viewport, details);
            rightSplit.setResizeWeight(0.85);
            JSplitPane mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, outlinerScroll, rightSplit);
            mainSplit.setResizeWeight(0.18);

            frame.getContentPane().setLayout(new BorderLayout());
            frame.getContentPane().add(tb, BorderLayout.NORTH);
            frame.getContentPane().add(mainSplit, BorderLayout.CENTER);

            // On close, exit application
            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    System.exit(0);
                }
            });

            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            // Hide the global splash if present (use reflection to avoid compile-time dependency)
            try {
                Class<?> splashCls = Class.forName("main.Splash");
                java.lang.reflect.Method hide = splashCls.getMethod("hide");
                hide.invoke(null);
            } catch (Throwable t) {
                // ignore if Splash not available
            }

            // initial UI refresh
            refreshOutliner();
            viewport.repaint();
        });
    }

    private static void doOpen(Component parent) {
        JFileChooser fc = new JFileChooser();
        int rc = fc.showOpenDialog(parent);
        if (rc == JFileChooser.APPROVE_OPTION) {
            File f = fc.getSelectedFile();
            try {
                SceneModel loaded = LevelIO.load(f);
                scene.getActors().clear();
                scene.getActors().addAll(loaded.getActors());
                scene.setName(loaded.getName());
                refreshOutliner();
                viewport.repaint();
                JOptionPane.showMessageDialog(parent, "Loaded: " + f.getName());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(parent, "Failed to open: " + ex.getMessage());
            }
        }
    }

    private static void doSave(Component parent) {
        JFileChooser fc = new JFileChooser();
        int rc = fc.showSaveDialog(parent);
        if (rc == JFileChooser.APPROVE_OPTION) {
            File f = fc.getSelectedFile();
            try {
                LevelIO.save(scene, f);
                JOptionPane.showMessageDialog(parent, "Saved: " + f.getName());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(parent, "Failed to save: " + ex.getMessage());
            }
        }
    }

    private static void refreshOutliner() {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(scene.getName());
        for (Actor a : scene.getActors()) {
            DefaultMutableTreeNode an = new DefaultMutableTreeNode(a);
            root.add(an);
        }
        treeModel.setRoot(root);
    }

    private static void selectActor(Actor a) {
        selectedActor = a;
        if (a != null) {
            nameField.setText(a.getName());
            posField.setText(String.format("%.1f,%.1f", a.getX(), a.getY()));
            // expand/select in tree
            // find tree node and select it
            DefaultMutableTreeNode root = (DefaultMutableTreeNode) treeModel.getRoot();
            for (int i = 0; i < root.getChildCount(); i++) {
                DefaultMutableTreeNode ch = (DefaultMutableTreeNode) root.getChildAt(i);
                if (ch.getUserObject() == a) {
                    outliner.setSelectionPath(new javax.swing.tree.TreePath(ch.getPath()));
                    break;
                }
            }
        } else {
            nameField.setText("");
            posField.setText("");
            outliner.clearSelection();
        }
    }

    private static void applyDetails() {
        if (selectedActor == null) return;
        selectedActor.setName(nameField.getText());
        try {
            String[] parts = posField.getText().split(",");
            if (parts.length >= 2) {
                float x = Float.parseFloat(parts[0].trim());
                float y = Float.parseFloat(parts[1].trim());
                selectedActor.setX(x);
                selectedActor.setY(y);
            }
        } catch (Exception ignored) {}
        refreshOutliner();
        viewport.repaint();
    }

    private static Actor findActorAt(int x, int y) {
        for (int i = scene.getActors().size() - 1; i >= 0; i--) {
            Actor a = scene.getActors().get(i);
            double dx = x - a.getX();
            double dy = y - a.getY();
            if (dx * dx + dy * dy <= 16 * 16) return a;
        }
        return null;
    }
}