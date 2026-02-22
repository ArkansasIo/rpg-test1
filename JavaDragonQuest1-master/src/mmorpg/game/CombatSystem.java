package mmorpg.game;

import mmorpg.entities.Player;
import mmorpg.entities.Monster;
import java.util.Random;
import java.util.Scanner;

public class CombatSystem {
    private static final Random random = new Random();

    public static boolean engage(Player player, Monster monster) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("A wild " + monster.getName() + " appears!");
        while (player.isAlive() && monster.isAlive()) {
            System.out.println("\nPlayer HP: " + player.getHp() + " | " + monster.getName() + " HP: " + monster.getHp());
            System.out.println("Choose action: [A]ttack or [R]un");
            String input = scanner.nextLine().trim().toLowerCase();
            if (input.equals("r")) {
                if (random.nextBoolean()) {
                    System.out.println("You successfully ran away!");
                    return false;
                } else {
                    System.out.println("Failed to run away!");
                }
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
