package mmorpg.framework;

public final class BalanceRules {

    private BalanceRules() { }

    public static double damageMultiplier(PvpPveContext context) {
        return context == PvpPveContext.PVE ? 1.0 : 0.70;
    }

    public static double critChanceMultiplier(PvpPveContext context) {
        return context == PvpPveContext.PVE ? 1.0 : 0.50;
    }

    public static double critDamageMultiplier(PvpPveContext context) {
        return context == PvpPveContext.PVE ? 1.0 : 0.60;
    }

    public static double healingMultiplier(PvpPveContext context) {
        return context == PvpPveContext.PVE ? 1.0 : 0.65;
    }

    public static double cooldownReductionMultiplier(PvpPveContext context) {
        return context == PvpPveContext.PVE ? 1.0 : 0.50;
    }

    public static double armorMultiplier(PvpPveContext context) {
        return context == PvpPveContext.PVE ? 1.0 : 1.10;
    }

    public static double critChanceCap(PvpPveContext context) {
        return context == PvpPveContext.PVE ? 0.70 : 0.40;
    }

    public static double cooldownReductionCap(PvpPveContext context) {
        return context == PvpPveContext.PVE ? 0.60 : 0.35;
    }

    public static double damageReductionCap() {
        return 0.70;
    }
}
