package mmorpg.entities;

/**
 * Represents a monster in the world.
 */
public class Monster {
    private String name;
    private int level;
    private String type;
    private int health;
    private int maxHealth;
    private int attack;
    private int defense;

    public Monster(String name, int level, String type, int health, int attack, int defense) {
        this.name = name;
        this.level = level;
        this.type = type;
        this.health = health;
        this.maxHealth = health;
        this.attack = attack;
        this.defense = defense;
    }

    public String getName() { return name; }
    public int getLevel() { return level; }
    public String getType() { return type; }
    public int getHp() { return health; }
    public int getMaxHp() { return maxHealth; }
    public int getAttack() { return attack; }
    public int getDefense() { return defense; }
    public void takeDamage(int dmg) { health -= dmg; if (health < 0) health = 0; }
    public boolean isAlive() { return health > 0; }
    public void reset() { health = maxHealth; }
}
