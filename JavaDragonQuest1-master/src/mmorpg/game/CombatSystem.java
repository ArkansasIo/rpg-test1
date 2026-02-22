package mmorpg.game;

import mmorpg.entities.Player;
import mmorpg.entities.Monster;
import java.util.Random;
import java.util.Scanner;

public class CombatSystem {
    private static final Random random = new Random();

    public static boolean engage(Player player, Monster monster) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("A wild " + monster.getName() + " (Lv." + monster.getLevel() + ", " + monster.getType() + ") appears!");
        boolean isBoss = monster instanceof Boss;
        if (isBoss) {
            Boss boss = (Boss) monster;
            System.out.println("Boss Type: " + boss.getBossTypeName() + " | Class: " + boss.getBossClassName());
            if (boss.isWorldBoss()) {
                System.out.println("World Boss Encounter!");
            }
        }
        while (player.isAlive() && monster.isAlive()) {
            System.out.println("\nPlayer HP: " + player.getHp() + " | Mana: " + player.getMana() + " | " + monster.getName() + " HP: " + monster.getHp());
            System.out.println("Choose action: [A]ttack, [S]pell, [I]tem, [R]un");
            String input = scanner.nextLine().trim().toLowerCase();
            if (input.equals("r")) {
                if (random.nextBoolean()) {
                    System.out.println("You successfully ran away!");
                    return false;
                } else {
                    System.out.println("Failed to run away!");
                }
                continue;
            }
            if (input.equals("i")) {
                if (player.getInventory().isEmpty()) {
                    System.out.println("No items to use.");
                } else {
                    System.out.println("Choose item to use:");
                    for (int idx = 0; idx < player.getInventory().size(); idx++) {
                        Item item = player.getInventory().get(idx);
                        System.out.println("[" + idx + "] " + item.getName() + " (Lv." + item.getLevel() + ")");
                    }
                    int itemIdx = -1;
                    try { itemIdx = Integer.parseInt(scanner.nextLine()); } catch (Exception ignored) {}
                    if (itemIdx >= 0 && itemIdx < player.getInventory().size()) {
                        Item item = player.getInventory().get(itemIdx);
                        System.out.println("Used " + item.getName() + ".");
                        // Example: heal or buff
                        player.heal(item.getLevel());
                    } else {
                        System.out.println("Invalid item selection.");
                    }
                }
                continue;
            }
            if (input.equals("s")) {
                System.out.println("Choose spell to cast:");
                // Stub: list spells
                Spell spell = new Spell();
                spell.setLevel(player.getLevel());
                spell.setSchool(random.nextInt(800));
                spell.setMagicClass(random.nextInt(800));
                System.out.println("Casting " + spell.getSchoolName() + " / " + spell.getMagicClassName() + " (Lv." + spell.getLevel() + ")");
                int spellDmg = spell.getLevel() + random.nextInt(10);
                monster.takeDamage(spellDmg);
                player.useMana(5);
                System.out.println("Spell hits " + monster.getName() + " for " + spellDmg + " damage.");
                if (!monster.isAlive()) {
                    System.out.println("You defeated the " + monster.getName() + "!");
                    return true;
                }
                continue;
            }
            // Player attacks
            int playerDmg = Math.max(1, player.getAttack() - monster.getDefense() + random.nextInt(3));
            monster.takeDamage(playerDmg);
            System.out.println("You hit the " + monster.getName() + " for " + playerDmg + " damage.");
            if (!monster.isAlive()) {
                System.out.println("You defeated the " + monster.getName() + "!");
                return true;
            }
            // Monster attacks
            int monsterDmg = Math.max(1, monster.getAttack() - player.getDefense() + random.nextInt(3));
            player.takeDamage(monsterDmg);
            System.out.println("The " + monster.getName() + " hits you for " + monsterDmg + " damage.");
            if (!player.isAlive()) {
                System.out.println("You were defeated by the " + monster.getName() + "...");
                return false;
            }
        }
        return player.isAlive();
    }
}
