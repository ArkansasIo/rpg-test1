package dq1.editor.panels;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.List;

/**
 * Lightweight visual scripting panel with a simple node graph editor:
 * - Add nodes
 * - Drag nodes
 * - Connect nodes by clicking output then input
 * - Save / Load graph to a simple text format
 * - Run: performs a simple traversal and shows execution order
 */
public class VisualScriptingPanelStub extends JPanel {
    private final VisualScriptingCanvas canvas;

    public VisualScriptingPanelStub() {
        super(new BorderLayout());
        JLabel lbl = new JLabel("Visual Scripting (Node Graph)", SwingConstants.CENTER);
        lbl.setFont(lbl.getFont().deriveFont(Font.BOLD, 14f));
        add(lbl, BorderLayout.NORTH);

        canvas = new VisualScriptingCanvas();
        add(new JScrollPane(canvas), BorderLayout.CENTER);

        JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(false);
        JButton addNode = new JButton("Add Node");
        addNode.addActionListener(e -> canvas.createNodeAtCenter());
        toolbar.add(addNode);

        JButton undoBtn = new JButton("Undo");
        undoBtn.addActionListener(e -> canvas.undo());
        toolbar.add(undoBtn);

        JButton redoBtn = new JButton("Redo");
        redoBtn.addActionListener(e -> canvas.redo());
        toolbar.add(redoBtn);

        JButton save = new JButton("Save");
        save.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                File f = fc.getSelectedFile();
                try {
                    canvas.getModel().saveToFile(f);
                    JOptionPane.showMessageDialog(this, "Saved to " + f.getName());
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "Save error: " + ex.getMessage());
                }
            }
        });
        toolbar.add(save);

        JButton load = new JButton("Load");
        load.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                File f = fc.getSelectedFile();
                try {
                    canvas.getModel().loadFromFile(f);
                    canvas.repaint();
                    JOptionPane.showMessageDialog(this, "Loaded " + f.getName());
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "Load error: " + ex.getMessage());
                }
            }
        });
        toolbar.add(load);

        JButton clear = new JButton("Clear");
        clear.addActionListener(e -> { canvas.getModel().clear(); canvas.repaint(); });
        toolbar.add(clear);

        JButton run = new JButton("Run");
        run.addActionListener(e -> {
            List<String> order = canvas.getModel().simpleRunOrder();
            JOptionPane.showMessageDialog(this, "Run order:\n" + String.join("\n", order));
        });
        toolbar.add(run);

        add(toolbar, BorderLayout.SOUTH);

        setPreferredSize(new Dimension(800, 600));
    }
}