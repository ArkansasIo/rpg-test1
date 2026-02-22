package dq1.core;

import static dq1.core.Game.State.*;
import dq1.core.rpg.BuffDebuffManager;
import dq1.core.rpg.CharacterClass;
import dq1.core.rpg.EquipmentSlot;
import dq1.core.rpg.InventorySystem;
import dq1.core.rpg.PlayerRpgProfile;
import dq1.core.rpg.RpgActionResult;
import dq1.core.rpg.RpgActionType;
import dq1.core.rpg.RpgRuntimeService;
import dq1.core.rpg.RpgSystems;
import dq1.core.wowui.WowPanelId;
import dq1.core.wowui.WowPanelModel;
import dq1.core.wowui.WowUiFramework;
import dq1.core.Script.ScriptCommand;
import static dq1.core.Settings.*;
import dq1.core.TileMap.Area;
import mmorpg.entities.FantasyEntityCatalog;
import mmorpg.entities.FantasyEntityGroup;
import mmorpg.entities.FantasyEntityProfile;
import mmorpg.game.FeatureRegistry;
import mmorpg.ui.DiabloInventoryFrame;
import mmorpg.ui.WowMapCatalogFrame;
import mmorpg.ui.WowUiFrame;
import mmorpg.world.WoWTileMapSystem;
import java.awt.Graphics2D;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;

/**
 * Game class.
 * 
 * @author Leonardo Ono (ono.leo80@gmail.com)
 */
