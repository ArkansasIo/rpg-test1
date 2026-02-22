package dq1.editor.audio;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class AudioToolsPanel extends JPanel {

    private static final float DEFAULT_SAMPLE_RATE = 44100f;

    // playback control
    private volatile boolean playing = false;
    private SourceDataLine currentLine;

    public AudioToolsPanel() {
        setLayout(new BorderLayout(8,8));

        JPanel presets = new JPanel(new GridLayout(0,2,4,4));
        JButton magicBtn = new JButton("Generate Magic Pulse");
        JButton magicPlayBtn = new JButton("Play");
        JButton whooshBtn = new JButton("Generate Whoosh");
        JButton whooshPlayBtn = new JButton("Play");
        JButton sparkleBtn = new JButton("Generate Sparkle");
        JButton sparklePlayBtn = new JButton("Play");
        presets.add(magicBtn);
        presets.add(magicPlayBtn);
        presets.add(whooshBtn);
        presets.add(whooshPlayBtn);
        presets.add(sparkleBtn);
        presets.add(sparklePlayBtn);

        // SFX parameter controls
        JPanel params = new JPanel(new GridLayout(0,2,4,4));
        JSpinner freqSpinner = new JSpinner(new SpinnerNumberModel(440.0, 20.0, 20000.0, 1.0));
        JSpinner durSpinner = new JSpinner(new SpinnerNumberModel(0.25, 0.01, 10.0, 0.01));
        JSpinner ampSpinner = new JSpinner(new SpinnerNumberModel(0.6, 0.01, 1.0, 0.01));
        JButton genPlayBtn = new JButton("Play Generated");
        JButton savePresetBtn = new JButton("Save Preset");
        params.add(new JLabel("Frequency (Hz):")); params.add(freqSpinner);
        params.add(new JLabel("Duration (s):")); params.add(durSpinner);
        params.add(new JLabel("Amplitude (0-1):")); params.add(ampSpinner);
        params.add(genPlayBtn); params.add(savePresetBtn);

        JPanel top = new JPanel(new BorderLayout());
        top.add(presets, BorderLayout.NORTH);
        top.add(params, BorderLayout.SOUTH);
        add(top, BorderLayout.NORTH);

        JTextArea log = new JTextArea(10, 40);
        log.setEditable(false);
        add(new JScrollPane(log), BorderLayout.CENTER);

        // store last generated sounds so Play can use them without saving
        final byte[][] lastGenerated = new byte[3][]; // 0 magic,1 whoosh,2 sparkle

        magicBtn.addActionListener(e -> {
            try {
                byte[] pcm = SfxGenerator.magicPulse(DEFAULT_SAMPLE_RATE);
                lastGenerated[0] = pcm;
                File out = ensureAudioFolder().toPath().resolve("magic_pulse.wav").toFile();
                SfxGenerator.saveWav(out, pcm, DEFAULT_SAMPLE_RATE);
                log.append("Saved: " + out.getAbsolutePath() + "\n");
            } catch (Exception ex) {
                log.append("Error: " + ex.getMessage() + "\n");
            }
        });

        whooshBtn.addActionListener(e -> {
            try {
                byte[] pcm = SfxGenerator.whoosh(DEFAULT_SAMPLE_RATE);
                lastGenerated[1] = pcm;
                File out = ensureAudioFolder().toPath().resolve("whoosh.wav").toFile();
                SfxGenerator.saveWav(out, pcm, DEFAULT_SAMPLE_RATE);
                log.append("Saved: " + out.getAbsolutePath() + "\n");
            } catch (Exception ex) {
                log.append("Error: " + ex.getMessage() + "\n");
            }
        });

        sparkleBtn.addActionListener(e -> {
            try {
                byte[] pcm = SfxGenerator.sparkle(DEFAULT_SAMPLE_RATE);
                lastGenerated[2] = pcm;
                File out = ensureAudioFolder().toPath().resolve("sparkle.wav").toFile();
                SfxGenerator.saveWav(out, pcm, DEFAULT_SAMPLE_RATE);
                log.append("Saved: " + out.getAbsolutePath() + "\n");
            } catch (Exception ex) {
                log.append("Error: " + ex.getMessage() + "\n");
            }
        });

        magicPlayBtn.addActionListener(e -> {
            if (lastGenerated[0] == null) {
                log.append("No generated sound yet. Click Generate first.\n");
                return;
            }
            AudioPlaybackUtil.playBytes(lastGenerated[0], DEFAULT_SAMPLE_RATE);
        });

        whooshPlayBtn.addActionListener(e -> {
            if (lastGenerated[1] == null) {
                log.append("No generated sound yet. Click Generate first.\n");
                return;
            }
            AudioPlaybackUtil.playBytes(lastGenerated[1], DEFAULT_SAMPLE_RATE);
        });

        sparklePlayBtn.addActionListener(e -> {
            if (lastGenerated[2] == null) {
                log.append("No generated sound yet. Click Generate first.\n");
                return;
            }
            AudioPlaybackUtil.playBytes(lastGenerated[2], DEFAULT_SAMPLE_RATE);
        });

        genPlayBtn.addActionListener(e -> {
            double freq = ((Number) freqSpinner.getValue()).doubleValue();
            double dur = ((Number) durSpinner.getValue()).doubleValue();
            double amp = ((Number) ampSpinner.getValue()).doubleValue();
            byte[] pcm = SfxGenerator.generateSine(freq, dur, DEFAULT_SAMPLE_RATE, amp);
            AudioPlaybackUtil.playBytes(pcm, DEFAULT_SAMPLE_RATE);
            log.append(String.format("Played generated: freq=%.2f dur=%.2f amp=%.2f\n", freq, dur, amp));
        });

        savePresetBtn.addActionListener(e -> {
            String name = JOptionPane.showInputDialog(this, "Enter preset name:");
            if (name == null || name.trim().isEmpty()) return;
            double freq = ((Number) freqSpinner.getValue()).doubleValue();
            double dur = ((Number) durSpinner.getValue()).doubleValue();
            double amp = ((Number) ampSpinner.getValue()).doubleValue();
            try {
                Path presetDir = Path.of("assets/res/audio/presets");
                if (!Files.exists(presetDir)) Files.createDirectories(presetDir);
                Path json = presetDir.resolve(name + ".json");
                String content = String.format("{\"freq\":%f,\"duration\":%f,\"amplitude\":%f}", freq, dur, amp);
                Files.writeString(json, content);
                EditorAudioAPI.registerPreset(name, json);
                log.append("Saved preset: " + json.toAbsolutePath() + "\n");
            } catch (IOException ioe) {
                log.append("Error saving preset: " + ioe.getMessage() + "\n");
            }
        });

        // Quick playback of existing files in the assets folder via file chooser
        JButton playFileBtn = new JButton("Play Audio File...");
        JButton stopBtn = new JButton("Stop");
        JPanel lower = new JPanel(new FlowLayout(FlowLayout.LEFT));
        lower.add(playFileBtn);
        lower.add(stopBtn);
        add(lower, BorderLayout.SOUTH);

        playFileBtn.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser(new File("assets/res/audio"));
            int r = chooser.showOpenDialog(this);
            if (r == JFileChooser.APPROVE_OPTION) {
                File f = chooser.getSelectedFile();
                try {
                    AudioPlaybackUtil.playFile(f);
                } catch (Exception ex) {
                    log.append("Error playing file: " + ex.getMessage() + "\n");
                }
            }
        });

        stopBtn.addActionListener(e -> AudioPlaybackUtil.stop());
    }

    private File ensureAudioFolder() {
        File dir = new File("assets/res/audio");
        if (!dir.exists()) dir.mkdirs();
        return dir;
    }
}
