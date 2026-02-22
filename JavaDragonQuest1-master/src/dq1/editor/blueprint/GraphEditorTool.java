package dq1.editor.blueprint;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.UUID;

/**
 * Small toolbar and utilities to create nodes/links and save/load graphs.
 */
public class GraphEditorTool extends JPanel {
    private final BlueprintGraph graph;
    private final GraphEditorPanel panel;

    public GraphEditorTool(BlueprintGraph graph, GraphEditorPanel panel) {
        this.graph = graph;
        this.panel = panel;
        setLayout(new FlowLayout(FlowLayout.LEFT));

        JButton addNode = new JButton(new AbstractAction("Add Node") {
            @Override
            public void actionPerformed(ActionEvent e) {
                String id = UUID.randomUUID().toString();
                Node n = new Node(id, "Add");
                n.setPosition(40, 40 + graph.getNodes().size() * 60);
                // sample pins
                n.addPin(new Pin(UUID.randomUUID().toString(), "In", Pin.PinType.INPUT, "float"));
                n.addPin(new Pin(UUID.randomUUID().toString(), "Out", Pin.PinType.OUTPUT, "float"));
                graph.addNode(n);
                panel.repaint();
            }
        });

        JButton addLink = new JButton(new AbstractAction("Link Example") {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (graph.getNodes().size() >= 2) {
                    Node a = graph.getNodes().get(0);
                    Node b = graph.getNodes().get(1);
                    Pin out = a.getOutputPins().get(0);
                    Pin in = b.getInputPins().get(0);
                    if (graph.canLink(a.getId(), out.getId(), b.getId(), in.getId())) {
                        Link link = new Link(UUID.randomUUID().toString(), a.getId(), out.getId(), b.getId(), in.getId());
                        graph.addLink(link);
                        panel.repaint();
                    } else {
                        JOptionPane.showMessageDialog(panel, "Cannot link: type mismatch or invalid pins");
                    }
                }
            }
        });

        JButton save = new JButton(new AbstractAction("Save") {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    File f = new File("blueprint_test.graph");
                    GraphSerializer.save(graph, f);
                    JOptionPane.showMessageDialog(panel, "Saved to " + f.getAbsolutePath());
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(panel, "Save failed: " + ex.getMessage());
                }
            }
        });

        JButton load = new JButton(new AbstractAction("Load") {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    File f = new File("blueprint_test.graph");
                    BlueprintGraph g = GraphSerializer.load(f);
                    // simple replace
                    graph.getNodes().clear();
                    graph.getLinks().clear();
                    for (Node n : g.getNodes()) graph.addNode(n);
                    for (Link l : g.getLinks()) graph.addLink(l);
                    panel.repaint();
                    JOptionPane.showMessageDialog(panel, "Loaded from " + f.getAbsolutePath());
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(panel, "Load failed: " + ex.getMessage());
                }
            }
        });

        add(addNode);
        add(addLink);
        add(save);
        add(load);
    }
}
