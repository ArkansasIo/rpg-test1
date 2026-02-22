package mmorpg.entities;

/**
 * Represents a non-player character (NPC) in the MMORPG.
 */
public class NPC {
    private String name;
    private String role;
    private String dialogue;

    public NPC(String name, String role, String dialogue) {
        this.name = name;
        this.role = role;
        this.dialogue = dialogue;
    }

    public String getName() { return name; }
    public String getRole() { return role; }
    public String getDialogue() { return dialogue; }

    public void speak() {
        System.out.println(name + ": " + dialogue);
    }
}
