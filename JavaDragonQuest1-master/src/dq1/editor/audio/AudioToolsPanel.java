package dq1.editor.audio;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.io.File;

public class AudioToolsPanel extends JPanel {

    private static final float DEFAULT_SAMPLE_RATE = 44100f;

    // playback control
    private volatile boolean playing = false;
    private SourceDataLine currentLine;

    public AudioToolsPanel() {
        setLayout(new BorderLayout(8,8));
        JPanel buttons = new JPanel(new GridLayout(0,2,4,4));
        JButton magicBtn = new JButton("Generate Magic Pulse");
        JButton magicPlayBtn = new JButton("Play");
        JButton whooshBtn = new JButton("Generate Whoosh");
        JButton whooshPlayBtn = new JButton("Play");
        JButton sparkleBtn = new JButton("Generate Sparkle");
        JButton sparklePlayBtn = new JButton("Play");
        buttons.add(magicBtn);
        buttons.add(magicPlayBtn);
        buttons.add(whooshBtn);
        buttons.add(whooshPlayBtn);
        buttons.add(sparkleBtn);
        buttons.add(sparklePlayBtn);

        add(buttons, BorderLayout.NORTH);

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
            playBytes(lastGenerated[0], DEFAULT_SAMPLE_RATE, log);
        });

        whooshPlayBtn.addActionListener(e -> {
            if (lastGenerated[1] == null) {
                log.append("No generated sound yet. Click Generate first.\n");
                return;
            }
            playBytes(lastGenerated[1], DEFAULT_SAMPLE_RATE, log);
        });

        sparklePlayBtn.addActionListener(e -> {
            if (lastGenerated[2] == null) {
                log.append("No generated sound yet. Click Generate first.\n");
                return;
            }
            playBytes(lastGenerated[2], DEFAULT_SAMPLE_RATE, log);
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
                    AudioInputStream ais = AudioSystem.getAudioInputStream(f);
                    byte[] buf = ais.readAllBytes();
                    // prefer using AudioSystem playback for files (handles format)
                    playAudioStream(ais, log);
                } catch (Exception ex) {
                    log.append("Error playing file: " + ex.getMessage() + "\n");
                }
            }
        });

        stopBtn.addActionListener(e -> stopPlayback());
    }

    private void playBytes(byte[] pcm, float sampleRate, JTextArea log) {
        new Thread(() -> {
            try {
                stopPlayback();
                AudioFormat format = new AudioFormat(sampleRate, 16, 1, true, false);
                DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
                currentLine = (SourceDataLine) AudioSystem.getLine(info);
                currentLine.open(format);
                currentLine.start();
                playing = true;
                int written = 0;
                int bufferSize = 4096;
                while (playing && written < pcm.length) {
                    int toWrite = Math.min(bufferSize, pcm.length - written);
                    currentLine.write(pcm, written, toWrite);
                    written += toWrite;
                }
                currentLine.drain();
                currentLine.stop();
                currentLine.close();
                playing = false;
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> log.append("Playback error: " + ex.getMessage() + "\n"));
            }
        }, "AudioPlayThread").start();
    }

    private void playAudioStream(AudioInputStream ais, JTextArea log) {
        new Thread(() -> {
            try {
                stopPlayback();
                AudioFormat baseFormat = ais.getFormat();
                AudioFormat decoded = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
                        baseFormat.getSampleRate(), 16, baseFormat.getChannels(),
                        baseFormat.getChannels() * 2, baseFormat.getSampleRate(), false);
                AudioInputStream din = AudioSystem.getAudioInputStream(decoded, ais);
                DataLine.Info info = new DataLine.Info(SourceDataLine.class, decoded);
                currentLine = (SourceDataLine) AudioSystem.getLine(info);
                currentLine.open(decoded);
                currentLine.start();
                playing = true;
                byte[] buffer = new byte[4096];
                int n = 0;
                while ((n = din.read(buffer, 0, buffer.length)) > 0 && playing) {
                    currentLine.write(buffer, 0, n);
                }
                currentLine.drain();
                currentLine.stop();
                currentLine.close();
                din.close();
                ais.close();
                playing = false;
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> log.append("Playback error: " + ex.getMessage() + "\n"));
            }
        }, "AudioFilePlayThread").start();
    }

    private void stopPlayback() {
        playing = false;
        try {
            if (currentLine != null && currentLine.isOpen()) {
                currentLine.stop();
                currentLine.close();
            }
        } catch (Exception ignored) {}
    }

    private File ensureAudioFolder() {
        File dir = new File("assets/res/audio");
        if (!dir.exists()) dir.mkdirs();
        return dir;
    }
}