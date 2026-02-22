package dq1.core.rpg;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

public class EquipmentSystem {

    private final EnumMap<EquipmentSlot, RpgItemDefinition> equipped
            = new EnumMap<>(EquipmentSlot.class);

    public boolean equip(RpgItemDefinition definition, CharacterClass characterClass) {
        if (definition == null || definition.getSlot() == null) {
            return false;
        }
        if (!definition.canEquip(characterClass)) {
            return false;
        }
        equipped.put(definition.getSlot(), definition);
        return true;
    }

    public RpgItemDefinition unequip(EquipmentSlot slot) {
        return equipped.remove(slot);
    }

    public Map<EquipmentSlot, RpgItemDefinition> getEquippedItems() {
        return Collections.unmodifiableMap(equipped);
    }

    public RpgStats buildEquipmentStats() {
        RpgStats total = new RpgStats();
        for (RpgItemDefinition definition : equipped.values()) {
            total.addAll(definition.getBaseStats());
            for (AttributeModifier modifier : definition.getAttributes()) {
                total.addModifier(modifier);
            }
        }
        return total;
    }
}
