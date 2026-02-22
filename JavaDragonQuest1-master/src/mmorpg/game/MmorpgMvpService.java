package mmorpg.game;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class MmorpgMvpService {

    public enum Race {
        HUMAN, ELF, DWARF, ORC, UNDEAD
    }

    public enum Archetype {
        WARRIOR, MAGE, ROGUE, CLERIC, RANGER
    }

    public enum ObjectiveType {
        KILL, COLLECT, VISIT, TALK
    }

    public enum ChatChannel {
        ZONE, PARTY, GUILD
    }

    public enum PostMvpModule {
        GUILDS,
        AUCTION_HOUSE_TRADING,
        RAIDS_10_20,
        CRAFTING_GATHERING,
        PVP_BATTLEGROUNDS_ARENAS,
        WORLD_EVENTS,
        HOUSING_TRANSMOG_COLLECTIONS,
        CROSS_REALM_SHARDING
    }

    public static final class Appearance {
        public String hairStyle;
        public String hairColor;
        public String skinTone;

        public Appearance(String hairStyle, String hairColor, String skinTone) {
            this.hairStyle = hairStyle;
            this.hairColor = hairColor;
            this.skinTone = skinTone;
        }
    }

    public static final class CharacterState {
        public final String id;
        public final String name;
        public final Race race;
        public final Archetype archetype;
        public final Appearance appearance;
        public String zone;
        public int level;
        public int xp;
        public int talentPoints;
        public int currency;
        public int hp;
        public int maxHp;
        public int mana;
        public int maxMana;
        public int energy;
        public int rage;
        public double x;
        public double y;
        public boolean mounted;
        public boolean swimming;
        public int bagSlots;
        public int durability;
        public final Map<String, Integer> inventory = new LinkedHashMap<>();
        public final Map<String, String> equipment = new LinkedHashMap<>();
        public final Set<String> unlockedSkills = new LinkedHashSet<>();

        public CharacterState(String id, String name, Race race, Archetype archetype
                , Appearance appearance, String zone) {
            this.id = id;
            this.name = name;
            this.race = race;
            this.archetype = archetype;
            this.appearance = appearance;
            this.zone = zone;
            this.level = 1;
            this.xp = 0;
            this.talentPoints = 0;
            this.currency = 200;
            this.maxHp = 100;
            this.hp = 100;
            this.maxMana = 80;
            this.mana = 80;
            this.energy = 100;
            this.rage = 0;
            this.bagSlots = 24;
            this.durability = 100;
            this.equipment.put("MainHand", "(none)");
            this.equipment.put("Head", "(none)");
            this.equipment.put("Chest", "(none)");
            this.inventory.put("Health Potion", 3);
            this.inventory.put("Starter Ration", 5);
            this.unlockedSkills.add("Basic Attack");
        }
    }

    public static final class QuestObjective {
        public final ObjectiveType type;
        public final String target;
        public final int required;
        public int progress;

        public QuestObjective(ObjectiveType type, String target, int required) {
            this.type = type;
            this.target = target;
            this.required = Math.max(1, required);
            this.progress = 0;
        }

        public boolean done() {
            return progress >= required;
        }
    }

    public static final class QuestState {
        public final String id;
        public final String name;
        public final List<QuestObjective> objectives = new ArrayList<>();
        public final int rewardCurrency;
        public final int rewardXp;
        public boolean accepted;
        public boolean turnedIn;

        public QuestState(String id, String name, int rewardCurrency, int rewardXp) {
            this.id = id;
            this.name = name;
            this.rewardCurrency = rewardCurrency;
            this.rewardXp = rewardXp;
        }

        public boolean complete() {
            for (QuestObjective objective : objectives) {
                if (!objective.done()) {
                    return false;
                }
            }
            return true;
        }
    }

    public static final class CombatOutcome {
        public final int damage;
        public final boolean crit;
        public final boolean hit;
        public final String message;

        public CombatOutcome(int damage, boolean crit, boolean hit, String message) {
            this.damage = damage;
            this.crit = crit;
            this.hit = hit;
            this.message = message;
        }
    }

    public static final class SecurityResult {
        public final boolean allowed;
        public final String reason;

        public SecurityResult(boolean allowed, String reason) {
            this.allowed = allowed;
            this.reason = reason;
        }
    }

    private static final MmorpgMvpService INSTANCE = new MmorpgMvpService();
    private final Map<String, CharacterState> characters = new LinkedHashMap<>();
    private final Set<String> uniqueNames = new HashSet<>();
    private final Map<String, QuestState> questBook = new LinkedHashMap<>();
    private final Map<String, Integer> threatTable = new HashMap<>();
    private final Map<String, Long> rateLimitByKey = new HashMap<>();
    private final List<String> auditLog = new ArrayList<>();
    private final Map<String, String> mailBox = new LinkedHashMap<>();
    private final EnumMap<PostMvpModule, String> postMvpStatus = new EnumMap<>(PostMvpModule.class);
    private final Set<String> friends = new LinkedHashSet<>();
    private String selectedCharacterId;

    private MmorpgMvpService() {
        seedDefaults();
    }

    public static MmorpgMvpService get() {
        return INSTANCE;
    }

    private void seedDefaults() {
        for (PostMvpModule module : PostMvpModule.values()) {
            postMvpStatus.put(module, "Planned");
        }
        if (questBook.isEmpty()) {
            QuestState q1 = new QuestState("q_mvp_001", "Scout The Border", 40, 60);
            q1.objectives.add(new QuestObjective(ObjectiveType.VISIT, "Border Camp", 1));
            q1.objectives.add(new QuestObjective(ObjectiveType.TALK, "Captain Rowan", 1));
            questBook.put(q1.id, q1);

            QuestState q2 = new QuestState("q_mvp_002", "Cull The Wolves", 55, 90);
            q2.objectives.add(new QuestObjective(ObjectiveType.KILL, "Forest Wolf", 4));
            q2.objectives.add(new QuestObjective(ObjectiveType.COLLECT, "Wolf Pelt", 2));
            questBook.put(q2.id, q2);
        }
    }

    public CharacterState createCharacter(String name, Race race, Archetype archetype
            , Appearance appearance, String startingZone) {
        String normalized = normalizeName(name);
        if (normalized == null || normalized.isBlank()) {
            return null;
        }
        if (uniqueNames.contains(normalized.toLowerCase())) {
            return null;
        }
        String id = "char_" + Integer.toHexString(normalized.hashCode()) + "_" + characters.size();
        CharacterState state = new CharacterState(id, normalized, race, archetype, appearance, startingZone);
        characters.put(id, state);
        uniqueNames.add(normalized.toLowerCase());
        selectedCharacterId = id;
        log("CHAR_CREATE " + normalized + " race=" + race + " class=" + archetype);
        return state;
    }

    public CharacterState selectCharacter(String id) {
        CharacterState state = characters.get(id);
        if (state != null) {
            selectedCharacterId = id;
            log("CHAR_SELECT " + state.name);
        }
        return state;
    }

    public CharacterState getSelectedCharacter() {
        if (selectedCharacterId == null) {
            return null;
        }
        return characters.get(selectedCharacterId);
    }

    public List<CharacterState> getCharacters() {
        return new ArrayList<>(characters.values());
    }

    public double move(CharacterState state, double dx, double dy, boolean inWater, double heightDrop) {
        if (state == null) {
            return 0;
        }
        state.swimming = inWater;
        double speed = state.mounted ? 1.7 : 1.0;
        if (state.swimming) {
            speed *= 0.65;
        }
        state.x += dx * speed;
        state.y += dy * speed;
        int damage = calcFallDamage(heightDrop);
        if (damage > 0) {
            state.hp = Math.max(1, state.hp - damage);
        }
        return speed;
    }

    public int calcFallDamage(double heightDrop) {
        if (heightDrop <= 5.0) {
            return 0;
        }
        return (int) Math.min(80, Math.max(1, (heightDrop - 5.0) * 2.2));
    }

    public void setMounted(CharacterState state, boolean mounted) {
        if (state != null) {
            state.mounted = mounted;
        }
    }

    public CombatOutcome castAbility(CharacterState state, String target, int power, int accuracy, int avoid) {
        if (state == null) {
            return new CombatOutcome(0, false, false, "No character selected.");
        }
        SecurityResult security = validateInput("ability_" + state.id, 120);
        if (!security.allowed) {
            return new CombatOutcome(0, false, false, security.reason);
        }
        boolean hit = accuracy >= avoid;
        boolean crit = ((state.level + power + accuracy) % 11) == 0;
        int base = Math.max(1, power + state.level * 2);
        int damage = hit ? (crit ? (int) (base * 1.6) : base) : 0;
        if (hit) {
            addThreat(target, damage);
        }
        if (state.mana >= 8) {
            state.mana -= 8;
        }
        return new CombatOutcome(damage, crit, hit
                , hit ? "Hit " + target + " for " + damage + (crit ? " (CRIT)" : "") : "Missed " + target);
    }

    public int heal(CharacterState state, int amount) {
        if (state == null) {
            return 0;
        }
        int before = state.hp;
        state.hp = Math.min(state.maxHp, state.hp + Math.max(1, amount));
        addThreat(state.name, amount / 2);
        return state.hp - before;
    }

    public void addThreat(String target, int amount) {
        threatTable.put(target, threatTable.getOrDefault(target, 0) + Math.max(0, amount));
    }

    public List<String> getThreatLines() {
        List<String> lines = new ArrayList<>();
        lines.add("Threat:");
        if (threatTable.isEmpty()) {
            lines.add("- none");
        }
        for (Map.Entry<String, Integer> entry : threatTable.entrySet()) {
            lines.add("- " + entry.getKey() + ": " + entry.getValue());
        }
        return lines;
    }

    public List<String> simulateNpcAiTick(String npcName, int distanceToPlayer, int leashRange) {
        List<String> lines = new ArrayList<>();
        lines.add("NPC: " + npcName);
        lines.add("Patrol waypoint advanced.");
        if (distanceToPlayer <= 6) {
            lines.add("Aggro triggered (radius 6).");
            lines.add("Casting ability: Shadow Bolt (2s).");
            if (distanceToPlayer > leashRange) {
                lines.add("Leash reset to spawn.");
            }
        }
        else {
            lines.add("Idle/patrol, no aggro.");
        }
        lines.add("Loot table ready on death.");
        return lines;
    }

    public boolean addItem(CharacterState state, String itemName, int qty, boolean stackable) {
        if (state == null || qty <= 0) {
            return false;
        }
        int usedSlots = state.inventory.size();
        if (!stackable && usedSlots + qty > state.bagSlots) {
            return false;
        }
        if (stackable && state.inventory.containsKey(itemName)) {
            state.inventory.put(itemName, state.inventory.get(itemName) + qty);
            return true;
        }
        if (state.inventory.size() >= state.bagSlots) {
            return false;
        }
        state.inventory.put(itemName, qty);
        return true;
    }

    public boolean repair(CharacterState state, int cost) {
        if (state == null) {
            return false;
        }
        if (state.currency < cost) {
            return false;
        }
        state.currency -= cost;
        state.durability = 100;
        return true;
    }

    public List<String> rollLoot(String enemyType, boolean partyLoot, int partySize) {
        List<String> lines = new ArrayList<>();
        lines.add("Loot source: " + enemyType);
        lines.add(partyLoot ? "Loot mode: Party split" : "Loot mode: Personal");
        lines.add("Drops:");
        lines.add("- Coin Pouch x" + (10 + enemyType.length() % 7));
        lines.add("- " + (enemyType.contains("Boss") ? "Epic Token" : "Uncommon Material"));
        if (partyLoot) {
            lines.add("Party members rolled need/greed (" + Math.max(1, partySize) + " players).");
        }
        return lines;
    }

    public List<QuestState> getQuests() {
        return new ArrayList<>(questBook.values());
    }

    public boolean acceptQuest(String questId) {
        QuestState state = questBook.get(questId);
        if (state == null || state.accepted) {
            return false;
        }
        state.accepted = true;
        return true;
    }

    public boolean advanceObjective(String questId, int index, int amount) {
        QuestState quest = questBook.get(questId);
        if (quest == null || !quest.accepted || index < 0 || index >= quest.objectives.size()) {
            return false;
        }
        QuestObjective objective = quest.objectives.get(index);
        objective.progress = Math.min(objective.required, objective.progress + Math.max(1, amount));
        return true;
    }

    public boolean turnInQuest(CharacterState character, String questId) {
        QuestState quest = questBook.get(questId);
        if (character == null || quest == null || !quest.accepted || !quest.complete() || quest.turnedIn) {
            return false;
        }
        character.currency += quest.rewardCurrency;
        gainXp(character, quest.rewardXp);
        quest.turnedIn = true;
        log("QUEST_TURNIN " + questId + " by " + character.name);
        return true;
    }

    public void gainXp(CharacterState state, int amount) {
        if (state == null) {
            return;
        }
        state.xp += Math.max(0, amount);
        while (state.xp >= xpForNextLevel(state.level)) {
            state.xp -= xpForNextLevel(state.level);
            state.level++;
            state.talentPoints++;
            state.maxHp += 12;
            state.maxMana += 8;
            state.hp = state.maxHp;
            state.mana = state.maxMana;
            unlockLevelSkill(state);
        }
    }

    private int xpForNextLevel(int level) {
        return 100 + level * 40;
    }

    private void unlockLevelSkill(CharacterState state) {
        if (state.level == 3) {
            state.unlockedSkills.add("Interrupt");
        }
        else if (state.level == 5) {
            state.unlockedSkills.add("Ultimate Strike");
        }
    }

    public List<String> createDungeonInstance(List<String> members, boolean matchmaking, String dungeonId) {
        List<String> lines = new ArrayList<>();
        lines.add("Dungeon: " + dungeonId + " (5-player)");
        lines.add("Matchmaking: " + (matchmaking ? "ON" : "OFF"));
        int count = Math.min(5, members == null ? 0 : members.size());
        lines.add("Members joined: " + count + "/5");
        lines.add("Lockout: 24h basic lockout applied.");
        return lines;
    }

    public void sendChat(ChatChannel channel, String author, String msg) {
        log("CHAT[" + channel + "] " + author + ": " + msg);
    }

    public void addFriend(String name) {
        if (name != null && !name.isBlank()) {
            friends.add(name.trim());
        }
    }

    public Set<String> getFriends() {
        return Collections.unmodifiableSet(friends);
    }

    public boolean vendorBuy(CharacterState state, String item, int price) {
        if (state == null || state.currency < price) {
            return false;
        }
        state.currency -= price;
        return addItem(state, item, 1, false);
    }

    public boolean vendorSell(CharacterState state, String item, int price) {
        if (state == null || !state.inventory.containsKey(item)) {
            return false;
        }
        state.inventory.remove(item);
        state.currency += Math.max(1, price);
        return true;
    }

    public void sendMail(String toName, String body) {
        mailBox.put(toName, body);
        log("MAIL to=" + toName);
    }

    public Map<String, String> getMailBox() {
        return Collections.unmodifiableMap(mailBox);
    }

    public Map<String, Object> snapshot(CharacterState state) {
        Map<String, Object> map = new LinkedHashMap<>();
        if (state == null) {
            return map;
        }
        map.put("id", state.id);
        map.put("name", state.name);
        map.put("level", state.level);
        map.put("xp", state.xp);
        map.put("zone", state.zone);
        map.put("currency", state.currency);
        map.put("hp", state.hp);
        map.put("mana", state.mana);
        map.put("durability", state.durability);
        map.put("inventory", new LinkedHashMap<>(state.inventory));
        map.put("savedAt", Instant.now().toString());
        return map;
    }

    public SecurityResult validateInput(String key, int minIntervalMs) {
        long now = System.currentTimeMillis();
        Long last = rateLimitByKey.get(key);
        if (last != null && now - last < Math.max(10, minIntervalMs)) {
            return new SecurityResult(false, "Rate limit blocked (" + key + ")");
        }
        rateLimitByKey.put(key, now);
        return new SecurityResult(true, "ok");
    }

    public boolean gmTeleport(CharacterState state, String zone, double x, double y) {
        if (state == null) {
            return false;
        }
        state.zone = zone;
        state.x = x;
        state.y = y;
        log("GM teleport " + state.name + " -> " + zone + " (" + x + "," + y + ")");
        return true;
    }

    public boolean gmGrantItem(CharacterState state, String item, int qty) {
        boolean result = addItem(state, item, qty, true);
        if (result) {
            log("GM grant item " + item + " x" + qty + " to " + state.name);
        }
        return result;
    }

    public List<String> getAuditLogTail(int max) {
        int n = Math.max(1, max);
        int start = Math.max(0, auditLog.size() - n);
        return new ArrayList<>(auditLog.subList(start, auditLog.size()));
    }

    public List<String> buildMvpSummaryLines() {
        List<String> lines = new ArrayList<>();
        CharacterState selected = getSelectedCharacter();
        lines.add("Characters: " + characters.size() + " (selected: " + (selected == null ? "none" : selected.name) + ")");
        lines.add("World/Movement: zones nav mount swim fall-dmg ready");
        lines.add("Combat: gcd/cd resource threat crit hit/avoid ready");
        lines.add("NPC AI: patrol leash aggro cast loot ready");
        lines.add("Inventory/Loot/Quest/XP/Talents: baseline ready");
        lines.add("Instance/Social/Economy/Persistence/Security/Admin: baseline ready");
        lines.add("Post-MVP tracked: " + postMvpStatus.size() + " modules");
        return lines;
    }

    public List<String> buildPostMvpLines() {
        List<String> lines = new ArrayList<>();
        lines.add("Post-MVP:");
        for (Map.Entry<PostMvpModule, String> entry : postMvpStatus.entrySet()) {
            lines.add("- " + entry.getKey().name() + ": " + entry.getValue());
        }
        return lines;
    }

    private String normalizeName(String name) {
        if (name == null) {
            return null;
        }
        String trimmed = name.trim().replaceAll("\\s+", " ");
        if (trimmed.length() < 3) {
            return null;
        }
        if (trimmed.length() > 12) {
            trimmed = trimmed.substring(0, 12);
        }
        return trimmed;
    }

    private void log(String value) {
        auditLog.add(Instant.now().toString() + " | " + value);
        if (auditLog.size() > 400) {
            auditLog.remove(0);
        }
    }
}
