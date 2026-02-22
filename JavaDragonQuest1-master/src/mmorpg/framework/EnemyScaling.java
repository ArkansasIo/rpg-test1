package mmorpg.framework;

public final class EnemyScaling {

    private EnemyScaling() { }

    public static double scaledHp(double baseHp, int levelDiff, EnemyRank rank
            , int players, double difficultyMultiplier) {

        return CombatMath.bossHp(baseHp * rank.getMultiplier(), levelDiff
                , CombatMath.partySizeMultiplier(players), difficultyMultiplier);
    }

    public static double scaledDamage(double baseDamage, int levelDiff, EnemyRank rank
            , double difficultyMultiplier) {

        return CombatMath.bossDamage(baseDamage * Math.sqrt(rank.getMultiplier())
                , levelDiff, difficultyMultiplier);
    }
}
