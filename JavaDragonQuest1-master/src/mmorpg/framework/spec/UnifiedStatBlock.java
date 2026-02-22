package mmorpg.framework.spec;

import java.util.EnumMap;
import java.util.Map;

public class UnifiedStatBlock {

    private final EnumMap<PrimaryAttribute, Double> primary = new EnumMap<>(PrimaryAttribute.class);
    private final EnumMap<SecondaryStat, Double> secondary = new EnumMap<>(SecondaryStat.class);
    private final EnumMap<CombatStat, Double> combat = new EnumMap<>(CombatStat.class);
    private final EnumMap<ResourceType, Double> resources = new EnumMap<>(ResourceType.class);
    private final EnumMap<DamageType, Double> flatResist = new EnumMap<>(DamageType.class);
    private final EnumMap<DamageType, Double> percentResist = new EnumMap<>(DamageType.class);
    private final EnumMap<DamageType, Double> vulnerability = new EnumMap<>(DamageType.class);

    public UnifiedStatBlock() {
        for (PrimaryAttribute key : PrimaryAttribute.values()) {
            primary.put(key, 0.0);
        }
        for (SecondaryStat key : SecondaryStat.values()) {
            secondary.put(key, 0.0);
        }
        for (CombatStat key : CombatStat.values()) {
            combat.put(key, 0.0);
        }
        for (ResourceType key : ResourceType.values()) {
            resources.put(key, 0.0);
        }
        for (DamageType key : DamageType.values()) {
            flatResist.put(key, 0.0);
            percentResist.put(key, 0.0);
            vulnerability.put(key, 0.0);
        }
    }

    public double get(PrimaryAttribute key) { return primary.getOrDefault(key, 0.0); }
    public double get(SecondaryStat key) { return secondary.getOrDefault(key, 0.0); }
    public double get(CombatStat key) { return combat.getOrDefault(key, 0.0); }
    public double get(ResourceType key) { return resources.getOrDefault(key, 0.0); }

    public UnifiedStatBlock set(PrimaryAttribute key, double value) { primary.put(key, value); return this; }
    public UnifiedStatBlock set(SecondaryStat key, double value) { secondary.put(key, value); return this; }
    public UnifiedStatBlock set(CombatStat key, double value) { combat.put(key, value); return this; }
    public UnifiedStatBlock set(ResourceType key, double value) { resources.put(key, value); return this; }

    public UnifiedStatBlock setResistFlat(DamageType type, double value) { flatResist.put(type, value); return this; }
    public UnifiedStatBlock setResistPercent(DamageType type, double value) { percentResist.put(type, value); return this; }
    public UnifiedStatBlock setVulnerability(DamageType type, double value) { vulnerability.put(type, value); return this; }

    public double resistFlat(DamageType type) { return flatResist.getOrDefault(type, 0.0); }
    public double resistPercent(DamageType type) { return percentResist.getOrDefault(type, 0.0); }
    public double vulnerability(DamageType type) { return vulnerability.getOrDefault(type, 0.0); }

    public void deriveSecondaryFromPrimary() {
        set(SecondaryStat.PHYSICAL_POWER, get(PrimaryAttribute.STR) * 1.8);
        set(SecondaryStat.ATTACK_SPEED, get(PrimaryAttribute.DEX) * 0.10);
        set(SecondaryStat.ACCURACY, get(PrimaryAttribute.DEX) * 1.2);
        set(SecondaryStat.EVASION, get(PrimaryAttribute.DEX) * 0.9);
        set(SecondaryStat.SPELL_POWER, get(PrimaryAttribute.INT) * 1.9);
        set(SecondaryStat.MANA_EFFICIENCY, get(PrimaryAttribute.WIS) * 0.30);
        set(SecondaryStat.MAX_HP, get(PrimaryAttribute.VIT) * 15 + get(PrimaryAttribute.CON) * 10);
        set(SecondaryStat.HP_REGEN, get(PrimaryAttribute.SPI) * 0.35);
        set(SecondaryStat.DAMAGE_REDUCTION_PERCENT, Math.min(70, get(PrimaryAttribute.CON) * 0.35));
    }

    public void deriveCombatFromSecondary() {
        set(CombatStat.BASE_DAMAGE, get(SecondaryStat.PHYSICAL_POWER) + get(SecondaryStat.SPELL_POWER) * 0.35);
        set(CombatStat.CRIT_CHANCE, Math.min(70, get(PrimaryAttribute.DEX) * 0.04));
        set(CombatStat.CRIT_DAMAGE, 150 + get(SecondaryStat.CRIT_DAMAGE_PERCENT));
        set(CombatStat.ARMOR, get(PrimaryAttribute.CON) * 2 + get(SecondaryStat.SHIELD_STRENGTH) * 0.4);
        set(CombatStat.MAGIC_RESISTANCE, get(PrimaryAttribute.WIS) * 1.2 + get(PrimaryAttribute.WIL) * 1.4);
        set(CombatStat.DAMAGE_MITIGATION_PERCENT, Math.min(70, get(SecondaryStat.DAMAGE_REDUCTION_PERCENT)));
        set(CombatStat.DODGE_CHANCE, Math.min(60, get(SecondaryStat.EVASION) * 0.07));
        set(CombatStat.COOLDOWN_REDUCTION, Math.min(60, get(PrimaryAttribute.SPI) * 0.25));
        set(CombatStat.ATTACK_SPEED, get(SecondaryStat.ATTACK_SPEED));
        set(CombatStat.CAST_SPEED, get(SecondaryStat.CAST_SPEED));
        set(CombatStat.MOVEMENT_SPEED, 100 + get(PrimaryAttribute.DEX) * 0.4);
    }
}
