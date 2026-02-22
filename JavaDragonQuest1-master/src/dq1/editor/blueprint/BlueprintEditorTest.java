package dq1.editor.blueprint;

import javax.swing.*;
import java.awt.*;
import java.util.UUID;

public class BlueprintEditorTest {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            BlueprintGraph graph = new BlueprintGraph();

            Node a = new Node(UUID.randomUUID().toString(), "Add");
            a.setPosition(40,40);
            a.addPin(new Pin(UUID.randomUUID().toString(), "In", Pin.PinType.INPUT, "float"));
            a.addPin(new Pin(UUID.randomUUID().toString(), "Out", Pin.PinType.OUTPUT, "float"));
            graph.addNode(a);

            Node b = new Node(UUID.randomUUID().toString(), "Add");
            b.setPosition(260,40);
            b.addPin(new Pin(UUID.randomUUID().toString(), "In", Pin.PinType.INPUT, "float"));
            b.addPin(new Pin(UUID.randomUUID().toString(), "Out", Pin.PinType.OUTPUT, "float"));
            graph.addNode(b);

            JFrame frame = new JFrame("Blueprint Editor Test");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            GraphEditorPanel panel = new GraphEditorPanel(graph);
            GraphEditorTool tool = new GraphEditorTool(graph, panel);
            frame.getContentPane().setLayout(new BorderLayout());
            frame.getContentPane().add(tool, BorderLayout.NORTH);
            frame.getContentPane().add(new JScrollPane(panel), BorderLayout.CENTER);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            // register a simple executable for "Add" nodes for runtime demo
            // left as an exercise for integration
        });
    }
}
