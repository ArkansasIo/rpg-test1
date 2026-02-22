package mmorpg.game;

/**
 * Represents a world or zone event (e.g., invasion, festival, weather change).
 */
public class Event {
    private String name;
    private String description;
    private boolean active;

    public Event(String name, String description) {
        this.name = name;
        this.description = description;
        this.active = false;
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public boolean isActive() { return active; }
    public void activate() { active = true; }
    public void deactivate() { active = false; }
}
