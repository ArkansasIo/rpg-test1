package mmorpg.entities;

/**
 * Represents an item in the MMORPG (equipment, consumable, quest item, etc.).
 */
public class Item {
    private String name;
    private String type;
    private int tier;

    public Item(String name, String type, int tier) {
        this.name = name;
        this.type = type;
        this.tier = tier;
    }

    public String getName() { return name; }
    public String getType() { return type; }
    public int getTier() { return tier; }
}
