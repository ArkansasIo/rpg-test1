package dq1.core;

import java.util.ArrayList;
import java.util.List;

public class StorySystem {
    public static final int ACT_COUNT = 12;
    public static final int CHAPTERS_PER_ACT = 50;

    private final List<Act> acts = new ArrayList<>();

    public StorySystem() {
        for (int i = 0; i < ACT_COUNT; i++) {
            acts.add(new Act(i + 1));
        }
    }

    public Act getAct(int actNum) {
        if (actNum < 1 || actNum > ACT_COUNT) return null;
        return acts.get(actNum - 1);
    }

    public static class Act {
        public final int actNumber;
        public final List<Chapter> chapters = new ArrayList<>();
        public Act(int actNumber) {
            this.actNumber = actNumber;
            for (int i = 0; i < CHAPTERS_PER_ACT; i++) {
                chapters.add(new Chapter(i + 1));
            }
        }
        public Chapter getChapter(int chapterNum) {
            if (chapterNum < 1 || chapterNum > CHAPTERS_PER_ACT) return null;
            return chapters.get(chapterNum - 1);
        }
    }

    public static class Chapter {
        public final int chapterNumber;
        public final List<Quest> quests = new ArrayList<>();
        public final List<Quest> sideQuests = new ArrayList<>();
        public Chapter(int chapterNumber) {
            this.chapterNumber = chapterNumber;
        }
        public void addQuest(String name, String description) {
            quests.add(new Quest(name, description, false));
        }
        public void addSideQuest(String name, String description) {
            sideQuests.add(new Quest(name, description, true));
        }
    }

    public static class Quest {
        public final String name;
        public final String description;
        public final boolean isSideQuest;
        public Quest(String name, String description, boolean isSideQuest) {
            this.name = name;
            this.description = description;
            this.isSideQuest = isSideQuest;
        }
    }
}