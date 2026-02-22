package mmorpg.framework.spec;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public final class GameLogService {

    public enum Channel {
        SYSTEM,
        COMBAT,
        LOOT,
        QUEST,
        SECURITY,
        ADMIN
    }

    public static final class Entry {
        public final Instant timestamp;
        public final Channel channel;
        public final String message;

        public Entry(Instant timestamp, Channel channel, String message) {
            this.timestamp = timestamp;
            this.channel = channel;
            this.message = message;
        }
    }

    private final List<Entry> entries = new ArrayList<>();

    public void log(Channel channel, String message) {
        entries.add(new Entry(Instant.now(), channel, message));
        if (entries.size() > 2000) {
            entries.remove(0);
        }
    }

    public List<Entry> tail(int max) {
        int safe = Math.max(1, max);
        int start = Math.max(0, entries.size() - safe);
        return new ArrayList<>(entries.subList(start, entries.size()));
    }

    public List<String> tailLines(int max) {
        List<String> lines = new ArrayList<>();
        for (Entry e : tail(max)) {
            lines.add(e.timestamp + " [" + e.channel + "] " + e.message);
        }
        if (lines.isEmpty()) {
            lines.add("(no logs)");
        }
        return lines;
    }
}
