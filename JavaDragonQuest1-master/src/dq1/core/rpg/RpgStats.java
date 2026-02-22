package dq1.core.rpg;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

public class RpgStats {

    private final EnumMap<RpgAttribute, Integer> values
            = new EnumMap<>(RpgAttribute.class);

    public RpgStats() {
        for (RpgAttribute attribute : RpgAttribute.values()) {
            values.put(attribute, 0);
        }
    }

    public int get(RpgAttribute attribute) {
        Integer value = values.get(attribute);
        return value == null ? 0 : value;
    }

    public void set(RpgAttribute attribute, int value) {
        values.put(attribute, value);
    }

    public void add(RpgAttribute attribute, int value) {
        set(attribute, get(attribute) + value);
    }

    public void addAll(RpgStats other) {
        for (RpgAttribute attribute : RpgAttribute.values()) {
            add(attribute, other.get(attribute));
        }
    }

    public void addModifier(AttributeModifier modifier) {
        add(modifier.getAttribute(), modifier.getValue());
    }

    public RpgStats copy() {
        RpgStats clone = new RpgStats();
        clone.addAll(this);
        return clone;
    }

    public RpgStats scaled(double multiplier) {
        RpgStats scaledStats = new RpgStats();
        for (RpgAttribute attribute : RpgAttribute.values()) {
            scaledStats.set(attribute, (int) Math.round(get(attribute) * multiplier));
        }
        return scaledStats;
    }

    public Map<RpgAttribute, Integer> asMap() {
        return Collections.unmodifiableMap(values);
    }
}
