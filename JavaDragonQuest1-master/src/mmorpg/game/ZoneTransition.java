package mmorpg.game;
import mmorpg.entities.Player;
import mmorpg.world.Zone;

/**
 * Handles logic for transitioning between zones, including requirements and triggers.
 */
public class ZoneTransition {
    private Zone fromZone;
    private Zone toZone;
    private String requirement;

    public ZoneTransition(Zone fromZone, Zone toZone, String requirement) {
        this.fromZone = fromZone;
        this.toZone = toZone;
        this.requirement = requirement;
    }

    public Zone getFromZone() { return fromZone; }
    public Zone getToZone() { return toZone; }
    public String getRequirement() { return requirement; }

    public boolean canTransition(Player player) {
        // Implement logic to check if player meets requirement
        return true;
    }
}
