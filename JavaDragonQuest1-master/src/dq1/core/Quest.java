s adn classpackage dq1.core;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

/**
 * Quest system for RPG.
 * Tracks quest progress, completion, and rewards.
 * @author GitHub Copilot
 */
public class Quest {
    public static class QuestData {
        public String id;
        public String name;
        public String description;
        public boolean completed;
        public List<String> objectives = new ArrayList<>();
        public List<String> rewards = new ArrayList<>();
        public QuestData(String id, String name, String description) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.completed = false;
        }
    }
    private static final Map<String, QuestData> QUESTS = new HashMap<>();

    public static void addQuest(String id, String name, String description) {
        QUESTS.put(id, new QuestData(id, name, description));
    }
    public static void addObjective(String questId, String objective) {
        QuestData q = QUESTS.get(questId);
        if (q != null) q.objectives.add(objective);
    }
    public static void addReward(String questId, String reward) {
        QuestData q = QUESTS.get(questId);
        if (q != null) q.rewards.add(reward);
    }
    public static void completeQuest(String questId) {
        QuestData q = QUESTS.get(questId);
        if (q != null) q.completed = true;
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
}
