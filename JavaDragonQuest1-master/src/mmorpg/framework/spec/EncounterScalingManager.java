package mmorpg.framework.spec;

public final class EncounterScalingManager {

    private EncounterScalingManager() { }

    public static double partyMultiplier(int players) {
        int p = Math.max(1, players);
        return 1.0 + (p - 1) * 0.65;
    }

    public static double bossHp(double baseHp, int levelDiff, int players, double difficultyMult) {
        return baseHp * (1.0 + levelDiff * 0.18) * partyMultiplier(players) * difficultyMult;
    }

    public static double bossDamage(double baseDamage, int levelDiff, int players, double difficultyMult) {
        return baseDamage * (1.0 + levelDiff * 0.12) * Math.pow(partyMultiplier(players), 0.6) * difficultyMult;
    }

    public static double encounterPowerBudget(double playerEffectivePower, double difficultyFactor) {
        return playerEffectivePower * difficultyFactor;
    }
}
