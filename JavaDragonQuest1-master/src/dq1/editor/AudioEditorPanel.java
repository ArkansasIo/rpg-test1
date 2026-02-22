package dq1.editor;

import dq1.core.GameAPI;
import java.awt.BorderLayout;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.SpinnerNumberModel;

/**
 * Audio and sound effects editor panel.
 */
public class AudioEditorPanel extends JPanel {
    private final JTextArea output = new JTextArea();
    private final JComboBox<String> musicSelector = new JComboBox<>();
    private final JSpinner musicVolumeSpinner = new JSpinner(new SpinnerNumberModel(5, 0, 9, 1));
    private final JSpinner soundVolumeSpinner = new JSpinner(new SpinnerNumberModel(5, 0, 9, 1));
    private final JSpinner sfxIdSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 127, 1));

    public AudioEditorPanel() {
        super(new BorderLayout(8, 8));
        output.setEditable(false);
        add(buildTopControls(), BorderLayout.NORTH);
        add(new JScrollPane(output), BorderLayout.CENTER);
        refreshAudioData();
    }

    private JPanel buildTopControls() {
        JPanel top = new JPanel();
        top.add(new JLabel("Music Track:"));
        top.add(musicSelector);

        JButton refresh = new JButton("Refresh");
        refresh.addActionListener(e -> refreshAudioData());
        top.add(refresh);

        JButton play = new JButton("Play");
        play.addActionListener(e -> log(GameAPI.previewMusic((String) musicSelector.getSelectedItem())));
        top.add(play);

        JButton pause = new JButton("Pause");
        pause.addActionListener(e -> log(GameAPI.pauseMusic()));
        top.add(pause);

        JButton stop = new JButton("Stop");
        stop.addActionListener(e -> log(GameAPI.stopMusic()));
        top.add(stop);

        top.add(new JLabel("Music Vol:"));
        top.add(musicVolumeSpinner);
        top.add(new JLabel("SFX Vol:"));
        top.add(soundVolumeSpinner);
        JButton applyVolume = new JButton("Apply Volumes");
        applyVolume.addActionListener(e -> {
            int mv = (Integer) musicVolumeSpinner.getValue();
            int sv = (Integer) soundVolumeSpinner.getValue();
            log(GameAPI.setAudioVolumes(mv, sv));
        });
        top.add(applyVolume);

        top.add(new JLabel("SFX Id:"));
        top.add(sfxIdSpinner);
        JButton playSfx = new JButton("Play SFX");
        playSfx.addActionListener(e -> log(GameAPI.previewSoundEffect((Integer) sfxIdSpinner.getValue())));
        top.add(playSfx);

        return top;
    }

    private void refreshAudioData() {
        musicSelector.removeAllItems();
        List<String> ids = GameAPI.getAudioTrackIds();
        for (String id : ids) {
            musicSelector.addItem(id);
        }
        List<String> lines = new ArrayList<>();
        lines.add("Audio Editor");
        lines.add("Tracks found: " + ids.size());
        lines.add("Known MIDI files:");
        for (String id : ids) {
            lines.add("- " + id);
        }
        File audioDir = new File("assets/res/audio");
        if (!audioDir.exists()) {
            audioDir = new File("JavaDragonQuest1-master/assets/res/audio");
        }
        lines.add("");
        lines.add("Audio folder: " + audioDir.getAbsolutePath());
        output.setText(String.join(System.lineSeparator(), lines));
    }

    private void log(String line) {
        output.setText(line + System.lineSeparator() + output.getText());
    }
}
