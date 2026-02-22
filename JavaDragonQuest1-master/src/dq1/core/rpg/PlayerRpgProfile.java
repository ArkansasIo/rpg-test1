package dq1.core.rpg;

public class PlayerRpgProfile {

    private CharacterClass characterClass;
    private int level;
    private final RpgStats baseStats = new RpgStats();
    private final InventorySystem inventory;
    private final EquipmentSystem equipment = new EquipmentSystem();
    private final BuffDebuffManager buffDebuffManager = new BuffDebuffManager();

    public PlayerRpgProfile(CharacterClass characterClass, int level, int maxInventorySlots) {
        this.characterClass = characterClass;
        this.level = level;
        this.inventory = new InventorySystem(maxInventorySlots);
    }

    public CharacterClass getCharacterClass() {
        return characterClass;
    }

    public void setCharacterClass(CharacterClass characterClass) {
        this.characterClass = characterClass;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public RpgStats getBaseStats() {
        return baseStats;
    }

    public InventorySystem getInventory() {
        return inventory;
    }

    public EquipmentSystem getEquipment() {
        return equipment;
    }

    public BuffDebuffManager getBuffDebuffManager() {
        return buffDebuffManager;
    }

    public boolean equip(RpgItemDefinition itemDefinition) {
        return equipment.equip(itemDefinition, characterClass);
    }

    public void applyEffect(BuffDebuff effect) {
        buffDebuffManager.apply(effect);
    }

    public void tickTurnEffects() {
        buffDebuffManager.tickTurn();
    }

    public RpgStats getTotalStats() {
        RpgStats total = baseStats.copy();
        total.addAll(equipment.buildEquipmentStats());
        for (AttributeModifier modifier : buffDebuffManager.collectModifiers()) {
            total.addModifier(modifier);
        }
        return total;
    }
}
