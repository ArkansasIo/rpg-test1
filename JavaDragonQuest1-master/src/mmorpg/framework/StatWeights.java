package mmorpg.framework;

import java.util.EnumMap;
import java.util.Map;

public final class StatWeights {

    private final EnumMap<SecondaryStat, Double> weights
            = new EnumMap<>(SecondaryStat.class);

    private StatWeights() { }

    public static StatWeights createDefault(PvpPveContext context) {
        StatWeights result = new StatWeights();
        for (SecondaryStat stat : SecondaryStat.values()) {
            result.weights.put(stat, 1.0);
        }

        result.weights.put(SecondaryStat.CRIT_CHANCE, context == PvpPveContext.PVE ? 2.5 : 1.25);
        result.weights.put(SecondaryStat.CRIT_DAMAGE, context == PvpPveContext.PVE ? 1.75 : 1.1);
        result.weights.put(SecondaryStat.ATTACK_SPEED, context == PvpPveContext.PVE ? 2.0 : 1.3);
        result.weights.put(SecondaryStat.CAST_SPEED, context == PvpPveContext.PVE ? 1.8 : 1.2);
        result.weights.put(SecondaryStat.DAMAGE_REDUCTION, context == PvpPveContext.PVE ? 1.2 : 2.0);
        result.weights.put(SecondaryStat.TENACITY, context == PvpPveContext.PVE ? 1.1 : 2.2);
        result.weights.put(SecondaryStat.HP_REGEN, context == PvpPveContext.PVE ? 1.0 : 0.7);
        result.weights.put(SecondaryStat.MP_REGEN, context == PvpPveContext.PVE ? 1.0 : 0.7);

        return result;
    }

    public double get(SecondaryStat stat) {
        return weights.getOrDefault(stat, 1.0);
    }

    public Map<SecondaryStat, Double> asMap() {
        return weights;
    }
}
