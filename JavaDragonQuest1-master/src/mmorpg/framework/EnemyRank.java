package mmorpg.framework;

public enum EnemyRank {
    NORMAL(1.0),
    VETERAN(1.2),
    ELITE(1.8),
    CHAMPION(2.8),
    BOSS(5.0),
    MYTHIC(10.0),
    ASCENDED(18.0);

    private final double multiplier;

    EnemyRank(double multiplier) {
        this.multiplier = multiplier;
    }

    public double getMultiplier() {
        return multiplier;
    }
}
