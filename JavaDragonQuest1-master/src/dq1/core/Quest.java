package dq1.core;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * WoW-style quest and story system.
 */
public class Quest {

    public static enum QuestCategory {
        MAIN_STORY, SIDE, DUNGEON, RAID, WORLD, PROFESSION
    }

    public static enum QuestStatus {
        LOCKED, AVAILABLE, ACTIVE, COMPLETED, TURNED_IN, FAILED
    }

    public static class ObjectiveData {
        public final String description;
        public final int targetCount;
        public int currentCount;

        public ObjectiveData(String description, int targetCount) {
            this.description = description;
            this.targetCount = Math.max(1, targetCount);
        }

        public boolean isCompleted() {
            return currentCount >= targetCount;
        }

        public String getProgressText() {
            return description + " (" + currentCount + "/" + targetCount + ")";
        }
    }

    public static class StoryChapter {
        public final String id;
        public final String title;
        public final String description;
        public final List<String> questIds = new ArrayList<>();

        public StoryChapter(String id, String title, String description) {
            this.id = id;
            this.title = title;
            this.description = description;
        }
    }

    public static class QuestData {
        public String id;
        public String name;
        public String description;
        public boolean completed;
        public List<String> objectives = new ArrayList<>();
        public List<String> rewards = new ArrayList<>();

        public String chapterId;
        public QuestCategory category = QuestCategory.SIDE;
        public QuestStatus status = QuestStatus.AVAILABLE;
        public final List<ObjectiveData> objectiveData = new ArrayList<>();
        public int requiredLevel = 1;
        public boolean repeatable;
        public boolean daily;
        public boolean weekly;

        public QuestData(String id, String name, String description) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.completed = false;
        }

