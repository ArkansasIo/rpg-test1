package dq1.editor.blueprint;

import java.io.Serializable;

public class Link implements Serializable {
    private final String id;
    private final String fromNodeId;
    private final String fromPinId;
    private final String toNodeId;
    private final String toPinId;

    public Link(String id, String fromNodeId, String fromPinId, String toNodeId, String toPinId) {
        this.id = id;
        this.fromNodeId = fromNodeId;
        this.fromPinId = fromPinId;
        this.toNodeId = toNodeId;
        this.toPinId = toPinId;
    }

    public String getId() { return id; }
    public String getFromNodeId() { return fromNodeId; }
    public String getFromPinId() { return fromPinId; }
    public String getToNodeId() { return toNodeId; }
    public String getToPinId() { return toPinId; }

    @Override
    public String toString() {
        return "Link{" + id + ": " + fromNodeId + "." + fromPinId + " -> " + toNodeId + "." + toPinId + '}';
    }
}
