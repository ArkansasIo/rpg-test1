package mmorpg.framework;

public class ActiveEffect {

    private final EffectDefinition definition;
    private int turnsRemaining;
    private int stacks;
    private final double drMultiplier;

    public ActiveEffect(EffectDefinition definition, double drMultiplier) {
        this.definition = definition;
        this.turnsRemaining = definition.getDurationTurns();
        this.stacks = 1;
        this.drMultiplier = drMultiplier;
    }

    public EffectDefinition getDefinition() {
        return definition;
    }

    public int getTurnsRemaining() {
        return turnsRemaining;
    }

    public void setTurnsRemaining(int turnsRemaining) {
        this.turnsRemaining = turnsRemaining;
    }

    public int getStacks() {
        return stacks;
    }

    public void addStack() {
        stacks++;
        if (stacks > definition.getMaxStacks()) {
            stacks = definition.getMaxStacks();
        }
    }

    public double getDrMultiplier() {
        return drMultiplier;
    }

    public void tickTurn() {
        turnsRemaining--;
    }

    public boolean isExpired() {
        return turnsRemaining <= 0;
    }
}