        public boolean areAllObjectivesCompleted() {
            if (objectiveData.isEmpty()) {
                return completed;
            }
            for (ObjectiveData objective : objectiveData) {
                if (!objective.isCompleted()) {
                    return false;
                }
            }
            return true;
        }
    }

    private static final Map<String, QuestData> QUESTS = new LinkedHashMap<>();
    private static final Map<String, StoryChapter> CHAPTERS = new LinkedHashMap<>();
    private static boolean initialized;

    public static void initializeWoWStoryIfNeeded() {
        if (initialized) {
            return;
        }
        initialized = true;

        addChapter("chapter_1", "The Call To Arms",
                "Azeroth is threatened by a growing shadow.");
        addChapter("chapter_2", "Allies And Relics",
                "Gather allies and sacred relics for the war ahead.");
        addChapter("chapter_3", "Siege Of The Dragon Keep",
                "Lead the final assault against the Dragon Lord.");

        addStoryQuest("q_main_001", "chapter_1", "A King's Request",
                "Meet the king and learn of the realm's crisis.", QuestCategory.MAIN_STORY);
        addObjective("q_main_001", "Speak to the King");
        addReward("q_main_001", "XP +100");
        addReward("q_main_001", "Gold +50");
        setStatus("q_main_001", QuestStatus.AVAILABLE);

        addStoryQuest("q_main_002", "chapter_1", "Scout The Wilds",
                "Scout the nearby region for signs of dragon cults.", QuestCategory.MAIN_STORY);
        addObjective("q_main_002", "Discover 3 wilderness landmarks", 3);
        addReward("q_main_002", "XP +150");
        addReward("q_main_002", "Potion x2");
        setStatus("q_main_002", QuestStatus.LOCKED);

        addStoryQuest("q_main_003", "chapter_2", "Relic Of Light",
                "Recover the relic hidden deep in the shrine.", QuestCategory.DUNGEON);
        addObjective("q_main_003", "Enter Shrine");
        addObjective("q_main_003", "Defeat relic guardian");
        addReward("q_main_003", "XP +300");
        addReward("q_main_003", "Relic of Light");
        setStatus("q_main_003", QuestStatus.LOCKED);

        addStoryQuest("q_main_004", "chapter_2", "Rally The Towns",
                "Inspire nearby settlements to join your cause.", QuestCategory.WORLD);
        addObjective("q_main_004", "Gain support from 4 towns", 4);
        addReward("q_main_004", "XP +260");
        addReward("q_main_004", "Gold +200");
        setStatus("q_main_004", QuestStatus.LOCKED);

        addStoryQuest("q_main_005", "chapter_3", "Dragon Keep Assault",
                "Lead a coordinated assault on the Dragon Keep.", QuestCategory.RAID);
        addObjective("q_main_005", "Reach the keep gates");
        addObjective("q_main_005", "Defeat Dragon Vanguard", 3);
        addObjective("q_main_005", "Confront Dragon Lord");
        addReward("q_main_005", "XP +600");
        addReward("q_main_005", "Legendary Crest");
        setStatus("q_main_005", QuestStatus.LOCKED);
    }

    public static void addChapter(String id, String title, String description) {
        CHAPTERS.put(id, new StoryChapter(id, title, description));
    }

    public static List<StoryChapter> getStoryChapters() {
        return new ArrayList<>(CHAPTERS.values());
    }

    public static String getChapterProgressText(String chapterId) {
        StoryChapter chapter = CHAPTERS.get(chapterId);
        if (chapter == null) {
            return "Unknown chapter";
        }
        int total = chapter.questIds.size();
        int done = 0;
        for (String questId : chapter.questIds) {
            QuestData quest = QUESTS.get(questId);
            if (quest != null && (quest.status == QuestStatus.TURNED_IN
                    || quest.status == QuestStatus.COMPLETED)) {
                done++;
            }
        }
        return done + "/" + total + " completed";
    }

    public static void addStoryQuest(String id, String chapterId, String name,
                                     String description, QuestCategory category) {
        QuestData quest = new QuestData(id, name, description);
        quest.chapterId = chapterId;
        quest.category = category;
        QUESTS.put(id, quest);
        StoryChapter chapter = CHAPTERS.get(chapterId);
        if (chapter != null) {
            chapter.questIds.add(id);
        }
    }

    public static void addQuest(String id, String name, String description) {
        QuestData quest = new QuestData(id, name, description);
        quest.category = QuestCategory.SIDE;
        quest.status = QuestStatus.AVAILABLE;
        QUESTS.put(id, quest);
    }

    public static void addObjective(String questId, String objective) {
        addObjective(questId, objective, 1);
    }

    public static void addObjective(String questId, String objective, int targetCount) {
        QuestData q = QUESTS.get(questId);
        if (q != null) {
            q.objectives.add(objective);
            q.objectiveData.add(new ObjectiveData(objective, targetCount));
        }
    }

    public static void addReward(String questId, String reward) {
        QuestData q = QUESTS.get(questId);
        if (q != null) {
            q.rewards.add(reward);
        }
    }

    public static void completeQuest(String questId) {
        QuestData q = QUESTS.get(questId);
        if (q != null) {
            q.completed = true;
            q.status = QuestStatus.COMPLETED;
        }
    }

    public static boolean acceptQuest(String questId) {
        QuestData quest = QUESTS.get(questId);
        if (quest == null || (quest.status != QuestStatus.AVAILABLE
                && quest.status != QuestStatus.LOCKED)) {
            return false;
        }
        if (quest.status == QuestStatus.LOCKED) {
            return false;
        }
        quest.status = QuestStatus.ACTIVE;
        return true;
    }

    public static boolean acceptFirstAvailableQuest() {
        for (QuestData quest : QUESTS.values()) {
            if (quest.status == QuestStatus.AVAILABLE) {
                return acceptQuest(quest.id);
            }
        }
        return false;
    }

    public static boolean progressObjective(String questId, int objectiveIndex, int amount) {
        QuestData quest = QUESTS.get(questId);
        if (quest == null || quest.status != QuestStatus.ACTIVE
                || objectiveIndex < 0 || objectiveIndex >= quest.objectiveData.size()) {
            return false;
        }
        ObjectiveData objective = quest.objectiveData.get(objectiveIndex);
        objective.currentCount = Math.min(objective.targetCount, objective.currentCount + Math.max(1, amount));

        if (quest.areAllObjectivesCompleted()) {
            quest.completed = true;
            quest.status = QuestStatus.COMPLETED;
            unlockNextStoryQuest(quest.id);
        }
        return true;
    }

    public static boolean progressFirstActiveQuest() {
        for (QuestData quest : QUESTS.values()) {
            if (quest.status == QuestStatus.ACTIVE && !quest.objectiveData.isEmpty()) {
                for (int i = 0; i < quest.objectiveData.size(); i++) {
                    if (!quest.objectiveData.get(i).isCompleted()) {
                        return progressObjective(quest.id, i, 1);
                    }
                }
            }
        }
        return false;
    }

    public static boolean turnInQuest(String questId) {
        QuestData quest = QUESTS.get(questId);
        if (quest == null || quest.status != QuestStatus.COMPLETED) {
            return false;
        }
        quest.status = QuestStatus.TURNED_IN;
        quest.completed = true;
        return true;
    }

    public static boolean turnInFirstCompletedQuest() {
        for (QuestData quest : QUESTS.values()) {
            if (quest.status == QuestStatus.COMPLETED) {
                return turnInQuest(quest.id);
            }
        }
        return false;
    }

    public static List<QuestData> getQuestLog() {
        return new ArrayList<>(QUESTS.values());
    }

    private static void setStatus(String questId, QuestStatus status) {
        QuestData quest = QUESTS.get(questId);
        if (quest != null) {
            quest.status = status;
        }
    }

    private static void unlockNextStoryQuest(String questId) {
        QuestData quest = QUESTS.get(questId);
        if (quest == null || quest.chapterId == null) {
            return;
        }
        StoryChapter chapter = CHAPTERS.get(quest.chapterId);
        if (chapter == null) {
            return;
        }
        int index = chapter.questIds.indexOf(questId);
        if (index >= 0 && index + 1 < chapter.questIds.size()) {
            QuestData next = QUESTS.get(chapter.questIds.get(index + 1));
            if (next != null && next.status == QuestStatus.LOCKED) {
                next.status = QuestStatus.AVAILABLE;
            }
        }
    }

    public static boolean isCompleted(String questId) {
        QuestData q = QUESTS.get(questId);
        return q != null && q.completed;
    }
    public static QuestData getQuest(String questId) {
        return QUESTS.get(questId);
    }

    public static List<QuestData> getAllQuests() {
        return new ArrayList<>(QUESTS.values());
    }

    public static void seedDemoSideQuestIfNeeded() {
        if (getQuest("demo_quest") != null) {
            return;
        }
        addQuest("demo_quest", "Find The King", "Visit Tantegel and speak with the king.");
        addObjective("demo_quest", "Reach Tantegel throne room");
        addReward("demo_quest", "Starter Gold");
    }
}
