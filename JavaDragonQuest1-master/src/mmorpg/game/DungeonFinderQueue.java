package mmorpg.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public final class DungeonFinderQueue {

    public enum Role {
        TANK, HEALER, DPS
    }

    public static final class QueueEntry {
        public final String playerName;
        public final int level;
        public final Role role;
        public final long queuedAt;

        public QueueEntry(String playerName, int level, Role role, long queuedAt) {
            this.playerName = playerName;
            this.level = level;
            this.role = role;
            this.queuedAt = queuedAt;
        }
    }

    public static final class MatchGroup {
        public final List<QueueEntry> members;

        public MatchGroup(List<QueueEntry> members) {
            this.members = Collections.unmodifiableList(members);
        }
    }

    private static final List<QueueEntry> queue = new ArrayList<>();

    private DungeonFinderQueue() {
    }

    public static void reset() {
        queue.clear();
    }

    public static boolean enqueue(String playerName, int level, Role role) {
        if (playerName == null || playerName.isBlank()) {
            return false;
        }
        for (QueueEntry entry : queue) {
            if (entry.playerName.equalsIgnoreCase(playerName)) {
                return false;
            }
        }
        queue.add(new QueueEntry(playerName.trim(), Math.max(1, level), role, System.currentTimeMillis()));
        return true;
    }

    public static boolean leaveQueue(String playerName) {
        Iterator<QueueEntry> it = queue.iterator();
        while (it.hasNext()) {
            QueueEntry entry = it.next();
            if (entry.playerName.equalsIgnoreCase(playerName)) {
                it.remove();
                return true;
            }
        }
        return false;
    }

    public static List<QueueEntry> getQueueSnapshot() {
        return Collections.unmodifiableList(new ArrayList<>(queue));
    }

    public static MatchGroup tryCreateMatch() {
        QueueEntry tank = findOldest(Role.TANK);
        QueueEntry healer = findOldest(Role.HEALER);
        List<QueueEntry> dps = findOldest(Role.DPS, 3);
        if (tank == null || healer == null || dps.size() < 3) {
            return null;
        }
        List<QueueEntry> members = new ArrayList<>();
        members.add(tank);
        members.add(healer);
        members.addAll(dps);
        removeMembers(members);
        return new MatchGroup(members);
    }

    public static List<String> buildQueueLines() {
        List<String> lines = new ArrayList<>();
        int tankCount = 0;
        int healerCount = 0;
        int dpsCount = 0;
        for (QueueEntry entry : queue) {
            switch (entry.role) {
                case TANK:
                    tankCount++;
                    break;
                case HEALER:
                    healerCount++;
                    break;
                case DPS:
                    dpsCount++;
                    break;
            }
        }
        lines.add("Queue size: " + queue.size());
        lines.add("Tank: " + tankCount + " Healer: " + healerCount + " DPS: " + dpsCount);
        int shown = 0;
        for (QueueEntry entry : queue) {
            lines.add("- " + entry.playerName + " Lv." + entry.level + " [" + entry.role + "]");
            shown++;
            if (shown >= 5) {
                break;
            }
        }
        if (shown == 0) {
            lines.add("Queue is empty.");
        }
        return lines;
    }

    private static QueueEntry findOldest(Role role) {
        QueueEntry best = null;
        for (QueueEntry entry : queue) {
            if (entry.role != role) {
                continue;
            }
            if (best == null || entry.queuedAt < best.queuedAt) {
                best = entry;
            }
        }
        return best;
    }

    private static List<QueueEntry> findOldest(Role role, int count) {
        List<QueueEntry> results = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            QueueEntry oldest = null;
            for (QueueEntry entry : queue) {
                if (entry.role != role || results.contains(entry)) {
                    continue;
                }
                if (oldest == null || entry.queuedAt < oldest.queuedAt) {
                    oldest = entry;
                }
            }
            if (oldest != null) {
                results.add(oldest);
            }
        }
        return results;
    }

    private static void removeMembers(List<QueueEntry> members) {
        queue.removeAll(members);
    }
}
