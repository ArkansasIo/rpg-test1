package mmorpg.entities;

import java.util.List;

/**
 * Represents a player character in the MMORPG.
 */
public class Player {
    private String name;
    private int level;
    private int experience;
    private int health;
    private int maxHealth;
    private int mana;
    private int attack;
    private int defense;
    private List<Item> inventory;
    private List<Creature> pets;

    public Player(String name, int level, int health, int mana, int attack, int defense, List<Item> inventory) {
        this.name = name;
        this.level = level;
        this.health = health;
        this.maxHealth = health;
        this.mana = mana;
        this.attack = attack;
        this.defense = defense;
        this.inventory = inventory;
    }

    public String getName() { return name; }
    public int getLevel() { return level; }
    public int getExperience() { return experience; }
    public int getHp() { return health; }
    public int getMaxHp() { return maxHealth; }
    public int getMana() { return mana; }
    public int getAttack() { return attack; }
    public int getDefense() { return defense; }
    public List<Item> getInventory() { return inventory; }
    public List<Creature> getPets() { return pets; }

    public void gainExperience(int xp) { experience += xp; }
    public void takeDamage(int dmg) { health -= dmg; if (health < 0) health = 0; }
    public void heal(int amount) { health += amount; if (health > maxHealth) health = maxHealth; }
    public void useMana(int amount) { mana -= amount; }
    public void addItem(Item item) { inventory.add(item); }
    public void addPet(Creature pet) { pets.add(pet); }

    public boolean isAlive() { return health > 0; }
    public void reset() { health = maxHealth; }
}
