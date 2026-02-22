package mmorpg.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class PartySyncService {

    public static final class PartyMember {
        public final String name;
        public int level;
        public String mapId;
        public boolean online;

        public PartyMember(String name, int level, String mapId, boolean online) {
            this.name = name;
            this.level = level;
            this.mapId = mapId;
            this.online = online;
        }
    }

    private static final List<PartyMember> members = new ArrayList<>();

    private PartySyncService() {
    }

    public static void initializeDefaults(String leaderName, int leaderLevel, String mapId) {
        members.clear();
        members.add(new PartyMember(leaderName, Math.max(1, leaderLevel), mapId, true));
        members.add(new PartyMember("Alyra", Math.max(1, leaderLevel - 1), mapId, true));
        members.add(new PartyMember("Doran", Math.max(1, leaderLevel - 2), mapId, true));
        members.add(new PartyMember("Mira", Math.max(1, leaderLevel - 1), mapId, false));
    }

    public static List<PartyMember> getMembers() {
        return Collections.unmodifiableList(members);
    }

    public static void syncLevelsToLeader() {
        if (members.isEmpty()) {
            return;
        }
        int leaderLevel = members.get(0).level;
        for (int i = 1; i < members.size(); i++) {
            PartyMember member = members.get(i);
            if (!member.online) {
                continue;
            }
            if (member.level < leaderLevel - 2) {
                member.level = leaderLevel - 2;
            }
            if (member.level > leaderLevel + 2) {
                member.level = leaderLevel + 2;
            }
        }
    }

    public static void syncAllToMap(String mapId) {
        for (PartyMember member : members) {
            if (member.online) {
                member.mapId = mapId;
            }
        }
    }

    public static boolean toggleOnline(String name) {
        for (PartyMember member : members) {
            if (member.name.equalsIgnoreCase(name)) {
                member.online = !member.online;
                return true;
            }
        }
        return false;
    }

    public static List<String> buildSummaryLines() {
        List<String> lines = new ArrayList<>();
        int onlineCount = 0;
        for (PartyMember member : members) {
            if (member.online) {
                onlineCount++;
            }
        }
        lines.add("Party size: " + members.size() + " (online " + onlineCount + ")");
        for (PartyMember member : members) {
            lines.add("- " + member.name + " Lv." + member.level
                    + " " + (member.online ? "ONLINE" : "OFFLINE")
                    + " @ " + member.mapId);
        }
        return lines;
    }
}
