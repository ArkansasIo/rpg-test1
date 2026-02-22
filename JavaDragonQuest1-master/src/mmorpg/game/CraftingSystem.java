package mmorpg.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class CraftingSystem {

    public enum Profession {
        ALCHEMY,
        BLACKSMITHING,
        ENCHANTING
    }

    public static final class Recipe {
        public final int id;
        public final String name;
        public final Profession profession;
        public final int requiredLevel;
        public final Map<String, Integer> materials;
        public final String product;

        public Recipe(int id, String name, Profession profession, int requiredLevel,
                Map<String, Integer> materials, String product) {
            this.id = id;
            this.name = name;
            this.profession = profession;
            this.requiredLevel = requiredLevel;
            this.materials = Collections.unmodifiableMap(new LinkedHashMap<>(materials));
            this.product = product;
        }
    }

    public static final class CraftResult {
        public final boolean success;
        public final String message;

        public CraftResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
    }

    private static final Map<Profession, Integer> professionLevels = new EnumMap<>(Profession.class);
    private static final Map<String, Integer> materials = new LinkedHashMap<>();
    private static final List<Recipe> recipes = new ArrayList<>();
    private static boolean initialized;

    private CraftingSystem() {
    }

    public static void initializeDefaults() {
        if (initialized) {
            return;
        }
        initialized = true;
        for (Profession profession : Profession.values()) {
            professionLevels.put(profession, 1);
        }
        materials.clear();
        materials.put("Herb", 8);
        materials.put("Crystal Dust", 6);
        materials.put("Iron Ore", 10);
        materials.put("Leather", 6);
        materials.put("Arcane Shard", 2);

        recipes.clear();
        recipes.add(new Recipe(101, "Minor Healing Potion", Profession.ALCHEMY, 1,
                mapOf("Herb", 2, "Crystal Dust", 1), "Potion: Minor Heal"));
        recipes.add(new Recipe(102, "Mana Tonic", Profession.ALCHEMY, 2,
                mapOf("Herb", 1, "Arcane Shard", 1), "Potion: Mana Tonic"));
        recipes.add(new Recipe(201, "Iron Longsword", Profession.BLACKSMITHING, 1,
                mapOf("Iron Ore", 4, "Leather", 1), "Weapon: Iron Longsword"));
        recipes.add(new Recipe(202, "Guardian Helm", Profession.BLACKSMITHING, 2,
                mapOf("Iron Ore", 5, "Crystal Dust", 1), "Armor: Guardian Helm"));
        recipes.add(new Recipe(301, "Lesser Weapon Rune", Profession.ENCHANTING, 1,
                mapOf("Crystal Dust", 2, "Arcane Shard", 1), "Rune: Lesser Weapon"));
    }

    public static Map<Profession, Integer> getProfessionLevels() {
        initializeDefaults();
        return Collections.unmodifiableMap(professionLevels);
    }

    public static void setProfessionLevel(Profession profession, int level) {
        initializeDefaults();
        professionLevels.put(profession, Math.max(1, Math.min(level, 300)));
    }

    public static Map<String, Integer> getMaterials() {
        initializeDefaults();
        return Collections.unmodifiableMap(materials);
    }

    public static List<Recipe> getRecipes() {
        initializeDefaults();
        return Collections.unmodifiableList(recipes);
    }

    public static List<Recipe> getRecipesFor(Profession profession) {
        initializeDefaults();
        List<Recipe> results = new ArrayList<>();
        for (Recipe recipe : recipes) {
            if (recipe.profession == profession) {
                results.add(recipe);
            }
        }
        return results;
    }

    public static CraftResult craftByRecipeId(int recipeId) {
        initializeDefaults();
        Recipe recipe = null;
        for (Recipe r : recipes) {
            if (r.id == recipeId) {
                recipe = r;
                break;
            }
        }
        if (recipe == null) {
            return new CraftResult(false, "Recipe not found.");
        }
        int professionLevel = professionLevels.getOrDefault(recipe.profession, 1);
        if (professionLevel < recipe.requiredLevel) {
            return new CraftResult(false, "Profession level too low.");
        }
        for (Map.Entry<String, Integer> req : recipe.materials.entrySet()) {
            int available = materials.getOrDefault(req.getKey(), 0);
            if (available < req.getValue()) {
                return new CraftResult(false, "Missing material: " + req.getKey());
            }
        }
        for (Map.Entry<String, Integer> req : recipe.materials.entrySet()) {
            materials.put(req.getKey(), materials.get(req.getKey()) - req.getValue());
        }
        return new CraftResult(true, "Crafted " + recipe.product + ".");
    }

    public static List<String> buildSummaryLines() {
        initializeDefaults();
        List<String> lines = new ArrayList<>();
        lines.add("Professions:");
        for (Profession profession : Profession.values()) {
            lines.add("- " + profession.name() + " Lv." + professionLevels.getOrDefault(profession, 1));
        }
        lines.add("Materials:");
        int shown = 0;
        for (Map.Entry<String, Integer> entry : materials.entrySet()) {
            lines.add("- " + entry.getKey() + ": " + entry.getValue());
            shown++;
            if (shown >= 4) {
                break;
            }
        }
        lines.add("Recipes: " + recipes.size());
        return lines;
    }

    private static Map<String, Integer> mapOf(String k1, int v1, String k2, int v2) {
        Map<String, Integer> map = new LinkedHashMap<>();
        map.put(k1, v1);
        map.put(k2, v2);
        return map;
    }
}
