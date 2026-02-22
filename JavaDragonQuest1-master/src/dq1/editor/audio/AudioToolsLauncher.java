package dq1.editor.audio;

import javax.swing.*;
import java.awt.*;

public class AudioToolsLauncher {
    public static void showDialog(Component parent) {
        JDialog d = new JDialog(SwingUtilities.getWindowAncestor(parent), "Audio Tools", Dialog.ModalityType.MODELESS);
        d.getContentPane().add(new AudioToolsPanel());
        d.pack();
        d.setLocationRelativeTo(parent);
        d.setVisible(true);
    }
}
