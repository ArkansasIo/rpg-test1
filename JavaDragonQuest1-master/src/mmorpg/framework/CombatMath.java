package mmorpg.framework;

public final class CombatMath {

    private CombatMath() { }

    public static double maxHp(double vit, double con, double gearHp) {
        return (vit * 15.0) + (con * 10.0) + gearHp;
    }

    public static double physicalDamage(double str, double weaponMultiplier) {
        return (str * 2.5) * weaponMultiplier;
    }

    public static double critChance(double dex, double gearCrit) {
        return (dex * 0.04) + gearCrit;
    }

    public static double mitigationFromArmor(double armor, double level) {
        double k = 50.0 + (level * 2.0);
        return armor / (armor + k);
    }

    public static double damageTaken(double incomingDamage
            , double mitigation, double resistPercent, double vulnerabilityPercent) {

        double value = incomingDamage;
        value = value * (1.0 - clamp(mitigation, 0.0, 0.95));
        value = value * (1.0 - clamp(resistPercent, 0.0, 0.95));
        value = value * (1.0 + clamp(vulnerabilityPercent, 0.0, 5.0));
        return Math.max(0.0, value);
    }

    public static double pveWeaponPower(double baseDamage, double weaponSpeedFactor
            , double attributeScaling, double elementalScaling, double pveMultiplier) {

        return (baseDamage * weaponSpeedFactor + attributeScaling + elementalScaling) * pveMultiplier;
    }

    public static double pvpWeaponPower(double normalizedBaseDamage
            , double attributeScaling, double pvpStatScale, double pvpDampening) {

        return (normalizedBaseDamage + (attributeScaling * pvpStatScale)) * pvpDampening;
    }

    public static double bossHp(double baseHp, double levelDiff
            , double partySizeMultiplier, double difficultyMultiplier) {

        return baseHp * (1.0 + (levelDiff * 0.18)) * partySizeMultiplier * difficultyMultiplier;
    }

    public static double bossDamage(double baseDamage, double levelDiff, double difficultyMultiplier) {
        return baseDamage * (1.0 + (levelDiff * 0.12)) * difficultyMultiplier;
    }

    public static double partySizeMultiplier(int players) {
        if (players <= 1) {
            return 1.0;
        }
        return 1.0 + ((players - 1) * 0.65);
    }

    public static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
}
