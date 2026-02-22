package mmorpg.framework;

public final class CombatResolver {

    public static final class HitResult {
        private final double rawDamage;
        private final double mitigatedDamage;
        private final double critChance;
        private final double mitigation;

        public HitResult(double rawDamage, double mitigatedDamage
                , double critChance, double mitigation) {
            this.rawDamage = rawDamage;
            this.mitigatedDamage = mitigatedDamage;
            this.critChance = critChance;
            this.mitigation = mitigation;
        }

        public double getRawDamage() {
            return rawDamage;
        }

        public double getMitigatedDamage() {
            return mitigatedDamage;
        }

        public double getCritChance() {
            return critChance;
        }

        public double getMitigation() {
            return mitigation;
        }
    }

    private CombatResolver() { }

    public static HitResult resolveBasicHit(
            double attackerStrength,
            double attackerDexterity,
            double attackerCritRating,
            double weaponMultiplier,
            double defenderArmor,
            double defenderResistPercent,
            double vulnerabilityPercent,
            double level,
            PvpPveContext context) {

        double raw = CombatMath.physicalDamage(attackerStrength, weaponMultiplier)
                * BalanceRules.damageMultiplier(context);

        double crit = CombatMath.critChance(attackerDexterity, attackerCritRating)
                * BalanceRules.critChanceMultiplier(context);
        crit = CombatMath.clamp(crit, 0.0, BalanceRules.critChanceCap(context));

        double mitigation = CombatMath.mitigationFromArmor(
                defenderArmor * BalanceRules.armorMultiplier(context), level);
        mitigation = CombatMath.clamp(mitigation, 0.0, BalanceRules.damageReductionCap());

        double damageTaken = CombatMath.damageTaken(raw, mitigation, defenderResistPercent, vulnerabilityPercent);
        return new HitResult(raw, damageTaken, crit, mitigation);
    }
}
