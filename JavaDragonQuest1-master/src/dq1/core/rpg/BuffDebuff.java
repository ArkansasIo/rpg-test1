package dq1.core.rpg;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BuffDebuff {

    private final String id;
    private final String name;
    private final boolean buff;
    private final int durationTurns;
    private final boolean stackable;
    private final List<AttributeModifier> modifiers = new ArrayList<>();

    public BuffDebuff(String id, String name, boolean buff
            , int durationTurns, boolean stackable) {

        this.id = id;
        this.name = name;
        this.buff = buff;
        this.durationTurns = durationTurns;
        this.stackable = stackable;
    }

    public BuffDebuff addModifier(RpgAttribute attribute, int value) {
        modifiers.add(new AttributeModifier(attribute, value, name));
        return this;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isBuff() {
        return buff;
    }

    public int getDurationTurns() {
        return durationTurns;
    }

    public boolean isStackable() {
        return stackable;
    }

    public List<AttributeModifier> getModifiers() {
        return Collections.unmodifiableList(modifiers);
    }
}
