package mmorpg.framework;

public class EffectDefinition {

    private final String id;
    private final String name;
    private final EffectCategory category;
    private final StackMode stackMode;
    private final int durationTurns;
    private final int maxStacks;
    private final SecondaryStat affectedStat;
    private final double magnitude;
    private final boolean cleanseable;

    public EffectDefinition(String id, String name, EffectCategory category
            , StackMode stackMode, int durationTurns, int maxStacks
            , SecondaryStat affectedStat, double magnitude, boolean cleanseable) {

        this.id = id;
        this.name = name;
        this.category = category;
        this.stackMode = stackMode;
        this.durationTurns = durationTurns;
        this.maxStacks = maxStacks;
        this.affectedStat = affectedStat;
        this.magnitude = magnitude;
        this.cleanseable = cleanseable;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public EffectCategory getCategory() {
        return category;
    }

    public StackMode getStackMode() {
        return stackMode;
    }

    public int getDurationTurns() {
        return durationTurns;
    }

    public int getMaxStacks() {
        return maxStacks;
    }

    public SecondaryStat getAffectedStat() {
        return affectedStat;
    }

    public double getMagnitude() {
        return magnitude;
    }

    public boolean isCleanseable() {
        return cleanseable;
    }
}
