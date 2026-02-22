package dq1.editor.blueprint;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BlueprintGraph implements Serializable {
    private final Map<String, Node> nodes = new HashMap<>();
    private final Map<String, Link> links = new HashMap<>();

    public void addNode(Node node) { nodes.put(node.getId(), node); }
    public Node getNode(String id) { return nodes.get(id); }
    public List<Node> getNodes() { return new ArrayList<>(nodes.values()); }

    public void addLink(Link link) { links.put(link.getId(), link); }
    public Link getLink(String id) { return links.get(id); }
    public List<Link> getLinks() { return new ArrayList<>(links.values()); }

    public boolean canLink(String fromNodeId, String fromPinId, String toNodeId, String toPinId) {
        // Basic checks: pins exist, from is output, to is input, data types match
        Node fromNode = nodes.get(fromNodeId);
        Node toNode = nodes.get(toNodeId);
        if (fromNode == null || toNode == null) return false;
        Pin fromPin = fromNode.getPin(fromPinId);
        Pin toPin = toNode.getPin(toPinId);
        if (fromPin == null || toPin == null) return false;
        if (fromPin.getType() != Pin.PinType.OUTPUT) return false;
        if (toPin.getType() != Pin.PinType.INPUT) return false;
        // simple type equality check; can be extended to allow casting
        return fromPin.getDataType().equals(toPin.getDataType());
    }

    public boolean removeNode(String nodeId) {
        if (!nodes.containsKey(nodeId)) return false;
        nodes.remove(nodeId);
        // remove links referencing it
        List<String> toRemove = new ArrayList<>();
        for (Link link : links.values()) {
            if (link.getFromNodeId().equals(nodeId) || link.getToNodeId().equals(nodeId)) toRemove.add(link.getId());
        }
        for (String id : toRemove) links.remove(id);
        return true;
    }

    public boolean removeLink(String linkId) { return links.remove(linkId) != null; }
}
