package dq1.editor.audio;

import javax.sound.sampled.*;
import javax.swing.*;
import java.io.File;
import java.io.IOException;

public class AudioPlaybackUtil {
    private static volatile SourceDataLine currentLine;
    private static volatile boolean playing = false;

    public static void playBytes(byte[] pcm, float sampleRate) {
        stop();
        new Thread(() -> {
            try {
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
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(null, "Playback error: " + e.getMessage()));
                playing = false;
            }
        }, "APU-PlayBytes").start();
    }

    public static void playStream(AudioInputStream ais) {
        stop();
        new Thread(() -> {
            try (AudioInputStream din = ais) {
                AudioFormat baseFormat = din.getFormat();
                AudioFormat decoded = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
                        baseFormat.getSampleRate(), 16, baseFormat.getChannels(),
                        baseFormat.getChannels() * 2, baseFormat.getSampleRate(), false);
                AudioInputStream pcmStream = AudioSystem.getAudioInputStream(decoded, din);
                DataLine.Info info = new DataLine.Info(SourceDataLine.class, decoded);
                currentLine = (SourceDataLine) AudioSystem.getLine(info);
                currentLine.open(decoded);
                currentLine.start();
                playing = true;
                byte[] buffer = new byte[4096];
                int n = 0;
                while ((n = pcmStream.read(buffer, 0, buffer.length)) > 0 && playing) {
                    currentLine.write(buffer, 0, n);
                }
                currentLine.drain();
                currentLine.stop();
                currentLine.close();
                playing = false;
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(null, "Playback error: " + e.getMessage()));
                playing = false;
            }
        }, "APU-PlayStream").start();
    }

    public static void playFile(File f) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        AudioInputStream ais = AudioSystem.getAudioInputStream(f);
        playStream(ais);
    }

    public static void stop() {
        playing = false;
        try {
            if (currentLine != null && currentLine.isOpen()) {
                currentLine.stop();
                currentLine.close();
            }
        } catch (Exception ignored) {
        }
    }

    public static boolean isPlaying() {
        return playing;
    }
}
