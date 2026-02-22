package dq1.core.rpg;

public enum Rarity {
    COMMON(1.0),
    UNCOMMON(1.1),
    RARE(1.25),
    EPIC(1.5),
    LEGENDARY(1.8),
    MYTHIC(2.2);

    private final double statMultiplier;

    Rarity(double statMultiplier) {
        this.statMultiplier = statMultiplier;
    }

    public double getStatMultiplier() {
        return statMultiplier;
    }
}
