package dq1.editor.blueprint;

import java.util.HashMap;
import java.util.Map;

/**
 * Very small runtime that evaluates a graph in topological order using a naive approach.
 * Nodes that perform computations should implement the ExecutableNode interface (below).
 */
public class BlueprintRuntime {

    public interface ExecutableNode {
        // key=pin id, value=object
        Map<String,Object> execute(Node node, Map<String,Object> inputValues);
    }

    private final BlueprintGraph graph;
    private final Map<String, ExecutableNode> registry = new HashMap<>();

    public BlueprintRuntime(BlueprintGraph graph) {
        this.graph = graph;
    }

    public void register(String nodeTitle, ExecutableNode impl) {
        registry.put(nodeTitle, impl);
    }

    /** Naive execution: assumes acyclic graph and executes nodes in insertion order; for a real system you'd topologically sort. */
    public Map<String,Object> run() {
        Map<String,Object> nodeOutputs = new HashMap<>();
        for (Node node : graph.getNodes()) {
            ExecutableNode impl = registry.get(node.getTitle());
            if (impl == null) continue; // no-op nodes
            Map<String,Object> inputs = new HashMap<>();
            // gather inputs from links
            for (Link link : graph.getLinks()) {
                if (!link.getToNodeId().equals(node.getId())) continue;
                String fromKey = link.getFromNodeId() + ":" + link.getFromPinId();
                Object value = nodeOutputs.get(fromKey);
                inputs.put(link.getToPinId(), value);
            }
            Map<String,Object> outputs = impl.execute(node, inputs);
            if (outputs != null) {
                for (Map.Entry<String,Object> e : outputs.entrySet()) {
                    String key = node.getId() + ":" + e.getKey();
                    nodeOutputs.put(key, e.getValue());
                }
            }
        }
        return nodeOutputs;
    }
}
