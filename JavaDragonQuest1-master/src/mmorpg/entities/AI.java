package mmorpg.entities;

/**
 * Represents basic AI logic for monsters, NPCs, or creatures.
 */
public class AI {
    public enum BehaviorType { PASSIVE, AGGRESSIVE, DEFENSIVE, FRIENDLY }

    private BehaviorType behavior;

    public AI(BehaviorType behavior) {
        this.behavior = behavior;
    }

    public BehaviorType getBehavior() { return behavior; }

    public void setBehavior(BehaviorType behavior) { this.behavior = behavior; }

    // Add methods for AI decision making, pathfinding, etc.
}