public class Game {
        public static void main(String[] args) {
            try {
                start();
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
    
    public static enum State { OL_PRESENTS, TITLE, MAP, CHANGE_MAP }
    private static final WowUiFramework WOW_UI = WowUiFramework.createDefault();
    
    private static Properties TEXTS;
    
    private static boolean running;
    private static TileMap currentMap;
    private static State state = State.OL_PRESENTS;
    private static TileMap newMap;
    private static boolean newMapUseFadeEffect;
    private static int playerNewRow;
    private static int playerNewCol;
    private static String playerNewDirection;
    private static boolean newMapRepelHasNoEffect;
    private static boolean newMapResetRepel;
    private static boolean newMapResetLight;
    private static boolean newMapIsDark;
    private static String newMapMusicId;
    private static boolean newClearLocalVars;
    private static Animation titleShineAnimation;
    
    // Removed static block for TEXTS initialization

    public static Properties getTexts() {
        return TEXTS;
    }

    public static String getText(String varName) {
        return TEXTS.getProperty(varName);
    }

    public static State getState() {
        return state;
    }

    public static void setState(State state) {
        Game.state = state;
    }

    public static TileMap getCurrentMap() {
        return currentMap;
    }

    private static boolean isSkipRequested() {
        return Input.isKeyJustPressed(KEY_CONFIRM)
                || Input.isKeyJustPressed(KEY_CANCEL);
    }

    private static boolean waitSkippable(int millis) {
        long endTime = System.currentTimeMillis() + millis;
        while (System.currentTimeMillis() < endTime) {
            if (isSkipRequested()) {
                return true;
            }
            View.refresh();
            sleep(1000 / 60);
        }
        return false;
    }

    public static void start() throws Exception {
                // Initialize game window and canvas
                javax.swing.JFrame frame = new javax.swing.JFrame("Dragon Quest 1");
                frame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
                frame.setResizable(false);
                frame.add(View.getCanvas());
                frame.pack();
                frame.setSize(Settings.VIEWPORT_WIDTH, Settings.VIEWPORT_HEIGHT);
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
                // Wait for canvas and graphics to initialize
                try { Thread.sleep(500); } catch (InterruptedException ignored) {}
            TEXTS = Resource.getTexts(RES_TEXTS_INF);
        Resource.loadMusics(RES_MUSICS_INF);
        Resource.loadEnemies(RES_ENEMIES_INF);
        Resource.loadItems(RES_ITEMS_INF);
        Resource.loadSpells(RES_SPELLS_INF);
        Resource.loadPlayerLevels(RES_PLAYER_LEVELS_INF);
        
        Audio.start();
        Player.start();
        Inventory.start();
        Quest.initializeWoWStoryIfNeeded();
        RpgSystems.bootstrap();

        //testStartSpecificMap();
        //testStartLoadingSavedGame();
        //testChangePlayerStatus();

        Script.registerClassStaticCommands(Audio.class);
        Script.registerClassStaticCommands(Game.class);
        Script.registerClassStaticCommands(View.class);
        Script.registerClassStaticCommands(Dialog.class);
        Script.registerClassStaticCommands(Player.class);
        Script.registerClassStaticCommands(Inventory.class);
        Script.registerClassStaticCommands(Shop.class);
        Script.registerClassStaticCommands(Battle.class);

        titleShineAnimation = new Animation(
            Resource.getImage("title_shine"), 12, 1, 500);

        titleShineAnimation.createAnimation("shine"
            , new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11,
                     11, 11, 11, 11, 11, 11, 11, 11, 11, 11,
                     11, 11, 11, 11, 11, 11, 11, 11, 11, 11 });

        View.start(); // Initialize graphics context before starting game logic
        running = true;
        new Thread(new LogicThread()).start();
    }
    
    private static void testChangePlayerStatus() {
        Script.setGlobalValue("##player_lv", 35);
        Script.setGlobalValue("##player_hp", 999);
        Script.setGlobalValue("##player_mp", 999);
        Script.setGlobalValue("##player_g", 65535);
        Script.setGlobalValue("##player_e", 6);

        Script.setGlobalValue("##player_str", 500);
        Script.setGlobalValue("##player_agi", 500);
        Script.setGlobalValue("##player_max_hp", 999);
        Script.setGlobalValue("##player_max_mp", 999);
    }
    
    private static void testStartSpecificMap() throws Exception {
        //teleport("tantegel_castle", 65, 16, "up", 1, "tantegel", 0, 1, 1, 1);
        //teleport("rimuldar", 40, 24, "right", 1, "town", 0, 1, 1, 1);
        //teleport("shrine", 47, 16, "right", 1, "tantegel", 0, 1, 1, 1);
        //teleport("charlock_castle", 22, 31, "up", 1, "dungeon", 0, 1, 1, 1);
        //teleport("brecconary", 10, 25, "right", 1, "town", 0, 1, 1, 1);
        //teleport("garinham", 9, 24, "right", 1, "town", 0, 1, 1, 1);
        //teleport("swamp_cave", 12, 12, "right", 1, "town", 0, 1, 1, 1);
        //teleport("kol", 19, 23, "up", 1, "town", 0, 1, 1, 1);
        //teleport("cantlin", 36, 37, "down", 1, "town", 0, 1, 1, 1);
        //teleport("world", 87, 117, "right", 1, "world", 0, 1, 1, 1);
        //teleport("hauksness", 10, 20, "right", 1, "dungeon", 0, 1, 1, 1);
        teleport("world", 77, 103, "down", 1, "world", 0, 1, 1, 1); // cantlin cantlin golem
    }
    
    private static void testStartLoadingSavedGame() throws Exception {
        Map<String, Object> loadedGlobalVars = Script.loadVars(3);
        loadGameInternal(loadedGlobalVars);
    }
    
    private static class LogicThread implements Runnable {

        @Override
        public void run() {
            try {
                while (running) {
                    switch (state) {
                        case OL_PRESENTS: updateOLPresents(); break;
                        case TITLE: updateTitle(); break;
                        case MAP: updateMap(); break;
                        case CHANGE_MAP: updateChangeMap(); break;
                    }
                }
            }
            catch (Exception e) {
                Logger.getLogger(Game.class.getName())
                    .log(Level.SEVERE, null, e);
                System.exit(-1);
            }
        }
        
    }
    
    private static void updateOLPresents() throws Exception {
        if (Settings.SKIP_INTRO_STORY) {
            state = TITLE;
            return;
        }
        OLPresents.reset();
        Graphics2D g = View.getOffscreenGraphics2D(1);
        OLPresents.update();
        OLPresents.draw(g);
        View.refresh();
        if (waitSkippable(3000)) {
            state = TITLE;
            return;
        }
        startTime = System.currentTimeMillis();
        while (OLPresents.update()) {
            if (isSkipRequested()) {
                state = TITLE;
                return;
            }
            OLPresents.draw(g);
            View.refresh();
            sync30fps();
        }
        String presents = getText("@@presents");
        for (int i = 0; i < presents.length(); i++) {
            if (isSkipRequested()) {
                state = TITLE;
                return;
            }
            Dialog.print(1, 12 + i, 19, presents.charAt(i));
            sleep(100);
        }
        if (waitSkippable(3000)) {
            state = TITLE;
            return;
        }
        View.fadeOut();
        state = TITLE;
    }
    
    private static void updateTitle() throws Exception {
        View.showImage(1, "title", 0, 0);
        if (!Settings.SKIP_INTRO_STORY) {
            waitSkippable(2000);
        }
        long introMusicStartTime = System.currentTimeMillis();
        Audio.playMusic("intro");
        View.fadeIn();
        Graphics2D g = View.getOffscreenGraphics2D(1);
        startTime = System.currentTimeMillis();
        while (!Settings.SKIP_INTRO_STORY
                && System.currentTimeMillis() - introMusicStartTime < 10500) {
            if (isSkipRequested()) {
                break;
            }
            titleShineAnimation.update();
            View.showImage(1, "title", 0, 0);
            titleShineAnimation.draw(g, 179, 60);
            View.refresh();
            sync30fps();
        }
        Audio.playSound(Audio.SOUND_SHOW_OPTIONS_MENU);
        View.showImage(1, "title", 0, -240);
        Dialog.print(0, 0, getText("@@title_keyboard_1"));
        Dialog.print(0, 0, "");
        Dialog.print(0, 0, "");
        Dialog.print(0, 0, getText("@@title_keyboard_2"));
        Dialog.print(0, 0, "");
        Dialog.print(0, 2, getText("@@title_keyboard_3"));
        Dialog.print(0, 0, "");
        Dialog.print(0, 1, getText("@@title_keyboard_4"));

        Dialog.close();
        boolean exit = false;
        while (!exit) {
            View.showImage(1, "title", 0, -240);
            String[] options = new String[] {
                "New Game",
                "Continue Game",
                "Load Game 1",
                "Load Game 2",
                "Load Game 3",
                "Load Game 4",
                "Load Game 5",
                "Settings",
                "Quests",
                "Quick Start (Skip Story)",
                "View WoW Map",
                "View WoW Map (Graphical)"
            };
            int option = -1;
            while (option == -1) {
                option = Dialog.showOptionsMenu(
                    10, 19, 18, 9, -1, options
                );
            }
            switch (option) {
                case 0: // New Game
                    resetGameTypePlayersName();
                    boolean optionMenuOk = false;
                    while (!optionMenuOk) {
                        playerNameConfirmed = false;
                        playerNameCanceled = false;
                        exit = startGameTypePlayersName(); 
                        if (exit) {
                            Dialog.fillBox(3, ' ', 19, 14, 29, 15);
                            Dialog.drawBoxBorder(3, 18, 13, 30, 16);
                            Dialog.printText(3, 19, 14, getText("@@title_keyboard_5"));
                            Dialog.printText(3, 19, 15, getText("@@title_keyboard_6"));
                            optionMenuOk = showOptionsMenu();
                            Dialog.fillBox(3, -1, 18, 13, 30, 16);
                            if (optionMenuOk) {
                                Audio.playSound(Audio.SOUND_MENU_CONFIRMED);
                                teleport("tantegel_castle", 65, 16, "up", 1, "tantegel", 0, 1, 1, 1);
                            }
                        } else {
                            optionMenuOk = true;
                        }
                    }
                    break;
                case 1: // Continue Game
                    exit = loadGame();
                    break;
                case 2: // Load Game 1
                case 3: // Load Game 2
                case 4: // Load Game 3
                case 5: // Load Game 4
                case 6: // Load Game 5
                    // Load specific save slot
                    exit = loadGameSlot(option - 1); // slot 1-5
                    break;
                case 7: // Settings
                    showSettingsMenu();
                    break;
                case 8: // Quests
                    showQuestMenu();
                    break;
                case 9: // Quick Start
                    Settings.SKIP_INTRO_STORY = true;
                    Player.setName("HERO");
                    Audio.playSound(Audio.SOUND_MENU_CONFIRMED);
                    teleport("tantegel_castle", 65, 16, "up", 1, "tantegel", 0, 1, 1, 1);
                    exit = true;
                    break;
                case 10: // View WoW Map
                    showWoWMapDialog();
                    break;
                case 11: // View WoW Map (Graphical)
                    showWoWMapGraphical();
                    break;
            }
        }
    }
    
    private static final KeyHandler KEY_HANDLER = new KeyHandler();
    private static final int PLAYER_NAME_MAX_LENGTH = 12;
    private static final char[] PLAYER_NAME = new char[PLAYER_NAME_MAX_LENGTH];
    private static int playerNameCursorIndex = 0;
    private static boolean playerNameConfirmed = false;
    private static boolean playerNameCanceled = false;
    private static int lastNameInputKeyCode = -1;
    private static long lastNameInputTimeMs = 0L;

    private static void resetGameTypePlayersName() {
        playerNameCursorIndex = 0;
        playerNameConfirmed = false;
        playerNameCanceled = false;
        lastNameInputKeyCode = -1;
        lastNameInputTimeMs = 0L;
        Arrays.fill(PLAYER_NAME, ' ');
    }
    
    private static boolean startGameTypePlayersName() throws Exception{
        View.clear(3, "0x00000000");
        
        Dialog.fillBox(3, ' ', 5, 22, 27, 23);
        Dialog.drawBoxBorder(3, 4, 21, 28, 24);
        Dialog.printText(3, 6, 22, "ENTER - Confirm");
        Dialog.printText(3, 6, 23, "  ESC - Cancel");
        
        Dialog.fillBox(3, ' ', 5, 17, 27, 19);
        Dialog.drawBoxBorder(3, 4, 16, 28, 20);
        Dialog.printText(3, 6, 17, "TYPE YOUR NAME:");
        Dialog.printText(3, 6, 18, "(max 12 chars)");
        
        Input.setListener(KEY_HANDLER);
        long blinkTime = System.nanoTime();
        while (!playerNameConfirmed && !playerNameCanceled) {
            synchronized (KEY_HANDLER) {
                for (int i = 0; i < PLAYER_NAME_MAX_LENGTH; i++) {
                    Dialog.print(3, 7 + i, 19, PLAYER_NAME[i]);
                }
                if ((int) ((System.nanoTime() - blinkTime) 
                                            * 0.0000000035) % 2 == 0) {            
                    int cursorCol = 7 + playerNameCursorIndex;
                    if (cursorCol > 18) {
                        cursorCol = 18;
                    }
                    Dialog.print(3, cursorCol, 19, 16);
                }
            }
            Game.sleep(1000 / 60);
        }
        if (playerNameCanceled) {
            View.clear(3, "0x00000000");
            return false;
        }
        else {
            Dialog.fillBox(3, -1, 5, 21, 25, 24);
            // set player's name
            Input.setListener(null);
            Player.setName(new String(PLAYER_NAME));
            Audio.playSound(Audio.SOUND_SHOW_OPTIONS_MENU);
            return true;
        }
    }
    
    private static final String VALID_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789 -_";
    
    private static class KeyHandler extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e) {
            synchronized (KEY_HANDLER) {
                long nowMs = System.currentTimeMillis();
                int keyCode = e.getKeyCode();
                if (keyCode == lastNameInputKeyCode && nowMs - lastNameInputTimeMs < 90) {
                    return;
                }
                lastNameInputKeyCode = keyCode;
                lastNameInputTimeMs = nowMs;

                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    playerNameCanceled = true;
                }
                else if (playerNameCursorIndex > 0 
                        && e.getKeyCode() == KeyEvent.VK_ENTER) {
                    
                    playerNameConfirmed = true;
                }
                else if (playerNameCursorIndex > 0 
                        && e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                    
                    PLAYER_NAME[--playerNameCursorIndex] = ' ';
                }
                else if (VALID_CHARS.indexOf(
                        Character.toUpperCase(e.getKeyChar())) >= 0  
                            && playerNameCursorIndex < PLAYER_NAME_MAX_LENGTH) {
                    
                    PLAYER_NAME[playerNameCursorIndex++] 
                            = Character.toUpperCase(e.getKeyChar());
                }
            }
        }

    }
    
    private static long startTime;
    
    private static void updateMap() throws Exception {
        startTime = System.currentTimeMillis();
        boolean exitMap = false;
        while (!exitMap) {
            // if the player is out of walkable area, 
            // he is automatically teleported to the configured location
            Area mapArea = currentMap.getCurrentArea();
            if (mapArea != null 
                    && Player.getX() % 16 == 0 && Player.getY() % 16 == 0
                    && !mapArea.contains(Player.getX(), Player.getY())){
                
                Audio.playSound(Audio.SOUND_ENTRANCE_OR_STAIRS);
                sleep(250);
                teleport(mapArea.teleportToMapId
                        , mapArea.teleportLocationCol
                        , mapArea.teleportLocationRow
                        , mapArea.teleportPlayerDirection
                        , mapArea.useFadeEffect ? 1 : 0, mapArea.musicId
                        , mapArea.isDark ? 1 : 0
                        , mapArea.repelHasNoEffect ? 1 : 0
                        , mapArea.resetRepel ? 1 : 0
                        , mapArea.resetLight ? 1 : 0);
                return;
            }
            
            if (Player.getX() % 16 == 0 && Player.getY() % 16 == 0) {
                exitMap = currentMap.checkEventTriggered();
            }
            if (exitMap || state != MAP) {
                break;
            }
            int beforeCol = Player.getMapCol();
            int beforeRow = Player.getMapRow();
            Player.update();
            int afterCol = Player.getMapCol();
            int afterRow = Player.getMapRow();
            if (beforeCol != afterCol || beforeRow != afterRow) {
                RpgSystems.getRuntime().nextTurn();
            }
            // play can select "load game" option
            if (state != MAP) {
                break;
            }
            currentMap.update();
            redraw();
            sync30fps();
        }
    }
    
    @ScriptCommand(name = "start_sync_30_fps")
    public static void startSync30fps() {
        startTime = System.currentTimeMillis();
    }
    
    // innacurate but i think it's ok xD ...
    @ScriptCommand(name = "sync_30_fps")
    public static void sync30fps() {
        long endTime = System.currentTimeMillis();
        int waitTime = (int) (33 - (endTime - startTime));
        if (waitTime < 1) {
            waitTime = 1;
        }
        startTime = endTime;
        sleep(waitTime);
    }
    
    @ScriptCommand(name = "force_redraw")
    public static void redraw() {
        Graphics2D g = View.getOffscreenGraphics2D(1);
        currentMap.draw(g);
        if (Player.isVisible()) {
            Player.draw(g);
        }
        View.refresh();
    }
    
    private static void updateChangeMap() throws Exception {
        if (newMapUseFadeEffect) {
            View.fadeOut();
        }
        boolean isSameMap = currentMap == newMap;
        if (currentMap != null) {
            if (isSameMap) {
                currentMap.executeAllEvents("on_map_internal_exit");
            }
            else {
                currentMap.executeAllEvents("on_map_exit");
            }
        }
        
        currentMap = newMap;
        newMap = null;
        Script.setGlobalValue("$$current_map_id", currentMap.getId());
        Script.setGlobalValue("$$current_map_name", currentMap.getName());
        Script.setGlobalValue("$$current_map_music_id", newMapMusicId);
        Script.setGlobalValue("##current_map_is_dark"
                                                , newMapIsDark ? 1 : 0);
        
        Script.setGlobalValue("##current_map_repel_has_no_effect"
                                        , newMapRepelHasNoEffect ? 1 : 0);
        
        if (isSameMap) {
            currentMap.executeAllEvents("on_map_internal_enter");
        }
        else {
            if (newClearLocalVars) {
                currentMap.clearAllLocalVars();
            }
            currentMap.executeAllEvents("on_map_enter");
        }

        // play background music
        if (newMapMusicId != null) {
            if (Audio.getCurrentMusic() == null 
                    || !Audio.getCurrentMusic().id.equals(newMapMusicId)) {
                
                Audio.playMusic(newMapMusicId);
                newMapMusicId = null;
            }
        }

        Player.setJustTeleported(true);
        Player.setLocation(playerNewRow, playerNewCol);
        Player.setDisabledEventLocation(playerNewCol, playerNewRow);
        if (!playerNewDirection.equals("preserve")) {
            Player.changeDirection(playerNewDirection);
        }
        
        // dark place with some kind of light effect
        currentMap.setDark(newMapIsDark);
        if (!newMapIsDark) {
            Player.setNotDarkPlace();
        }
        else if (newMapIsDark && newMapResetLight) {
            Player.setDarkPlace();
        }
        Player.updateViewLightRadius(true);
        
        // repel effect
        if (newMapResetRepel) {
            Player.setRepelCounter(0, "");
        }
        currentMap.setRepelHasNoEffect(newMapRepelHasNoEffect);
        currentMap.selectAreaAccordingToPlayerLocation();
        state = MAP;
        redraw();
        if (newMapUseFadeEffect) {
            View.fadeIn();
        }
    }
    
    @ScriptCommand(name = "wait_for_fire_key")
    public static void waitForFireKey() {
        while (true) {
            if (Input.isKeyJustPressed(KEY_CONFIRM)) {
                break;
            }
            View.refresh();
            sleep(1);
        }
    }

    @ScriptCommand(name = "wait_for_fire_or_esc_key")
    public static void waitForFireOrEscKey() {
        while (true) {
            if (Input.isKeyJustPressed(KEY_CONFIRM)
                    || Input.isKeyJustPressed(KEY_CANCEL)) {
                
                break;
            }
            View.refresh();
            sleep(1);
        }
    }
    
    @ScriptCommand(name = "wait_for_any_key")
    public static void waitForAnyKey() {
        while (true) {
            if (Input.isKeyJustPressed(KEY_LEFT)) {
                break;
            }
            if (Input.isKeyJustPressed(KEY_RIGHT)) {
                break;
            }
            if (Input.isKeyJustPressed(KEY_UP)) {
                break;
            }
            if (Input.isKeyJustPressed(KEY_DOWN)) {
                break;
            }
            if (Input.isKeyJustPressed(KEY_CONFIRM)) {
                break;
            }
            if (Input.isKeyJustPressed(KEY_CANCEL)) {
                break;
            }
            View.refresh();
            sleep(1);
        }
    }
    
    @ScriptCommand(name = "sleep")
    public static void sleep(int t) {
        try {
            Thread.sleep(t);
        } catch (InterruptedException ex) {
        }
        View.refresh();
    }
    
    @ScriptCommand(name = "teleport")
    public static void teleport(String mapId, int col, int row
            , String playerDirection, int useFadeEffect
            , String musicId, int isDark, int repelHasNoEffect
            , int resetRepel, int resetLight) throws Exception {
        
        if (currentMap != null && !currentMap.isDark() && isDark != 0 ) {
            Player.setOutsideLocation(currentMap.getId()
                    , Audio.getCurrentMusic().id
                    , Player.getMapCol(), Player.getMapRow());
        }
        
        newMap = Resource.getTileMap(mapId);
        playerNewRow = row;
        playerNewCol = col;
        playerNewDirection = playerDirection;
        newMapUseFadeEffect = useFadeEffect != 0;
        newMapMusicId = musicId;
        newMapIsDark = isDark != 0;
        newMapRepelHasNoEffect = repelHasNoEffect != 0;
        newMapResetRepel = resetRepel != 0;
        newMapResetLight = resetLight != 0;
        newClearLocalVars = true;
        state = CHANGE_MAP;
    }

    private static void showSavedFiles() {
        Dialog.drawBoxBorder(3, 9, 3, 28, 18);
        Dialog.fillBox(3, ' ', 10, 4, 27, 17);
        for (int i = 0; i < 3; i++) {
            String mapName = "---------";
            String playerName = "----";
            String playerLV = "--";
            String playerG = "-----";
            String playerAccumulatedTimeMs = "--:--:--";
            Map<String, Object> fileGlobalVars = Script.loadVars(i + 1);
            if (fileGlobalVars != null) {
                mapName = Util.formatLeft(
                    fileGlobalVars.get("$$current_map_name").toString(), 12);

                playerName = Util.formatLeft(
                        fileGlobalVars.get("$$player_name").toString(), 4);

                playerLV = Util.formatLeft(
                        fileGlobalVars.get("##player_lv").toString(), 2);

                playerG = Util.formatRight(
                        fileGlobalVars.get("##player_g").toString(), 5);

                playerAccumulatedTimeMs = Util.convertMsToHHMMSS(
                    (Long) fileGlobalVars.get("##player_accumulated_time_ms"));
            }
            Dialog.printText(
                    3, 11, 4 + i * 5, "File " + (i + 1) + " - " + playerName);

            Dialog.printText(3, 11, 5 + i * 5, "Loc.:" + mapName);
            Dialog.printText(
                    3, 11, 6 + i * 5, "Time:" + playerAccumulatedTimeMs);

            Dialog.printText(
                    3, 11, 7 + i * 5, "  LV:" + playerLV + "   G:" + playerG);

            if (i < 2) {
                Dialog.printText(3, 9, 8 + i * 5
                        , "\u000c\r\r\r\r\r\r\r\r\r\r\r\r\r\r\r\r\r\r\u000e");
            }
        }
    }

    private static void hideSavedFiles() {
        Dialog.fillBox(3, -1, 9, 3, 28, 18);
        View.refresh();
    }

    // return -1 = canceled
    private static int selectGameFile(int defaultSelectedItem) {
        showSavedFiles();
        int selectedItem = defaultSelectedItem;
        long blinkTime = System.nanoTime();
        boolean exit = false;
        while (!exit) {
            for (int r = 0; r < 3; r++) {
                Dialog.print(3, 10, 4 + r * 5, ' ');
            }

            if ((int) ((System.nanoTime() - blinkTime)
                                        * 0.0000000035) % 2 == 0) {

                Dialog.print(3, 10, 4 + selectedItem * 5, 2);
            }

            if (Input.isKeyJustPressed(KEY_UP)
                                            && selectedItem > 0) {

                selectedItem--;
                blinkTime = System.nanoTime();
            }
            else if (Input.isKeyJustPressed(KEY_DOWN)
                                && selectedItem < 3 - 1) {

                selectedItem++;
                blinkTime = System.nanoTime();
            }
            else if (Input.isKeyJustPressed(KEY_CONFIRM)) {
                Audio.playSound(Audio.SOUND_MENU_CONFIRMED);
                break;
            }
            else if (Input.isKeyJustPressed(KEY_CANCEL)) {
                selectedItem = -1;
                break;
            }
            View.refresh();
            Game.sleep(1000 / 60);
        }
        return selectedItem;
    }
    
    @ScriptCommand(name = "save_game")
    public static void saveGame() {
        int selectedItem = 0;
        boolean exit = false;
        while (!exit) {
            showSavedFiles();
            Dialog.clear();
            Dialog.print(0, 0, Game.getText("@@game_select_save_file"));
            selectedItem = selectGameFile(selectedItem);
            if (selectedItem >= 0) {
                hideSavedFiles();
                Dialog.clear();
                Script.setGlobalValue("##file_number", selectedItem + 1);
                Dialog.print(0, 0
                        , Game.getText("@@game_save_file_confirmation"));
                
                String yes = Game.getText("@@option_yes");
                String no = Game.getText("@@option_no");
                int saveConfirmation = Dialog.showOptionsMenu(
                                        3, true, 9, 3, 0, 0, 1, 1, yes, no);
                
                if (saveConfirmation == 0) {
                    Dialog.clear();
                    Player.updatePlayingTime();
                    RpgSystems.exportRuntimeToGlobals();
                    if (Script.saveVars(selectedItem + 1)) {
                        Dialog.print(
                            0, 1, Game.getText("@@game_saved_successfully"));
                        
                        exit = true;
                    }
                    else {
                        Dialog.print(0, 1, Game.getText("@@game_save_error"));
                        exit = false;
                    }
                }
                else if (saveConfirmation == 1) {
                    showSavedFiles();
                    exit = false;
                }
            }
            else if (selectedItem < 0) {
                selectedItem = -1;
                break;
            }
        }
        hideSavedFiles();
    }

    @ScriptCommand(name = "load_game")
    public static boolean loadGame() throws Exception {
        int selectedItem = 0;
        boolean exit = false;
        while (!exit) {
            showSavedFiles();
            Dialog.clear();
            Dialog.print(0, 0, Game.getText("@@game_select_load_file"));
            selectedItem = selectGameFile(selectedItem);
            if (selectedItem >= 0) {
                hideSavedFiles();
                Dialog.clear();
                Script.setGlobalValue("##file_number", selectedItem + 1);
                Dialog.print(0, 0
                        , Game.getText("@@game_load_file_confirmation"));
                
                String yes = Game.getText("@@option_yes");
                String no = Game.getText("@@option_no");
                int loadConfirmation = Dialog.showOptionsMenu(
                                        3, true, 9, 3, 0, 0, 1, 1, yes, no);
                
                if (loadConfirmation == 0) {
                    Dialog.clear();
                    Map<String, Object> loadedFile 
                            = Script.loadVars(selectedItem + 1);
                    
                    if (loadedFile != null) {
                        Dialog.print(
                            0, 0, Game.getText("@@game_loaded_successfully"));
                        
                        Game.sleep(1000);
                        Dialog.close();
                        loadGameInternal(loadedFile);
                        exit = true;
                        return true;
                    }
                    else {
                        Dialog.print(0, 1, Game.getText("@@game_load_error"));
                        exit = false;
                    }
                }
                else if (loadConfirmation == 1) {
                    showSavedFiles();
                    exit = false;
                }
            }
            else if (selectedItem < 0) {
                exit = true;
                break;
            }
        }
        hideSavedFiles();
        Dialog.close();
        return false;        
    }
    
    private static void loadGameInternal(
            Map<String, Object> loadedGlobalVars) throws Exception {
        
        Script.getVARS().clear();
        Script.getVARS().putAll(loadedGlobalVars);
        RpgSystems.importRuntimeFromGlobals();
        Audio.applyConfigValues();
        Dialog.applyConfigValues();
        Battle.applyConfigValues();
        Player.updateStartPlayingTime();
        String mapId = Script.getGlobalValue("$$current_map_id").toString();
        // save the original outside spell information because it will be
        // affected by teleport function
        String outsideMapId = (String) Script.getGlobalValue("$$player_outside_map");
        String outsideMusicId = (String) Script.getGlobalValue("$$player_outside_music_id");
        Integer outsideRow = (Integer) Script.getGlobalValue("##player_outside_row");
        Integer outsideCol = (Integer) Script.getGlobalValue("##player_outside_col");
        teleport(mapId, Player.getMapCol(), Player.getMapRow(), "down", 1
            , "" + Script.getGlobalValue("$$current_map_music_id")
            , (Integer) Script.getGlobalValue("##current_map_is_dark")
            , (Integer) Script.getGlobalValue(
                    "##current_map_repel_has_no_effect"), 0, 0);
        // restore the original outside information
        Script.setGlobalValue("$$player_outside_map", outsideMapId);
        Script.setGlobalValue("$$player_outside_music_id", outsideMusicId);
        Script.setGlobalValue("##player_outside_row", outsideRow);
        Script.setGlobalValue("##player_outside_col", outsideCol);
        currentMap = null;
        newClearLocalVars = false;
        newMap.setResetLightOnEnter(false);
        newMap.setResetRepelOnEnter(false);
        Audio.stopMusic();
        Object newMapMusicIdTmp 
                = Script.getGlobalValue("$$current_map_music_id");
        
        newMapMusicId 
                = newMapMusicIdTmp == null ? null : newMapMusicIdTmp.toString();
    }

    @ScriptCommand(name = "change_event_location")
    public static void changeEventVisibility(String eventId, int col, int row) {
        for (Event event : currentMap.getEvents()) {
            if (event.getId().equals(eventId)) {
                event.setLocation(col * 16, row * 16);
                break;
            }
        }
    }
    
    @ScriptCommand(name = "change_event_visibility")
    public static void changeEventVisibility(String eventId, int visible) {
        for (Event event : currentMap.getEvents()) {
            if (event.getId().equals(eventId)) {
                event.setVisible(visible != 0);
                break;
            }
        }
    }

    @ScriptCommand(name = "change_event_animation")
    public static void changeEventAnimation(String eventId, String animatId) {
        for (Event event : currentMap.getEvents()) {
            if (event.getId().equals(eventId)) {
                event.changeAnimation(animatId);
                break;
            }
        }
    }

    @ScriptCommand(name = "change_event_turn_to_player")
    public static void changeEventTurnToPlayer(String eventId) {
        for (Event event : currentMap.getEvents()) {
            if (event.getId().equals(eventId)) {
                event.turnToPlayer();
                break;
            }
        }
    }

    // get_event col row "$$event_id" "$$event_type"
    @ScriptCommand(name = "get_event")
    public static void getEvent(int col, int row, String eventIdGlobalVar
                                , String eventTypeGlobalVar) throws Exception {
        
        Script.setGlobalValue(eventIdGlobalVar, "");
        Script.setGlobalValue(eventTypeGlobalVar, "");
        for (Event event : currentMap.getEvents()) {
            if (event.getX() == col * 16 && event.getY() == row * 16) {
                Script.setGlobalValue(eventIdGlobalVar, event.getId());
                Script.setGlobalValue(eventTypeGlobalVar, event.getType());
                break;
            }
        }
    }
    
    @ScriptCommand(name = "trigger_event")
    public static void triggerEvent(
            String eventId, String label) throws Exception {
        
        for (Event event : currentMap.getEvents()) {
            if (event.getId().equals(eventId)) {
                event.getScript().execute(label);
                break;
            }
        }
    }
    
    @ScriptCommand(name = "exit_game")
    public static void exitGame() {
        System.exit(0);
    }
    

    public static boolean showOptionsMenu() {
        Dialog.drawBoxBorder(3, 18, 3, 30, 12);
        Dialog.fillBox(3, ' ', 19, 4, 29, 11);

        String msgSpeed1 = Game.getText("@@game_config_option_msg_speed_1");
        String msgSpeed2 = Game.getText("@@game_config_option_msg_speed_2");
        String battleSpeed1 = 
                Game.getText("@@game_config_option_battle_speed_1");
        
        String battleSpeed2 = 
                Game.getText("@@game_config_option_battle_speed_2");
        
        String soundVol1 = Game.getText("@@game_config_option_sound_vol_1");
        String soundVol2 = Game.getText("@@game_config_option_sound_vol_2");
        String musicVol1 = Game.getText("@@game_config_option_music_vol_1");
        String musicVol2 = Game.getText("@@game_config_option_music_vol_2");
        
        msgSpeed1 = Util.formatLeft(msgSpeed1, 7);
        msgSpeed2 = Util.formatLeft(msgSpeed2, 7);
        battleSpeed1 = Util.formatLeft(battleSpeed1, 7);
        battleSpeed2 = Util.formatLeft(battleSpeed2, 7);
        soundVol1 = Util.formatLeft(soundVol1, 7);
        soundVol2 = Util.formatLeft(soundVol2, 7);
        musicVol1 = Util.formatLeft(musicVol1, 7);
        musicVol2 = Util.formatLeft(musicVol2, 7);
        
        Dialog.printText(3, 20, 4, msgSpeed1 + "\u000b \u0002");
        Dialog.printText(3, 21, 5, msgSpeed2);
        Dialog.printText(3, 20, 6, battleSpeed1 + "\u000b \u0002");
        Dialog.printText(3, 21, 7, battleSpeed2);
        Dialog.printText(3, 20, 8, soundVol1 + "\u000b \u0002");
        Dialog.printText(3, 21, 9, soundVol2);
        Dialog.printText(3, 20, 10, musicVol1 + "\u000b \u0002");
        Dialog.printText(3, 21, 11, musicVol2);
        
        // wait for appropriate cursor start blink time
        long blinkTime = System.nanoTime();
        int selectedOption = 0;
        boolean retValue = false;
        while (true) {
            Dialog.print(3, 19, 4, ' ');
            Dialog.print(3, 19, 6, ' ');
            Dialog.print(3, 19, 8, ' ');
            Dialog.print(3, 19, 10, ' ');

            Dialog.printText(3, 28, 4, "" + (9 - Dialog.getSpeed() / 10));
            Dialog.printText(3, 28, 6, "" + (9 - Battle.getSpeed() / 10));
            Dialog.printText(3, 28, 8, "" + Audio.getSoundVolume());
            Dialog.printText(3, 28, 10, "" + Audio.getMusicVolume());

            // blink cursor
            if ((int) ((System.nanoTime() - blinkTime) 
                                        * 0.0000000035) % 2 == 0) {

                Dialog.print(3, 19, 4 + 2 * selectedOption, 2);
            }

            if (Input.isKeyJustPressed(KEY_UP) 
                    && selectedOption > 0) {

                selectedOption--;
                blinkTime = System.nanoTime();
            }
            else if (Input.isKeyJustPressed(KEY_DOWN) 
                                && selectedOption < 3) {

                selectedOption++;
                blinkTime = System.nanoTime();
            }
            else if (Input.isKeyJustPressed(KEY_LEFT)) {
                changeOptionValue(selectedOption, -1);
            }
            else if (Input.isKeyJustPressed(KEY_RIGHT)) {
                changeOptionValue(selectedOption, 1);
            }
            else if (Input.isKeyJustPressed(KEY_CONFIRM)) {
                selectedOption = -1;
                retValue = true;
                break;
            }
            else if (Input.isKeyJustPressed(KEY_CANCEL)) {
                selectedOption = -1;
                retValue = false;
                break;
            }
            View.refresh();
            Game.sleep(1000 / 60);
        }

        Dialog.fillBox(3, -1, 18, 3, 30, 12);
        return retValue;
    }
    
    private static void changeOptionValue(int option, int dv) {
        switch (option) {
            // message speed
            case 0:
                int currentMessageSpeed = 9 - Dialog.getSpeed() / 10;
                int newMessageSpeed = currentMessageSpeed + dv < 1 
                        ? 1 : currentMessageSpeed + dv > 9 
                        ? 9 : currentMessageSpeed + dv;
                
                newMessageSpeed = (9 - newMessageSpeed) * 10 + 1;
                Dialog.setSpeed(newMessageSpeed);
                Script.setGlobalValue(
                        "##game_config_message_speed_ms", newMessageSpeed);
                
                break;
            // battle speed
            case 1:
                int currentBattleSpeed = 9 - Battle.getSpeed() / 10;
                int newBattleSpeed = currentBattleSpeed + dv < 1 
                        ? 1 : currentBattleSpeed + dv > 9 
                        ? 9 : currentBattleSpeed + dv;
                
                newBattleSpeed = (9 - newBattleSpeed) * 10 + 1;
                Battle.setSpeed(newBattleSpeed);
                Script.setGlobalValue(
                        "##game_config_battle_speed_ms", newBattleSpeed);
                
                break;
            // sound volume
            case 2:
                int currentSoundVolume = Audio.getSoundVolume();
                int newSoundVolume = currentSoundVolume + dv < 0 
                        ? 0 : currentSoundVolume + dv > 9 
                        ? 9 : currentSoundVolume + dv;

                Audio.setSoundVolume(newSoundVolume);
                Audio.playSound(Audio.SOUND_SHOW_OPTIONS_MENU);
                break;
            // music volume
            case 3:
                int currentMusicVolume = Audio.getMusicVolume();
                int newMusicVolume = currentMusicVolume + dv < 0 
                        ? 0 : currentMusicVolume + dv > 9 
                        ? 9 : currentMusicVolume + dv;
                
                Audio.setMusicVolume(newMusicVolume);
                break;
        }
    }
    
    // enabled != 0 = true
    @ScriptCommand(name = "set_current_map_enemies_encounter_enabled")
    public static void setCurrentMapEnemiesEncountersEnabled(int enabled) {
        if (currentMap != null) {
            currentMap.setEnemiesEncounterEnabled(enabled != 0);
        }
    }

    // direction -> "down", "left", "up", "right" or "stay"
    @ScriptCommand(name = "walk_event")
    public static void walkEvent(
            String eventId, String direction) throws Exception {
        
        int walkDx = 0;
        int walkDy = 0;
        switch (direction) {
            case "down": walkDx = 0; walkDy = 1; break;
            case "left": walkDx = -1; walkDy = 0; break;
            case "up": walkDx = 0; walkDy = -1; break;
            case "right": walkDx = 1; walkDy = 0; break;
            case "stay": walkDx = 0; walkDy = 0; break;
        }
        if (eventId.equals("player")) {
            if (!direction.equals("stay")) {
                Player.changeDirection(direction);
            }
            startTime = System.currentTimeMillis();
            for (int i = 0; i < 16; i++) {
                Player.incX(walkDx);
                Player.incY(walkDy);
                Player.getAnimation().update();
                for (Event eventTmp : currentMap.getEvents()) {
                    eventTmp.getAnimation().update();
                }
                redraw();
                sync30fps();
            }
            Player.setLocation(Player.getMapRow(), Player.getMapCol());
        }
        else {
            startTime = System.currentTimeMillis();
            for (int i = 0; i < 16; i++) {
                for (Event eventTmp : currentMap.getEvents()) {
                    if (eventTmp.getId().equals(eventId)) {
                        if (!direction.equals("stay")) {
                            eventTmp.changeAnimation(direction);
                        }
                        eventTmp.setLocation(
                            eventTmp.getX() + walkDx, eventTmp.getY() + walkDy);
                    }
                    eventTmp.getAnimation().update();
                }
                Player.getAnimation().update();
                redraw();
                sync30fps();
            }
        }
    }

    @ScriptCommand(name = "change_tile")
    public static void changeTile(int col, int row, int tileId) {
        if (currentMap != null) {
            currentMap.setTile(row, col, tileId);
        }
    }
    
    private static boolean loadGameSlot(int slot) {
        try {
            Map<String, Object> loadedGlobalVars = Script.loadVars(slot);
            loadGameInternal(loadedGlobalVars);
            return true;
        } catch (Exception e) {
            Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, e);
            return false;
        }
    }

    private static void showSettingsMenu() {
        boolean exit = false;
        while (!exit) {
            String[] settingsOptions = new String[] {
                "Audio / Speed",
                "Display",
                "Keyboard Keybinds",
                "Mouse Controls",
                "UI / Story",
                "Reset Default Keybinds",
                "Back"
            };
            int option = Dialog.showOptionsMenu(8, 8, 24, 9, -1, settingsOptions);
            switch (option) {
                case 0:
                    showOptionsMenu();
                    break;
                case 1:
                    changeScreenResolution();
                    break;
                case 2:
                    remapControlsMenu();
                    break;
                case 3:
                    showMouseControlsMenu();
                    break;
                case 4:
                    showUiStoryMenu();
                    break;
                case 5:
                    resetDefaultKeybinds();
                    break;
                case 6:
                case -1:
                    exit = true;
                    break;
            }
        }
    }

    private static void showMouseControlsMenu() {
        boolean exit = false;
        while (!exit) {
            String[] options = new String[] {
                "Mouse Input: " + (Settings.MOUSE_ENABLED ? "ON" : "OFF"),
                "Left Click -> Confirm",
                "Right Click -> Cancel",
                "Back"
            };
            int option = Dialog.showOptionsMenu(8, 10, 28, 6, -1, options);
            switch (option) {
                case 0:
                    Settings.MOUSE_ENABLED = !Settings.MOUSE_ENABLED;
                    showUiToast("Mouse Input: " + (Settings.MOUSE_ENABLED ? "ON" : "OFF"));
                    break;
                case 1:
                    showUiToast("Left click is confirm.");
                    break;
                case 2:
                    showUiToast("Right click is cancel.");
                    break;
                case 3:
                case -1:
                    exit = true;
                    break;
            }
        }
    }

    private static void showUiStoryMenu() {
        boolean exit = false;
        while (!exit) {
            String[] options = new String[] {
                "Auto Skip Story: " + (Settings.SKIP_INTRO_STORY ? "ON" : "OFF"),
                "Show Controls Help",
                "Back"
            };
            int option = Dialog.showOptionsMenu(8, 10, 24, 5, -1, options);
            switch (option) {
                case 0:
                    Settings.SKIP_INTRO_STORY = !Settings.SKIP_INTRO_STORY;
                    showUiToast("Auto Skip Story: " + (Settings.SKIP_INTRO_STORY ? "ON" : "OFF"));
                    break;
                case 1:
                    showUiHelpMenu();
                    break;
                case 2:
                case -1:
                    exit = true;
                    break;
            }
        }
    }

    private static void resetDefaultKeybinds() {
        Settings.KEY_CONFIRM = KeyEvent.VK_X;
        Settings.KEY_CANCEL = KeyEvent.VK_Z;
        Settings.KEY_UP = KeyEvent.VK_UP;
        Settings.KEY_DOWN = KeyEvent.VK_DOWN;
        Settings.KEY_LEFT = KeyEvent.VK_LEFT;
        Settings.KEY_RIGHT = KeyEvent.VK_RIGHT;
        syncLegacyKeybindFields();
        showUiToast("Keybinds reset to default.");
    }

    private static void changeScreenResolution() {
        String[] resolutions = new String[] {
            "640x480",
            "800x600",
            "1024x768",
            "1280x960",
            "Toggle Fullscreen: " + (Settings.fullscreen ? "ON" : "OFF"),
            "Back"
        };
        boolean exit = false;
        while (!exit) {
            int option = Dialog.showOptionsMenu(10, 10, 30, 8, -1, resolutions);
            switch (option) {
                case 0:
                    Settings.screenWidth = 640;
                    Settings.screenHeight = 480;
                    showUiToast("Resolution set to 640x480");
                    break;
                case 1:
                    Settings.screenWidth = 800;
                    Settings.screenHeight = 600;
                    showUiToast("Resolution set to 800x600");
                    break;
                case 2:
                    Settings.screenWidth = 1024;
                    Settings.screenHeight = 768;
                    showUiToast("Resolution set to 1024x768");
                    break;
                case 3:
                    Settings.screenWidth = 1280;
                    Settings.screenHeight = 960;
                    showUiToast("Resolution set to 1280x960");
                    break;
                case 4:
                    Settings.fullscreen = !Settings.fullscreen;
                    showUiToast("Fullscreen: " + (Settings.fullscreen ? "ON" : "OFF"));
                    break;
                case 5:
                case -1:
                    exit = true;
                    break;
            }
        }
    }

    private static void remapControlsMenu() {
        boolean exit = false;
        while (!exit) {
            String[] controls = new String[] {
                "Confirm: " + keyName(Settings.KEY_CONFIRM),
                "Cancel : " + keyName(Settings.KEY_CANCEL),
                "Up     : " + keyName(Settings.KEY_UP),
                "Down   : " + keyName(Settings.KEY_DOWN),
                "Left   : " + keyName(Settings.KEY_LEFT),
                "Right  : " + keyName(Settings.KEY_RIGHT),
                "Back"
            };

            int option = Dialog.showOptionsMenu(8, 8, 28, 9, -1, controls);
            switch (option) {
                case 0:
                    remapBinding("Confirm", 0);
                    break;
                case 1:
                    remapBinding("Cancel", 1);
                    break;
                case 2:
                    remapBinding("Up", 2);
                    break;
                case 3:
                    remapBinding("Down", 3);
                    break;
                case 4:
                    remapBinding("Left", 4);
                    break;
                case 5:
                    remapBinding("Right", 5);
                    break;
                case 6:
                case -1:
                    exit = true;
                    break;
            }
        }
    }

    private static void remapBinding(String controlName, int bindingIndex) {
        Dialog.printText(3, 8, 21, "Press key for " + controlName + " (ESC = cancel)");
        int newKey = waitForKeyPress();
        Dialog.hideOptionsMenu(3, 8, 21, 40, 2, "");
        if (newKey == -1) {
            return;
        }
        switch (bindingIndex) {
            case 0: Settings.KEY_CONFIRM = newKey; break;
            case 1: Settings.KEY_CANCEL = newKey; break;
            case 2: Settings.KEY_UP = newKey; break;
            case 3: Settings.KEY_DOWN = newKey; break;
            case 4: Settings.KEY_LEFT = newKey; break;
            case 5: Settings.KEY_RIGHT = newKey; break;
        }
        syncLegacyKeybindFields();
        showUiToast(controlName + " = " + keyName(newKey));
    }

    private static int waitForKeyPress() {
        Input.clearState();
        while (true) {
            int keyCode = Input.consumeLastKeyPressed();
            if (keyCode == KeyEvent.VK_ESCAPE) {
                return -1;
            }
            if (keyCode >= 0) {
                return keyCode;
            }
            View.refresh();
            Game.sleep(1000 / 60);
        }
    }

    private static void syncLegacyKeybindFields() {
        Settings.keyConfirm = Settings.KEY_CONFIRM;
        Settings.keyCancel = Settings.KEY_CANCEL;
        Settings.keyUp = Settings.KEY_UP;
        Settings.keyDown = Settings.KEY_DOWN;
        Settings.keyLeft = Settings.KEY_LEFT;
        Settings.keyRight = Settings.KEY_RIGHT;
    }

    private static String keyName(int keyCode) {
        return KeyEvent.getKeyText(keyCode).toUpperCase();
    }

    private static void showUiToast(String text) {
        Dialog.printText(3, 8, 22, text);
        Game.sleep(900);
        Dialog.hideOptionsMenu(3, 8, 22, Math.max(24, text.length() + 2), 2, "");
    }

    public static void showQuickToast(String text) {
        showUiToast(text);
    }

    public static void showUiHelpMenu() {
        Dialog.drawBoxBorder(3, 4, 4, 31, 17);
        Dialog.fillBox(3, ' ', 5, 5, 30, 16);
        Dialog.printText(3, 6, 6, "RPG UI HELP");
        Dialog.printText(3, 6, 8, "Move: " + keyName(Settings.KEY_UP) + "/" + keyName(Settings.KEY_DOWN)
                + "/" + keyName(Settings.KEY_LEFT) + "/" + keyName(Settings.KEY_RIGHT));
        Dialog.printText(3, 6, 10, "Confirm: " + keyName(Settings.KEY_CONFIRM));
        Dialog.printText(3, 6, 11, "Cancel : " + keyName(Settings.KEY_CANCEL));
        Dialog.printText(3, 6, 13, "Mouse L: Confirm");
        Dialog.printText(3, 6, 14, "Mouse R: Cancel");
        Dialog.printText(3, 6, 16, "Press confirm/cancel to close");
        waitForFireOrEscKey();
        Dialog.hideOptionsMenu(3, 4, 4, 28, 14, "");
    }

    public static void showQuestMenu() {
        String[] questOptions = new String[] {
            "Quest Log",
            "Story Chapters",
            "Accept Available",
            "Progress Objective",
            "Turn In Completed",
            "Create Demo Quest",
            "Back"
        };
        boolean exit = false;
        while (!exit) {
            int option = Dialog.showOptionsMenu(10, 18, 24, 8, -1, questOptions);
            switch (option) {
                case 0:
                    showQuestList();
                    break;
                case 1:
                    showStoryChapters();
                    break;
                case 2:
                    if (Quest.acceptFirstAvailableQuest()) {
                        showUiToast("Accepted a quest.");
                    } else {
                        showUiToast("No available quests.");
                    }
                    break;
                case 3:
                    if (Quest.progressFirstActiveQuest()) {
                        showUiToast("Objective progressed.");
                    } else {
                        showUiToast("No active objective.");
                    }
                    break;
                case 4:
                    if (Quest.turnInFirstCompletedQuest()) {
                        showUiToast("Quest turned in.");
                    } else {
                        showUiToast("No completed quest to turn in.");
                    }
                    break;
                case 5:
                    addDemoQuestIfNeeded();
                    break;
                case 6:
                case -1:
                    exit = true;
                    break;
            }
        }
    }

    private static void addDemoQuestIfNeeded() {
        if (Quest.getQuest("demo_quest") == null) {
            Quest.seedDemoSideQuestIfNeeded();
            showUiToast("Demo quest added.");
        }
        else {
            showUiToast("Demo quest already exists.");
        }
    }

    private static void showQuestList() {
        Quest.initializeWoWStoryIfNeeded();
        java.util.List<Quest.QuestData> quests = Quest.getAllQuests();
        Dialog.drawBoxBorder(3, 6, 6, 31, 20);
        Dialog.fillBox(3, ' ', 7, 7, 30, 19);
        if (quests.isEmpty()) {
            Dialog.printText(3, 8, 9, "No quests found.");
            Dialog.printText(3, 8, 11, "Use 'Create Demo Quest' first.");
            waitForFireOrEscKey();
            Dialog.hideOptionsMenu(3, 6, 6, 26, 14, "");
            return;
        }
        int row = 8;
        for (Quest.QuestData q : quests) {
            if (row > 18) {
                break;
            }
            String status = q.status.name().replace('_', ' ');
            Dialog.printText(3, 8, row, q.name + " [" + status + "]");
            if (row + 1 <= 18) {
                if (!q.objectiveData.isEmpty()) {
                    Dialog.printText(3, 8, row + 1, q.objectiveData.get(0).getProgressText());
                } else {
                    Dialog.printText(3, 8, row + 1, q.description);
                }
            }
            row += 3;
        }
        Dialog.printText(3, 8, 19, "Press confirm/cancel");
        waitForFireOrEscKey();
        Dialog.hideOptionsMenu(3, 6, 6, 26, 14, "");
    }

    private static void showStoryChapters() {
        Quest.initializeWoWStoryIfNeeded();
        java.util.List<Quest.StoryChapter> chapters = Quest.getStoryChapters();
        Dialog.drawBoxBorder(3, 5, 5, 31, 20);
        Dialog.fillBox(3, ' ', 6, 6, 30, 19);
        int row = 7;
        for (Quest.StoryChapter chapter : chapters) {
            if (row > 18) {
                break;
            }
            Dialog.printText(3, 7, row, chapter.title);
            if (row + 1 <= 18) {
                Dialog.printText(3, 7, row + 1, Quest.getChapterProgressText(chapter.id));
            }
            row += 3;
        }
        Dialog.printText(3, 7, 19, "Press confirm/cancel");
        waitForFireOrEscKey();
        Dialog.hideOptionsMenu(3, 5, 5, 27, 15, "");
    }

    private static void showWoWMapDialog() throws Exception {
        try {
            WoWTileMapSystem mapSystem
                    = WoWTileMapSystem.loadFromGameAssets(Path.of("assets", "res", "map"));
            List<WoWTileMapSystem.MapRecord> maps = mapSystem.getMaps();
            if (maps.isEmpty()) {
                showSimplePanel("WOW MAP CATALOG", List.of("No map files found."));
                return;
            }

            final int pageSize = 7;
            int page = 0;
            boolean exit = false;
            while (!exit) {
                int totalPages = (maps.size() + pageSize - 1) / pageSize;
                int start = page * pageSize;
                int end = Math.min(start + pageSize, maps.size());

                List<String> options = new ArrayList<>();
                for (int i = start; i < end; i++) {
                    WoWTileMapSystem.MapRecord map = maps.get(i);
                    options.add("#" + map.getMapId() + " " + map.getMapTitle());
                }
                boolean hasPrev = page > 0;
                boolean hasNext = page < totalPages - 1;
                if (hasPrev) {
                    options.add("< Prev Page");
                }
                if (hasNext) {
                    options.add("Next Page >");
                }
                options.add("Back");

                int option = Dialog.showOptionsMenu(
                        4, 5, 33, options.size() + 2, -1,
                        options.toArray(new String[0]));

                int mapCountInPage = end - start;
                if (option >= 0 && option < mapCountInPage) {
                    showWoWMapDetailsPanel(maps.get(start + option));
                    continue;
                }

                int cursor = mapCountInPage;
                if (hasPrev) {
                    if (option == cursor) {
                        page--;
                        continue;
                    }
                    cursor++;
                }
                if (hasNext) {
                    if (option == cursor) {
                        page++;
                        continue;
                    }
                    cursor++;
                }
                if (option == cursor || option == -1) {
                    exit = true;
                }
            }
        } catch (Exception ex) {
            showSimplePanel("WOW MAP CATALOG",
                    List.of("Catalog error.",
                            ex.getMessage() == null ? "Unknown error." : ex.getMessage()));
        }
    }

    private static void showWoWMapDetailsPanel(WoWTileMapSystem.MapRecord map) {
        List<String> lines = new ArrayList<>();
        lines.add("ID: " + map.getMapId() + "   File: " + map.getMapFileId());
        lines.add("Title: " + map.getMapTitle());
        lines.add("Biome: " + map.getBiome());
        lines.add("Size: " + map.getRows() + "x" + map.getCols());
        lines.add("Tiles: " + map.getTileCount());
        lines.add("Top tile IDs:");

        map.getTileIdCounts().entrySet().stream()
                .sorted(Map.Entry.<Integer, Integer>comparingByValue(Comparator.reverseOrder()))
                .limit(5)
                .forEach(e -> lines.add("tileId " + e.getKey() + " -> " + e.getValue()));

        showSimplePanel("WOW MAP DETAILS", lines);
    }

    private static void showWoWMapGraphical() {
        SwingUtilities.invokeLater(() -> new WowMapCatalogFrame().setVisible(true));
    }

    public static void showWoWMapCatalog() throws Exception {
        showWoWMapDialog();
    }

    public static void showWoWUi() {
        boolean exit = false;
        while (!exit) {
            String[] options = WOW_UI.getHubMenuOptions();
            int option = Dialog.showOptionsMenu(8, 8, 30, 11, -1, options);
            if (option == -1 || option >= WOW_UI.getHubOrder().size()) {
                exit = true;
                continue;
            }

            WowPanelId panelId = WOW_UI.getHubOrder().get(option);
            switch (panelId) {
                case CHARACTER:
                    showWoWCharacterPanel();
                    break;
                case INVENTORY:
                    showWoWInventoryPanel();
                    break;
                case QUESTS:
                    showQuestMenu();
                    break;
                case WORLD_MAP:
                    try {
                        showWoWMapCatalog();
                    }
                    catch (Exception ex) {
                        showSimplePanel("WOW WORLD MAP",
                                List.of("Failed to open map catalog.",
                                        ex.getMessage() == null ? "Unknown error." : ex.getMessage()));
                    }
                    break;
                case PARTY:
                    showPartyMenu();
                    break;
                case HOTBAR:
                    showWoWHotbarPanel();
                    break;
                case KEYBINDS:
                    showWoWKeybindPanel();
                    break;
                case DIABLO_INVENTORY:
                    SwingUtilities.invokeLater(() -> new DiabloInventoryFrame().setVisible(true));
                    break;
                case EXTERNAL_FRAME:
                    SwingUtilities.invokeLater(() -> new WowUiFrame().setVisible(true));
                    break;
            }
        }
    }

    public static void showSettingsMenuFromGame() {
        showSettingsMenu();
    }

    public static void showPartyMenu() {
        boolean exit = false;
        while (!exit) {
            String[] options = new String[] {
                "RPG Character Summary",
                "RPG Inventory Overview",
                "RPG Buffs / Debuffs",
                "RPG Runtime Actions",
                "Fantasy Entity Catalog (600)",
                "Feature Coverage",
                "Back"
            };
            int option = Dialog.showOptionsMenu(9, 10, 30, 9, -1, options);
            switch (option) {
                case 0:
                    showRpgCharacterSummary();
                    break;
                case 1:
                    showRpgInventoryOverview();
                    break;
                case 2:
                    showRpgBuffDebuffOverview();
                    break;
                case 3:
                    showRpgRuntimeActions();
                    break;
                case 4:
                    showFantasyEntityCatalogMenu();
                    break;
                case 5:
                    showFeatureCoverage();
                    break;
                case 6:
                case -1:
                    exit = true;
                    break;
            }
        }
    }

    public static void onWoWHotbarPressed(int slot) {
        RpgActionResult result = RpgSystems.getRuntime().applyHotbarEffect(slot);
        if (result.isSuccess()) {
            RpgSystems.getRuntime().nextTurn();
        }
        showUiToast(result.getMessage());
    }

    private static void showWoWCharacterPanel() {
        WowPanelModel model = WOW_UI.getPanel(WowPanelId.CHARACTER);
        List<String> lines = new ArrayList<>();
        lines.add(model.getSubtitle());
        lines.addAll(RpgSystems.buildSummaryLines());
        lines.addAll(RpgSystems.buildEquipmentLines().subList(0, 3));
        lines.add("Shortcut: " + keyName(Settings.KEY_WOW_CHARACTER));
        lines.add("Open panel key: " + keyName(Settings.KEY_WOW_UI));
        showSimplePanel(model.getTitle(), lines);
    }

    private static void showWoWInventoryPanel() {
        WowPanelModel model = WOW_UI.getPanel(WowPanelId.INVENTORY);
        PlayerRpgProfile profile = RpgSystems.getProfile();
        InventorySystem inventory = profile.getInventory();
        List<String> lines = new ArrayList<>();
        lines.add(model.getSubtitle());
        lines.add("Slots: " + inventory.getUsedSlots() + "/" + inventory.getMaxSlots());
        lines.add("Shortcut: " + keyName(Settings.KEY_WOW_INVENTORY));
        lines.add("Top items:");
        int shown = 0;
        for (InventorySystem.InventoryEntry entry : inventory.getEntries()) {
            if (shown >= 5) {
                break;
            }
            lines.add("- " + entry.getDefinition().getName() + " x" + entry.getQuantity());
            shown++;
        }
        if (shown == 0) {
            lines.add("Inventory is empty.");
        }
        showSimplePanel(model.getTitle(), lines);
    }

    private static void showWoWHotbarPanel() {
        WowPanelModel model = WOW_UI.getPanel(WowPanelId.HOTBAR);
        List<String> lines = new ArrayList<>();
        lines.add(model.getSubtitle());
        lines.add("Slot 1 key: " + keyName(Settings.KEY_WOW_HOTBAR_1));
        lines.add("Slot 2 key: " + keyName(Settings.KEY_WOW_HOTBAR_2));
        lines.add("Slot 3 key: " + keyName(Settings.KEY_WOW_HOTBAR_3));
        lines.add("Slot 4 key: " + keyName(Settings.KEY_WOW_HOTBAR_4));
        lines.add("Slot 5 key: " + keyName(Settings.KEY_WOW_HOTBAR_5));
        lines.addAll(RpgSystems.getRuntime().buildHotbarLines().subList(0, 5));
        showSimplePanel(model.getTitle(), lines);
    }

    private static void showWoWKeybindPanel() {
        WowPanelModel model = WOW_UI.getPanel(WowPanelId.KEYBINDS);
        List<String> lines = new ArrayList<>();
        lines.add(model.getSubtitle());
        lines.add("UI Hub: " + keyName(Settings.KEY_WOW_UI));
        lines.add("Character: " + keyName(Settings.KEY_WOW_CHARACTER));
        lines.add("Inventory: " + keyName(Settings.KEY_WOW_INVENTORY));
        lines.add("Spellbook: " + keyName(Settings.KEY_WOW_SPELLBOOK));
        lines.add("Quest Log: " + keyName(Settings.KEY_WOW_QUEST_LOG));
        lines.add("World Map: " + keyName(Settings.KEY_WOW_WORLD_MAP));
        lines.add("Party: " + keyName(Settings.KEY_WOW_PARTY));
        lines.add("Settings: " + keyName(Settings.KEY_WOW_SETTINGS));
        lines.add("Confirm: " + keyName(Settings.KEY_CONFIRM));
        lines.add("Cancel: " + keyName(Settings.KEY_CANCEL));
        showSimplePanel(model.getTitle(), lines);
    }

    private static void showRpgRuntimeActions() {
        boolean exit = false;
        while (!exit) {
            String[] options = new String[] {
                "Equip first inventory gear",
                "Unequip main hand",
                "Use first consumable",
                "Change class",
                "Auto-equip best loadout",
                "Auto-bind hotbar",
                "Advance effect turn",
                "Back"
            };
            int option = Dialog.showOptionsMenu(8, 9, 31, 10, -1, options);
            RpgRuntimeService runtime = RpgSystems.getRuntime();
            RpgActionResult result;
            switch (option) {
                case 0:
                    result = equipFirstAvailableGear(runtime);
                    showUiToast(result.getMessage());
                    break;
                case 1:
                    result = runtime.unequipToInventory(EquipmentSlot.MAIN_HAND);
                    showUiToast(result.getMessage());
                    break;
                case 2:
                    result = useFirstConsumable(runtime);
                    showUiToast(result.getMessage());
                    break;
                case 3:
                    showClassSelector(runtime);
                    break;
                case 4:
                    result = runtime.autoEquipBestLoadout();
                    showUiToast(result.getMessage());
                    break;
                case 5:
                    result = runtime.autoBindHotbar();
                    showUiToast(result.getMessage());
                    break;
                case 6:
                    runtime.nextTurn();
                    showUiToast("Advanced one RPG turn.");
                    break;
                case 7:
                case -1:
                    exit = true;
                    break;
            }
        }
    }

    private static void showFantasyEntityCatalogMenu() {
        boolean exit = false;
        while (!exit) {
            String[] options = new String[] {
                "Catalog Summary",
                "Group Counts",
                "Sample Entries",
                "Back"
            };
            int option = Dialog.showOptionsMenu(8, 9, 30, 6, -1, options);
            switch (option) {
                case 0:
                    showFantasySummaryPanel();
                    break;
                case 1:
                    showFantasyGroupCountPanel();
                    break;
                case 2:
                    showFantasySampleSelector();
                    break;
                case 3:
                case -1:
                    exit = true;
                    break;
            }
        }
    }

    private static void showFantasySummaryPanel() {
        List<String> lines = new ArrayList<>();
        lines.add("Fantasy Taxonomy Loaded");
        lines.add("Base types: " + FantasyEntityCatalog.getArchetypeCount());
        lines.add("Profiles: " + FantasyEntityCatalog.getProfileCount());
        lines.add("Groups: MONSTER, NPC, CREATURE, ANIMAL");
        lines.add("Each profile has class/subclass/subtype.");
        showSimplePanel("FANTASY CATALOG", lines);
    }

    private static void showFantasyGroupCountPanel() {
        List<String> lines = new ArrayList<>();
        lines.add("Group distribution:");
        Map<FantasyEntityGroup, Integer> counts = FantasyEntityCatalog.getGroupCounts();
        for (FantasyEntityGroup group : FantasyEntityGroup.values()) {
            lines.add(group.name() + ": " + counts.getOrDefault(group, 0));
        }
        showSimplePanel("FANTASY GROUP COUNTS", lines);
    }

    private static void showFantasySampleSelector() {
        String[] options = new String[] {
            "Monsters",
            "NPCs",
            "Creatures",
            "Animals",
            "Back"
        };
        int option = Dialog.showOptionsMenu(10, 9, 22, 7, -1, options);
        switch (option) {
            case 0:
                showFantasySamplesByGroup(FantasyEntityGroup.MONSTER);
                break;
            case 1:
                showFantasySamplesByGroup(FantasyEntityGroup.NPC);
                break;
            case 2:
                showFantasySamplesByGroup(FantasyEntityGroup.CREATURE);
                break;
            case 3:
                showFantasySamplesByGroup(FantasyEntityGroup.ANIMAL);
                break;
            default:
                break;
        }
    }

    private static void showFantasySamplesByGroup(FantasyEntityGroup group) {
        List<FantasyEntityProfile> profiles = FantasyEntityCatalog.getByGroup(group);
        List<String> lines = new ArrayList<>();
        lines.add(group.name() + " sample:");
        int shown = 0;
        for (FantasyEntityProfile profile : profiles) {
            if (shown >= 7) {
                break;
            }
            lines.add("#" + profile.getId()
                    + " T" + profile.getTypeId()
                    + " " + profile.getTypeName()
                    + " " + profile.getEntityClass());
            shown++;
        }
        lines.add("Total " + group.name() + ": " + profiles.size());
        showSimplePanel("FANTASY SAMPLE", lines);
    }

    private static RpgActionResult equipFirstAvailableGear(RpgRuntimeService runtime) {
        for (InventorySystem.InventoryEntry entry : RpgSystems.getProfile().getInventory().getEntries()) {
            if (entry.getDefinition().getSlot() != null) {
                return runtime.equipFromInventory(entry.getDefinition().getId());
            }
        }
        return RpgActionResult.fail(RpgActionType.NONE, "No equippable item found.");
    }

    private static RpgActionResult useFirstConsumable(RpgRuntimeService runtime) {
        for (InventorySystem.InventoryEntry entry : RpgSystems.getProfile().getInventory().getEntries()) {
            if (entry.getDefinition().getKind() == dq1.core.rpg.ItemKind.CONSUMABLE) {
                return runtime.useConsumable(entry.getDefinition().getId());
            }
        }
        return RpgActionResult.fail(RpgActionType.NONE, "No consumable item found.");
    }

    private static void showClassSelector(RpgRuntimeService runtime) {
        CharacterClass[] classes = CharacterClass.values();
        String[] options = new String[classes.length + 1];
        for (int i = 0; i < classes.length; i++) {
            options[i] = classes[i].name();
        }
        options[classes.length] = "Back";
        int selection = Dialog.showOptionsMenu(10, 8, 20, classes.length + 3, -1, options);
        if (selection >= 0 && selection < classes.length) {
            RpgActionResult result = runtime.changeClass(classes[selection]);
            showUiToast(result.getMessage());
        }
    }

    private static void showFeatureCoverage() {
        List<String> lines = new ArrayList<>();
        lines.addAll(FeatureRegistry.buildSummaryLines());
        lines.add("Missing:");
        for (FeatureRegistry.FeatureEntry feature : FeatureRegistry.getMissingFeatures(4)) {
            lines.add("- " + feature.getName());
        }
        showSimplePanel("FEATURE COVERAGE", lines);
    }

    private static void showRpgCharacterSummary() {
        List<String> lines = RpgSystems.buildSummaryLines();
        showSimplePanel("RPG SUMMARY", lines);
    }

    private static void showRpgInventoryOverview() {
        PlayerRpgProfile profile = RpgSystems.getProfile();
        InventorySystem inventory = profile.getInventory();
        List<String> lines = new ArrayList<>();
        lines.add("Inventory slots: " + inventory.getUsedSlots()
                + "/" + inventory.getMaxSlots());
        lines.add("Item type catalog: 90");
        int shown = 0;
        for (InventorySystem.InventoryEntry entry : inventory.getEntries()) {
            if (shown >= 6) {
                break;
            }
            lines.add("- " + entry.getDefinition().getName()
                    + " x" + entry.getQuantity());
            shown++;
        }
        if (shown == 0) {
            lines.add("Inventory is empty.");
        }
        showSimplePanel("RPG INVENTORY", lines);
    }

    private static void showRpgBuffDebuffOverview() {
        PlayerRpgProfile profile = RpgSystems.getProfile();
        List<BuffDebuffManager.ActiveEffect> effects
                = profile.getBuffDebuffManager().getActiveEffects();
        List<String> lines = new ArrayList<>();
        lines.add("Active effects: " + effects.size());
        if (effects.isEmpty()) {
            lines.add("No buffs or debuffs.");
        }
        else {
            for (BuffDebuffManager.ActiveEffect effect : effects) {
                lines.add("- " + effect.getEffect().getName()
                        + " (" + (effect.getEffect().isBuff() ? "Buff" : "Debuff")
                        + ") turns=" + effect.getTurnsRemaining()
                        + " stacks=" + effect.getStacks());
            }
        }
        showSimplePanel("BUFFS / DEBUFFS", lines);
    }

    private static void showSimplePanel(String title, List<String> lines) {
        Dialog.drawBoxBorder(3, 4, 4, 34, 20);
        Dialog.fillBox(3, ' ', 5, 5, 33, 19);
        Dialog.printText(3, 6, 6, title);
        int row = 8;
        for (String line : lines) {
            if (row > 18) {
                break;
            }
            Dialog.printText(3, 6, row++, line);
        }
        Dialog.printText(3, 6, 19, "Press confirm/cancel");
        waitForFireOrEscKey();
        Dialog.hideOptionsMenu(3, 4, 4, 31, 16, "");
    }
}
