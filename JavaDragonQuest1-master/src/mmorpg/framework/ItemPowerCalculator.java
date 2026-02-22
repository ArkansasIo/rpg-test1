package mmorpg.framework;

import dq1.core.rpg.AttributeModifier;
import dq1.core.rpg.RpgAttribute;
import dq1.core.rpg.RpgItemDefinition;
import dq1.core.rpg.RpgStats;
import java.util.EnumMap;

public final class ItemPowerCalculator {

    private ItemPowerCalculator() { }

    public static double calculate(RpgItemDefinition item, PvpPveContext context) {
        StatWeights weights = StatWeights.createDefault(context);
        EnumMap<SecondaryStat, Double> mapped = mapItemStats(item.getBaseStats());

        for (AttributeModifier modifier : item.getAttributes()) {
            SecondaryStat mappedStat = mapAttribute(modifier.getAttribute());
            if (mappedStat != null) {
                mapped.put(mappedStat, mapped.getOrDefault(mappedStat, 0.0) + modifier.getValue());
            }
        }

        double power = 0.0;
        for (var entry : mapped.entrySet()) {
            power += entry.getValue() * weights.get(entry.getKey());
        }

        double rarityBonus = 1.0 + (item.getRarity().ordinal() * (context == PvpPveContext.PVE ? 0.08 : 0.04));
        return power * rarityBonus;
    }

    private static EnumMap<SecondaryStat, Double> mapItemStats(RpgStats stats) {
        EnumMap<SecondaryStat, Double> map = new EnumMap<>(SecondaryStat.class);
        map.put(SecondaryStat.PHYSICAL_POWER, (double) stats.get(RpgAttribute.ATTACK_POWER));
        map.put(SecondaryStat.SPELL_POWER, (double) stats.get(RpgAttribute.SPELL_POWER));
        map.put(SecondaryStat.CRIT_CHANCE, (double) stats.get(RpgAttribute.CRIT_RATE));
        map.put(SecondaryStat.DAMAGE_REDUCTION, (double) stats.get(RpgAttribute.DEFENSE));
        map.put(SecondaryStat.CAST_SPEED, (double) stats.get(RpgAttribute.HASTE));
        map.put(SecondaryStat.HP_REGEN, (double) stats.get(RpgAttribute.MAX_HP) * 0.05);
        return map;
    }

    private static SecondaryStat mapAttribute(RpgAttribute attribute) {
        return switch (attribute) {
            case ATTACK_POWER -> SecondaryStat.PHYSICAL_POWER;
            case SPELL_POWER -> SecondaryStat.SPELL_POWER;
            case DEFENSE -> SecondaryStat.DAMAGE_REDUCTION;
            case CRIT_RATE -> SecondaryStat.CRIT_CHANCE;
            case HASTE -> SecondaryStat.ATTACK_SPEED;
            case MAX_HP -> SecondaryStat.HP_REGEN;
            case MAX_MP -> SecondaryStat.MP_REGEN;
            default -> null;
        };
    }
}
