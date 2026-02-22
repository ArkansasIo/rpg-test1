package dq1.editor.audio;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class EditorAudioAPI {

    private static final Path PRESET_DIR = Path.of("assets/res/audio/presets");
    private static final Path AUDIO_DIR = Path.of("assets/res/audio");
    private static final Map<String, Path> presets = new HashMap<>();

    static {
        try {
            if (!Files.exists(PRESET_DIR)) Files.createDirectories(PRESET_DIR);
            if (!Files.exists(AUDIO_DIR)) Files.createDirectories(AUDIO_DIR);
            // load existing presets
            if (Files.exists(PRESET_DIR)) {
                Files.list(PRESET_DIR).filter(p -> p.toString().endsWith(".json")).forEach(p -> {
                    String name = p.getFileName().toString().replaceFirst("\\.json$", "");
                    presets.put(name, p);
                });
            }
        } catch (IOException ignored) {}
    }

    public static Map<String, Path> getPresets() {
        return Collections.unmodifiableMap(presets);
    }

    public static void registerPreset(String name, Path jsonFile) {
        presets.put(name, jsonFile);
    }

    public static File getAudioFile(String filename) {
        Path p = AUDIO_DIR.resolve(filename);
        File f = p.toFile();
        return f.exists() ? f : null;
    }

    public static void playPreset(String name) {
        try {
            Path p = presets.get(name);
            if (p == null) return;
            String json = Files.readString(p);
            // parse minimal json: expect keys freq, duration, amplitude (simple parser)
            double freq = parseDouble(json, "freq", 440.0);
            double duration = parseDouble(json, "duration", 0.25);
            double amp = parseDouble(json, "amplitude", 0.6);
            byte[] pcm = SfxGenerator.generateSine(freq, duration, 44100, amp);
            AudioPlaybackUtil.playBytes(pcm, 44100f);
        } catch (Exception ignored) {}
    }

    // Convenience: play an audio file from assets/res/audio by filename
    public static void playFileByName(String filename) {
        try {
            File f = getAudioFile(filename);
            if (f == null) return;
            AudioPlaybackUtil.playFile(f);
        } catch (Exception ignored) {}
    }

    private static double parseDouble(String json, String key, double def) {
        try {
            int idx = json.indexOf('"' + key + '"');
            if (idx < 0) return def;
            int colon = json.indexOf(':', idx);
            int comma = json.indexOf(',', colon);
            int end = comma > 0 ? comma : json.indexOf('}', colon);
            String val = json.substring(colon + 1, end).trim();
            return Double.parseDouble(val);
        } catch (Exception e) {
            return def;
        }
    }
}