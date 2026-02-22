package dq1.editor.audio;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class AudioToolsPanel extends JPanel {

    private static final float DEFAULT_SAMPLE_RATE = 44100f;

    public AudioToolsPanel() {
        setLayout(new BorderLayout(8,8));
        JPanel buttons = new JPanel(new GridLayout(0,1,4,4));
        JButton magicBtn = new JButton("Generate Magic Pulse");
        JButton whooshBtn = new JButton("Generate Whoosh");
        JButton sparkleBtn = new JButton("Generate Sparkle");
        buttons.add(magicBtn);
        buttons.add(whooshBtn);
        buttons.add(sparkleBtn);

        add(buttons, BorderLayout.NORTH);

        JTextArea log = new JTextArea(10, 40);
        log.setEditable(false);
        add(new JScrollPane(log), BorderLayout.CENTER);

        magicBtn.addActionListener(e -> {
            try {
                byte[] pcm = SfxGenerator.magicPulse(DEFAULT_SAMPLE_RATE);
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
                File out = ensureAudioFolder().toPath().resolve("sparkle.wav").toFile();
                SfxGenerator.saveWav(out, pcm, DEFAULT_SAMPLE_RATE);
                log.append("Saved: " + out.getAbsolutePath() + "\n");
            } catch (Exception ex) {
                log.append("Error: " + ex.getMessage() + "\n");
            }
        });
    }

    private File ensureAudioFolder() {
        File dir = new File("assets/res/audio");
        if (!dir.exists()) dir.mkdirs();
        return dir;
    }
}
