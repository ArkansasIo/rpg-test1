package dq1.editor;

import dq1.editor.audio.AudioPlaybackUtil;
import dq1.editor.audio.EditorAudioAPI;

import javax.imageio.ImageIO;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;

public class InspectorPanel extends JPanel {

    private JLabel nameLabel = new JLabel();
    private JLabel sizeLabel = new JLabel();
    private JLabel modifiedLabel = new JLabel();
    private JButton playBtn = new JButton("Play");
    private JButton stopBtn = new JButton("Stop");
    private JLabel imagePreview = new JLabel();

    public InspectorPanel() {
        setLayout(new BorderLayout());
        JPanel meta = new JPanel(new GridLayout(0,1));
        meta.add(new JLabel("Inspector"));
        meta.add(nameLabel);
        meta.add(sizeLabel);
        meta.add(modifiedLabel);

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controls.add(playBtn);
        controls.add(stopBtn);

        JPanel top = new JPanel(new BorderLayout());
        top.add(meta, BorderLayout.CENTER);
        top.add(controls, BorderLayout.SOUTH);

        add(top, BorderLayout.NORTH);

        imagePreview.setHorizontalAlignment(SwingConstants.CENTER);
        add(new JScrollPane(imagePreview), BorderLayout.CENTER);

        playBtn.addActionListener(e -> {
            // action handled in inspect when file known
        });
        stopBtn.addActionListener(e -> AudioPlaybackUtil.stop());
    }

    public void inspect(File f) {
        if (f == null) {
            nameLabel.setText("Name: (none)");
            sizeLabel.setText("Size: -");
            modifiedLabel.setText("Modified: -");
            imagePreview.setIcon(null);
            removeAllPlayListeners();
            playBtn.setEnabled(false);
            stopBtn.setEnabled(false);
            return;
        }
        nameLabel.setText("Name: " + f.getName());
        try {
            long size = Files.size(f.toPath());
            sizeLabel.setText("Size: " + size + " bytes");
        } catch (Exception e) {
            sizeLabel.setText("Size: ?");
        }
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        modifiedLabel.setText("Modified: " + fmt.format(new Date(f.lastModified())));

        // Reset preview and controls
        imagePreview.setIcon(null);
        removeAllPlayListeners();
        playBtn.setEnabled(false);
        stopBtn.setEnabled(true);

        String name = f.getName().toLowerCase();
        try {
            if (name.endsWith(".wav") || name.endsWith(".aiff") || name.endsWith(".au") || name.endsWith(".mp3")) {
                // enable play
                playBtn.setEnabled(true);
                playBtn.addActionListener(e -> {
                    try {
                        AudioPlaybackUtil.playFile(f);
                    } catch (UnsupportedAudioFileException | IOException ex) {
                        JOptionPane.showMessageDialog(this, "Cannot play file: " + ex.getMessage());
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, "Playback error: " + ex.getMessage());
                    }
                });
            } else if (name.endsWith(".png") || name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".gif")) {
                // show thumbnail
                Image img = ImageIO.read(f);
                if (img != null) {
                    Image t = img.getScaledInstance(300, -1, Image.SCALE_SMOOTH);
                    imagePreview.setIcon(new ImageIcon(t));
                }
                playBtn.setEnabled(false);
            } else {
                playBtn.setEnabled(false);
            }
        } catch (IOException ioe) {
            JOptionPane.showMessageDialog(this, "Preview error: " + ioe.getMessage());
        }
    }

    public void inspectAttached(String filename) {
        if (filename == null || filename.isEmpty()) {
            inspect((File) null);
            return;
        }
        nameLabel.setText("Name: " + filename);
        imagePreview.setIcon(null);
        stopBtn.setEnabled(true);
        removeAllPlayListeners();
        // Try to resolve file and show size if present
        File f = EditorAudioAPI.getAudioFile(filename);
        if (f != null) {
            try {
                long size = Files.size(f.toPath());
                sizeLabel.setText("Size: " + size + " bytes");
            } catch (Exception e) {
                sizeLabel.setText("Size: ?");
            }
            playBtn.setEnabled(true);
            playBtn.addActionListener(e -> {
                try {
                    AudioPlaybackUtil.playFile(f);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Playback error: " + ex.getMessage());
                }
            });
        } else {
            sizeLabel.setText("Size: (not in assets)");
            playBtn.setEnabled(true);
            playBtn.addActionListener(e -> EditorAudioAPI.playFileByName(filename));
        }
    }

    private void removeAllPlayListeners() {
        for (java.awt.event.ActionListener al : playBtn.getActionListeners()) {
            playBtn.removeActionListener(al);
        }
    }
}