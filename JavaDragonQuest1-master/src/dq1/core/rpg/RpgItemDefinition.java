package dq1.core.rpg;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public class RpgItemDefinition {

    private final int id;
    private final int typeId;
    private final String typeName;
    private final String name;
    private final ItemKind kind;
    private final Rarity rarity;
    private final EquipmentSlot slot;
    private final EnumSet<CharacterClass> allowedClasses;
    private final RpgStats baseStats;
    private final List<AttributeModifier> attributes;
    private final boolean stackable;

    public RpgItemDefinition(int id, int typeId, String typeName, String name
            , ItemKind kind, Rarity rarity, EquipmentSlot slot
            , Set<CharacterClass> allowedClasses, RpgStats baseStats
            , List<AttributeModifier> attributes, boolean stackable) {

        this.id = id;
        this.typeId = typeId;
        this.typeName = typeName;
        this.name = name;
        this.kind = kind;
        this.rarity = rarity;
        this.slot = slot;
        this.allowedClasses = allowedClasses.isEmpty()
                ? EnumSet.noneOf(CharacterClass.class)
                : EnumSet.copyOf(allowedClasses);
        this.baseStats = baseStats.copy();
        this.attributes = new ArrayList<>(attributes);
        this.stackable = stackable;
    }

    public int getId() {
        return id;
    }

    public int getTypeId() {
        return typeId;
    }

    public String getTypeName() {
        return typeName;
    }

    public String getName() {
        return name;
    }

    public ItemKind getKind() {
        return kind;
    }

    public Rarity getRarity() {
        return rarity;
    }

    public EquipmentSlot getSlot() {
        return slot;
    }

    public Set<CharacterClass> getAllowedClasses() {
        return Collections.unmodifiableSet(allowedClasses);
    }

    public RpgStats getBaseStats() {
        return baseStats.copy();
    }

    public List<AttributeModifier> getAttributes() {
        return Collections.unmodifiableList(attributes);
    }

    public boolean isStackable() {
        return stackable;
    }

    public boolean canEquip(CharacterClass characterClass) {
        return slot != null
                && !allowedClasses.isEmpty()
                && allowedClasses.contains(characterClass);
    }
}
