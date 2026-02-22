package mmorpg.framework;

public final class EncounterBudget {

    private EncounterBudget() { }

    public static double calculate(double totalPlayerEffectivePower, double difficultyFactor) {
        return totalPlayerEffectivePower * difficultyFactor;
    }

    public static boolean abilityExceedsBudget(double abilityPower
            , double budget, boolean telegraphed, boolean hasCounterplay) {

        if (abilityPower <= budget) {
            return false;
        }
        return !(telegraphed && hasCounterplay);
    }
}
