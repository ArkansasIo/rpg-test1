package dq1.editor.blueprint;

import java.io.Serializable;

public class Pin implements Serializable {
    public enum PinType { INPUT, OUTPUT }

    private final String id;
    private String name;
    private final PinType type;
    private String dataType; // simple type name, e.g. "float", "int", "Vector2"

    public Pin(String id, String name, PinType type, String dataType) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.dataType = dataType;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public PinType getType() { return type; }
    public String getDataType() { return dataType; }
    public void setDataType(String dataType) { this.dataType = dataType; }

    @Override
    public String toString() {
        return "Pin{" + id + ":" + name + "," + type + "," + dataType + '}';
    }
}
