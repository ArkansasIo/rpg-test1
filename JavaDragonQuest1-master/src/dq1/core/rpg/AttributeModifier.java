package dq1.core.rpg;

public class AttributeModifier {

    private final RpgAttribute attribute;
    private final int value;
    private final String source;

    public AttributeModifier(RpgAttribute attribute, int value, String source) {
        this.attribute = attribute;
        this.value = value;
        this.source = source;
    }

    public RpgAttribute getAttribute() {
        return attribute;
    }

    public int getValue() {
        return value;
    }

    public String getSource() {
        return source;
    }
}
