package mmorpg.game;

import java.util.List;

/**
 * Represents a quest in the MMORPG.
 */
public class Quest {
    private String name;
    private String description;
    private List<String> objectives;
    private boolean completed;

    public Quest(String name, String description, List<String> objectives) {
        this.name = name;
        this.description = description;
        this.objectives = objectives;
        this.completed = false;
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public List<String> getObjectives() { return objectives; }
    public boolean isCompleted() { return completed; }
    public void complete() { completed = true; }
}
