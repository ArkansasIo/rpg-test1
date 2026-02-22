package mmorpg.framework;

public final class ThreatSystem {

    private ThreatSystem() { }

    public static double threat(double damage, double healing
            , double damageThreatMult, double healingThreatMult
            , double flatThreat, double tauntBonus) {

        return (damage * damageThreatMult)
                + (healing * healingThreatMult)
                + flatThreat
                + tauntBonus;
    }
}
