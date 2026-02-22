package dq1.editor.blueprint;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Node implements Serializable {
    private final String id;
    private String title;
    private float x, y;

    // pins grouped by id
    private final Map<String, Pin> pins = new HashMap<>();
    private final List<String> inputPinOrder = new ArrayList<>();
    private final List<String> outputPinOrder = new ArrayList<>();

    public Node(String id, String title) {
        this.id = id;
        this.title = title;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public float getX() { return x; }
    public float getY() { return y; }
    public void setPosition(float x, float y) { this.x = x; this.y = y; }

    public void addPin(Pin pin) {
        pins.put(pin.getId(), pin);
        if (pin.getType() == Pin.PinType.INPUT) inputPinOrder.add(pin.getId());
        else outputPinOrder.add(pin.getId());
    }

    public Pin getPin(String pinId) { return pins.get(pinId); }
    public List<Pin> getInputPins() {
        List<Pin> list = new ArrayList<>();
        for (String id : inputPinOrder) list.add(pins.get(id));
        return list;
    }
    public List<Pin> getOutputPins() {
        List<Pin> list = new ArrayList<>();
        for (String id : outputPinOrder) list.add(pins.get(id));
        return list;
    }
}
