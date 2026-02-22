package dq1.core;

import static dq1.core.Player.getLV;
import java.awt.event.KeyEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Debug class.
 * 
 * @author Leonardo Ono (ono.leo80@gmail.com)
 */
public class Debug {

    public static void update() {
        // All debug key actions are handled here
        if (Input.isKeyJustPressed(KeyEvent.VK_2)) {
            Script.setGlobalValue("##game_state_dragon_lord_defeated", 0);
            System.out.println("dragon lord NOT defeated");
        }
        if (Input.isKeyJustPressed(KeyEvent.VK_3)) {
            Script.setGlobalValue("##game_state_princess_rescued", 0);
            System.out.println("princess not rescued");
        }
        if (Input.isKeyJustPressed(KeyEvent.VK_4)) {
            Script.setGlobalValue("##game_state_princess_rescued", 1);
            System.out.println("princess rescued / player carrying princess");
            Player.setState("princess");
            Player.changeDirection("down");
        }
        if (Input.isKeyJustPressed(KeyEvent.VK_5)) {
            Script.setGlobalValue("##game_state_princess_rescued", 2);
            System.out.println("princess rescued already in tantegel");
        }
        if (Input.isKeyJustPressed(KeyEvent.VK_L)) {
            try {
                Game.teleport("charlock_castle", 294, 35, "up", 1, "dungeon", 0, 0, 0, 1);
            } catch (Exception ex) {
                Logger.getLogger(Debug.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (Input.isKeyJustPressed(KeyEvent.VK_W)) {
            try {
                Game.teleport("world", 77, 105, "down", 1, "world", 0, 0, 0, 1);
            } catch (Exception ex) {
                Logger.getLogger(Debug.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (Input.isKeyJustPressed(KeyEvent.VK_U)) {
            System.out.println("level up!");
            Player.levelUp();
            int currentLevel = getLV();
            PlayerLevel playerLevel = Resource.getPlayerLevel(currentLevel);
            PlayerLevel playerLevelNext = Resource.getPlayerLevel(currentLevel + 1);
            int expDif = playerLevelNext.getXP() - playerLevel.getXP();
            Script.setGlobalValue("##player_e", playerLevel.getXP() + (int) (expDif * Math.random()));
            Dialog.close();
        }
        if (Input.isKeyJustPressed(KeyEvent.VK_E)) {
            boolean enabled = Game.getCurrentMap().isEnemiesEncounterEnabled();
            Game.getCurrentMap().setEnemiesEncounterEnabled(!enabled);
            System.out.println("enemies enabled = " + (!enabled));
            Player.equipPlayer(22);
        }
        if (Input.isKeyJustPressed(KeyEvent.VK_9)) {
            Player.equipPlayer(14);
        }
        if (Input.isKeyJustPressed(KeyEvent.VK_A)) {
            Inventory.addItem(42);
            Inventory.addItem(43);
            Inventory.addItem(46);
            Inventory.addItem(46);
            Inventory.addItem(46);
        }
        if (Input.isKeyJustPressed(KeyEvent.VK_S)) {
            int lv = 30;
            int str = 25;
            int agi = 25;
            int hp = 5;
            int mp = 50;
            int hpMax = 50;
            int mpMax = 50;
            int gp = 999;
            int xp = 0;
            Script.setGlobalValue("##player_str", str);
            Script.setGlobalValue("##player_agi", agi);
            Script.setGlobalValue("##player_max_hp", hpMax);
            Script.setGlobalValue("##player_max_mp", mpMax);
            Script.setGlobalValue("##player_lv", lv);
            Script.setGlobalValue("##player_hp", hp);
            Script.setGlobalValue("##player_mp", mp);
            Script.setGlobalValue("##player_g", gp);
            Script.setGlobalValue("##player_e", xp);
        }
        if (Input.isKeyJustPressed(KeyEvent.VK_M)) {
            Script.setGlobalValue("##player_mp", 999);
        }
        if (Input.isKeyJustPressed(KeyEvent.VK_G)) {
            Player.incG(100);
        }
        if (Input.isKeyJustPressed(KeyEvent.VK_D)) {
            Script.setGlobalValue("##player_g", 1000);
            Script.setGlobalValue("##game_state_dragon_lord_defeated", 0);
            Script.setGlobalValue("##game_state_princess_rescued", 0);
            Script.setGlobalValue("##player_weapon_id", -1);
            Script.setGlobalValue("##player_armor_id", -1);
            Script.setGlobalValue("##player_shield_id", -1);
        }
    }
}
