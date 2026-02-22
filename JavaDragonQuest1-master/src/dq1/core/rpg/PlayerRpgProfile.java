package dq1.core.rpg;

public class PlayerRpgProfile {

    private CharacterClass characterClass;
    private int level;
    private int currentHp;
    private int currentMp;
    private final RpgStats baseStats = new RpgStats();
    private final InventorySystem inventory;
    private final EquipmentSystem equipment = new EquipmentSystem();
    private final BuffDebuffManager buffDebuffManager = new BuffDebuffManager();

    public PlayerRpgProfile(CharacterClass characterClass, int level, int maxInventorySlots) {
        this.characterClass = characterClass;
        this.level = level;
        this.inventory = new InventorySystem(maxInventorySlots);
        this.currentHp = 1;
        this.currentMp = 0;
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

    public int getCurrentHp() {
        return currentHp;
    }

    public int getCurrentMp() {
        return currentMp;
    }

    public int getMaxHp() {
        return getTotalStats().get(RpgAttribute.MAX_HP);
    }

    public int getMaxMp() {
        return getTotalStats().get(RpgAttribute.MAX_MP);
    }

    public void resetResourcesToMax() {
        currentHp = Math.max(1, getMaxHp());
        currentMp = Math.max(0, getMaxMp());
    }

    public void healHp(int amount) {
        if (amount <= 0) {
            return;
        }
        int maxHp = Math.max(1, getMaxHp());
        currentHp += amount;
        if (currentHp > maxHp) {
            currentHp = maxHp;
        }
    }

    public void restoreMp(int amount) {
        if (amount <= 0) {
            return;
        }
        int maxMp = Math.max(0, getMaxMp());
        currentMp += amount;
        if (currentMp > maxMp) {
            currentMp = maxMp;
        }
    }

    public boolean spendMp(int amount) {
        if (amount < 0) {
            return false;
        }
        if (currentMp < amount) {
            return false;
        }
        currentMp -= amount;
        return true;
    }

    public void clampResources() {
        int maxHp = Math.max(1, getMaxHp());
        int maxMp = Math.max(0, getMaxMp());
        if (currentHp <= 0) {
            currentHp = 1;
        }
        if (currentHp > maxHp) {
            currentHp = maxHp;
        }
        if (currentMp < 0) {
            currentMp = 0;
        }
        if (currentMp > maxMp) {
            currentMp = maxMp;
        }
    }

    public void setCurrentHp(int currentHp) {
        this.currentHp = currentHp;
        clampResources();
    }

    public void setCurrentMp(int currentMp) {
        this.currentMp = currentMp;
        clampResources();
    }
}
