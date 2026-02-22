package mmorpg.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.UIManager;

/**
 * WoW-style UI prototype for the MMORPG module.
 */
public class WowUiFrame extends JFrame {

    private final Random random = new Random();

    private final JLabel playerNameLabel = new JLabel("Arin - Level 12", SwingConstants.LEFT);
    private final JProgressBar healthBar = new JProgressBar(0, 1000);
    private final JProgressBar manaBar = new JProgressBar(0, 1000);
    private final JProgressBar xpBar = new JProgressBar(0, 1000);

    private final JLabel targetNameLabel = new JLabel("Target: Ancient Dragon", SwingConstants.LEFT);
    private final JProgressBar targetHealthBar = new JProgressBar(0, 1000);

    private final JTextArea questTracker = new JTextArea();
    private final JTextArea chatLog = new JTextArea();

    public WowUiFrame() {
        super("MMORPG - WoW UI Prototype");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(1200, 760));
        setLayout(new BorderLayout(8, 8));
        getContentPane().setBackground(new Color(18, 20, 26));

        installLookAndFeel();
        add(createTopBar(), BorderLayout.NORTH);
        add(createCenterArea(), BorderLayout.CENTER);
        add(createBottomBar(), BorderLayout.SOUTH);

        setLocationRelativeTo(null);
        startUiSimulation();
    }

    private void installLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }
    }

    private JPanel createTopBar() {
        JPanel topBar = createPanel(new BorderLayout(10, 8), new Color(28, 32, 42), 8);

        JPanel playerFrame = createPanel(new GridLayout(4, 1, 0, 4), new Color(34, 39, 52), 8);
        styleLabel(playerNameLabel, 15, true, new Color(230, 230, 230));
        playerFrame.add(playerNameLabel);

        healthBar.setValue(840);
        manaBar.setValue(620);
        xpBar.setValue(320);
        styleBar(healthBar, "HP", new Color(170, 45, 45));
        styleBar(manaBar, "MP", new Color(45, 90, 180));
        styleBar(xpBar, "XP", new Color(170, 130, 40));
        playerFrame.add(healthBar);
        playerFrame.add(manaBar);
        playerFrame.add(xpBar);

        JPanel targetFrame = createPanel(new GridLayout(2, 1, 0, 4), new Color(34, 39, 52), 8);
        styleLabel(targetNameLabel, 14, true, new Color(245, 220, 170));
        targetFrame.add(targetNameLabel);
        targetHealthBar.setValue(740);
        styleBar(targetHealthBar, "Target HP", new Color(160, 50, 50));
        targetFrame.add(targetHealthBar);

        topBar.add(playerFrame, BorderLayout.WEST);
        topBar.add(targetFrame, BorderLayout.EAST);
        return topBar;
    }

    private JPanel createCenterArea() {
        JPanel center = createPanel(new BorderLayout(10, 10), new Color(18, 20, 26), 0);

        JPanel minimapPanel = createPanel(new BorderLayout(), new Color(30, 36, 48), 8);
        JLabel minimapTitle = new JLabel("MINIMAP", SwingConstants.CENTER);
        styleLabel(minimapTitle, 14, true, new Color(235, 235, 235));
        minimapPanel.add(minimapTitle, BorderLayout.NORTH);

        JTextArea minimapMock = new JTextArea();
        minimapMock.setEditable(false);
        minimapMock.setBackground(new Color(24, 28, 36));
        minimapMock.setForeground(new Color(180, 210, 180));
        minimapMock.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        minimapMock.setText(
                "N\n"
                + "  [Town]     [Forest]\n"
                + "      \\         |\n"
                + "       [You] -- [Dungeon]\n"
                + "                |\n"
                + "              [Raid]"
        );
        minimapPanel.add(minimapMock, BorderLayout.CENTER);

        JPanel questPanel = createPanel(new BorderLayout(), new Color(30, 36, 48), 8);
        JLabel questTitle = new JLabel("QUEST TRACKER", SwingConstants.CENTER);
        styleLabel(questTitle, 14, true, new Color(235, 235, 235));
        questTracker.setEditable(false);
        questTracker.setBackground(new Color(24, 28, 36));
        questTracker.setForeground(new Color(220, 220, 220));
        questTracker.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        questTracker.setText(
                "[Main] The Dragon Threat\n"
                + "- Speak with the king\n"
                + "- Gather the sacred key\n"
                + "- Enter Dragon's Lair\n\n"
                + "[Side] Explorer\n"
                + "- Discover 3 new sub-zones (1/3)"
        );
        questPanel.add(questTitle, BorderLayout.NORTH);
        questPanel.add(new JScrollPane(questTracker), BorderLayout.CENTER);

        JPanel leftColumn = createPanel(new GridLayout(2, 1, 0, 10), new Color(18, 20, 26), 0);
        leftColumn.add(minimapPanel);
        leftColumn.add(questPanel);

        JPanel worldPanel = createPanel(new BorderLayout(), new Color(26, 30, 40), 8);
        JLabel worldTitle = new JLabel("WORLD VIEWPORT", SwingConstants.CENTER);
        styleLabel(worldTitle, 15, true, new Color(240, 240, 240));
        JTextArea worldMock = new JTextArea();
        worldMock.setEditable(false);
        worldMock.setBackground(new Color(22, 24, 32));
        worldMock.setForeground(new Color(190, 190, 210));
        worldMock.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        worldMock.setText(
                "   . . .  Mountains      ~~~ River\n"
                + " [You] @  Path to dungeon entrance\n"
                + "   NPC !  Campfire and quest marker\n\n"
                + " (This panel is a UI mock placeholder for a 3D/2D world renderer.)"
        );
        worldPanel.add(worldTitle, BorderLayout.NORTH);
        worldPanel.add(new JScrollPane(worldMock), BorderLayout.CENTER);

        center.add(leftColumn, BorderLayout.WEST);
        center.add(worldPanel, BorderLayout.CENTER);
        return center;
    }

    private JPanel createBottomBar() {
        JPanel bottom = createPanel(new BorderLayout(8, 8), new Color(28, 32, 42), 8);

        JPanel actionBar = createPanel(new FlowLayout(FlowLayout.CENTER, 6, 4), new Color(24, 26, 36), 6);
        for (int i = 1; i <= 12; i++) {
            JButton skill = new JButton(String.valueOf(i));
            skill.setPreferredSize(new Dimension(58, 42));
            skill.setFocusPainted(false);
            skill.setBackground(new Color(56, 62, 82));
            skill.setForeground(new Color(235, 235, 235));
            skill.setBorder(BorderFactory.createLineBorder(new Color(120, 120, 140)));
            final int slot = i;
            skill.addActionListener(e -> appendChat("[Action] Used skill slot " + slot));
            actionBar.add(skill);
        }

        JPanel utilityBar = createPanel(new FlowLayout(FlowLayout.LEFT, 6, 4), new Color(24, 26, 36), 6);
        JButton inventoryButton = createUtilityButton("Inventory", "Inventory panel opened.");
        JButton characterButton = createUtilityButton("Character", "Character panel opened.");
        JButton talentsButton = createUtilityButton("Talents", "Talent window opened.");
        JButton mapButton = createUtilityButton("World Map", "World map opened.");
        JButton groupButton = createUtilityButton("Party", "Party window opened.");
        utilityBar.add(inventoryButton);
        utilityBar.add(characterButton);
        utilityBar.add(talentsButton);
        utilityBar.add(mapButton);
        utilityBar.add(groupButton);

        JPanel chatPanel = createPanel(new BorderLayout(), new Color(30, 36, 48), 6);
        JLabel chatTitle = new JLabel("CHAT", SwingConstants.LEFT);
        styleLabel(chatTitle, 13, true, new Color(235, 235, 235));
        chatLog.setEditable(false);
        chatLog.setLineWrap(true);
        chatLog.setWrapStyleWord(true);
        chatLog.setBackground(new Color(16, 18, 24));
        chatLog.setForeground(new Color(210, 210, 210));
        chatLog.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        chatLog.setText("[System] Welcome to the realm.\n");
        chatPanel.add(chatTitle, BorderLayout.NORTH);
        chatPanel.add(new JScrollPane(chatLog), BorderLayout.CENTER);
        chatPanel.setPreferredSize(new Dimension(360, 180));

        bottom.add(utilityBar, BorderLayout.NORTH);
        bottom.add(actionBar, BorderLayout.CENTER);
        bottom.add(chatPanel, BorderLayout.EAST);
        return bottom;
    }

    private JButton createUtilityButton(String label, String message) {
        JButton button = new JButton(label);
        button.setFocusPainted(false);
        button.setBackground(new Color(56, 62, 82));
        button.setForeground(new Color(235, 235, 235));
        button.setBorder(BorderFactory.createLineBorder(new Color(120, 120, 140)));
        button.addActionListener(e -> appendChat("[UI] " + message));
        return button;
    }

    private void styleBar(JProgressBar bar, String prefix, Color color) {
        bar.setStringPainted(true);
        bar.setForeground(color);
        bar.setBackground(new Color(30, 30, 30));
        bar.setString(prefix + " " + bar.getValue() + "/" + bar.getMaximum());
    }

    private void styleLabel(JLabel label, int size, boolean bold, Color color) {
        label.setFont(new Font(Font.SANS_SERIF, bold ? Font.BOLD : Font.PLAIN, size));
        label.setForeground(color);
    }

    private JPanel createPanel(java.awt.LayoutManager layout, Color color, int padding) {
        JPanel panel = new JPanel(layout);
        panel.setBackground(color);
        panel.setBorder(BorderFactory.createEmptyBorder(padding, padding, padding, padding));
        return panel;
    }

    private void startUiSimulation() {
        Timer timer = new Timer(1400, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateBars();
            }
        });
        timer.start();
    }

    private void updateBars() {
        int hp = clamp(healthBar.getValue() + random.nextInt(121) - 60, 120, 1000);
        int mp = clamp(manaBar.getValue() + random.nextInt(101) - 50, 80, 1000);
        int xp = xpBar.getValue() + random.nextInt(55);
        if (xp > 1000) {
            xp = xp - 1000;
            appendChat("[System] Level progress increased.");
        }
        int targetHp = clamp(targetHealthBar.getValue() + random.nextInt(141) - 70, 0, 1000);

        healthBar.setValue(hp);
        manaBar.setValue(mp);
        xpBar.setValue(xp);
        targetHealthBar.setValue(targetHp);

        healthBar.setString("HP " + hp + "/1000");
        manaBar.setString("MP " + mp + "/1000");
        xpBar.setString("XP " + xp + "/1000");
        targetHealthBar.setString("Target HP " + targetHp + "/1000");
    }

    private int clamp(int value, int min, int max) {
        if (value < min) {
            return min;
        }
        if (value > max) {
            return max;
        }
        return value;
    }

    private void appendChat(String message) {
        chatLog.append(message + "\n");
        chatLog.setCaretPosition(chatLog.getDocument().getLength());
    }
}
