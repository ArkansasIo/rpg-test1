package mmorpg.framework.spec;

import java.util.EnumMap;
import java.util.Map;

public final class PvePvpStatChannels {

    public enum Context {
        PVE, PVP
    }

    private static final EnumMap<CombatStat, Double> PVE_MULT = new EnumMap<>(CombatStat.class);
    private static final EnumMap<CombatStat, Double> PVP_MULT = new EnumMap<>(CombatStat.class);

    static {
        for (CombatStat stat : CombatStat.values()) {
            PVE_MULT.put(stat, 1.0);
            PVP_MULT.put(stat, 1.0);
        }
        PVP_MULT.put(CombatStat.BASE_DAMAGE, 0.70);
        PVP_MULT.put(CombatStat.CRIT_CHANCE, 0.50);
        PVP_MULT.put(CombatStat.CRIT_DAMAGE, 0.60);
        PVP_MULT.put(CombatStat.COOLDOWN_REDUCTION, 0.50);
        PVP_MULT.put(CombatStat.DAMAGE_MITIGATION_PERCENT, 1.10);
    }

    private PvePvpStatChannels() { }

    public static double applyMultiplier(CombatStat stat, double value, Context context) {
        Map<CombatStat, Double> map = context == Context.PVE ? PVE_MULT : PVP_MULT;
        return value * map.getOrDefault(stat, 1.0);
    }

    public static double cap(CombatStat stat, double value, Context context) {
        if (stat == CombatStat.CRIT_CHANCE) {
            return Math.min(value, context == Context.PVE ? 70.0 : 40.0);
        }
        if (stat == CombatStat.COOLDOWN_REDUCTION) {
            return Math.min(value, context == Context.PVE ? 60.0 : 35.0);
        }
        if (stat == CombatStat.DAMAGE_MITIGATION_PERCENT) {
            return Math.min(value, 70.0);
        }
        return value;
    }
}
