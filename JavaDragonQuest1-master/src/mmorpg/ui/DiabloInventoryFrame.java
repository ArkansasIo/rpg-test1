package mmorpg.ui;

import dq1.core.rpg.EquipmentSlot;
import dq1.core.rpg.InventorySystem;
import dq1.core.rpg.ItemKind;
import dq1.core.rpg.PlayerRpgProfile;
import dq1.core.rpg.RpgActionResult;
import dq1.core.rpg.RpgAttribute;
import dq1.core.rpg.RpgItemDefinition;
import dq1.core.rpg.RpgRuntimeService;
import dq1.core.rpg.RpgStats;
import dq1.core.rpg.RpgSystems;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

/**
 * Diablo-style character inventory/equipment/stats UI backed by RPG runtime.
 */
public class DiabloInventoryFrame extends JFrame {

    private final PlayerRpgProfile profile = RpgSystems.getProfile();
    private final RpgRuntimeService runtime = RpgSystems.getRuntime();

    private final JLabel titleLabel = new JLabel();
    private final JLabel statusLabel = new JLabel("Ready.");
    private final JTextArea statsArea = new JTextArea();
    private final JPanel equipmentPanel = new JPanel(new GridLayout(6, 2, 6, 6));
    private final JPanel inventoryGrid = new JPanel(new GridLayout(8, 6, 4, 4));
    private final JComboBox<EquipmentSlot> unequipSlotBox = new JComboBox<>(EquipmentSlot.values());

    private final List<JButton> inventoryCells = new ArrayList<>();
    private final List<RpgItemDefinition> visibleItems = new ArrayList<>();
    private int selectedIndex = -1;

    public DiabloInventoryFrame() {
        super("RPG - Diablo Style Inventory");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(1180, 760));
        setLayout(new BorderLayout(8, 8));
        getContentPane().setBackground(new Color(22, 18, 16));

        add(buildTopPanel(), BorderLayout.NORTH);
        add(buildCenterPanel(), BorderLayout.CENTER);
        add(buildBottomPanel(), BorderLayout.SOUTH);

        setLocationRelativeTo(null);
        refreshAll();
    }

    private JPanel buildTopPanel() {
        JPanel panel = themedPanel(new BorderLayout(8, 8), new Color(40, 28, 24), 8);
        titleLabel.setForeground(new Color(245, 222, 173));
        titleLabel.setFont(new Font(Font.SERIF, Font.BOLD, 20));
        panel.add(titleLabel, BorderLayout.WEST);
        return panel;
    }

    private JPanel buildCenterPanel() {
        JPanel panel = themedPanel(new GridLayout(1, 3, 8, 8), new Color(22, 18, 16), 0);

        JPanel equipContainer = themedPanel(new BorderLayout(6, 6), new Color(30, 24, 20), 8);
        JLabel equipTitle = sectionLabel("Equipment Slots");
        equipContainer.add(equipTitle, BorderLayout.NORTH);
        equipmentPanel.setBackground(new Color(30, 24, 20));
        equipContainer.add(equipmentPanel, BorderLayout.CENTER);

        JPanel invContainer = themedPanel(new BorderLayout(6, 6), new Color(30, 24, 20), 8);
        JLabel invTitle = sectionLabel("Inventory Grid (8x6)");
        invContainer.add(invTitle, BorderLayout.NORTH);
        inventoryGrid.setBackground(new Color(20, 16, 14));
        for (int i = 0; i < 48; i++) {
            JButton cell = new JButton("-");
            cell.setFocusPainted(false);
            cell.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 11));
            cell.setBackground(new Color(56, 44, 36));
            cell.setForeground(new Color(240, 220, 200));
            final int idx = i;
            cell.addActionListener(e -> {
                selectedIndex = idx;
                refreshInventorySelection();
            });
            inventoryCells.add(cell);
            inventoryGrid.add(cell);
        }
        invContainer.add(inventoryGrid, BorderLayout.CENTER);

        JPanel statsContainer = themedPanel(new BorderLayout(6, 6), new Color(30, 24, 20), 8);
        JLabel statsTitle = sectionLabel("Character Stats");
        statsContainer.add(statsTitle, BorderLayout.NORTH);
        statsArea.setEditable(false);
        statsArea.setBackground(new Color(18, 15, 12));
        statsArea.setForeground(new Color(240, 232, 214));
        statsArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        statsContainer.add(new JScrollPane(statsArea), BorderLayout.CENTER);

        panel.add(equipContainer);
        panel.add(invContainer);
        panel.add(statsContainer);
        return panel;
    }

    private JPanel buildBottomPanel() {
        JPanel panel = themedPanel(new BorderLayout(8, 8), new Color(40, 28, 24), 8);
        JPanel actionRow = themedPanel(new FlowLayout(FlowLayout.LEFT, 8, 0), new Color(40, 28, 24), 0);

        JButton equipButton = actionButton("Equip Selected");
        equipButton.addActionListener(e -> doEquipSelected());

        JButton useButton = actionButton("Use Selected");
        useButton.addActionListener(e -> doUseSelected());

        JButton unequipButton = actionButton("Unequip Slot");
        unequipButton.addActionListener(e -> doUnequip());

        JButton refreshButton = actionButton("Refresh");
        refreshButton.addActionListener(e -> refreshAll());

        actionRow.add(equipButton);
        actionRow.add(useButton);
        actionRow.add(unequipSlotBox);
        actionRow.add(unequipButton);
        actionRow.add(refreshButton);

        statusLabel.setForeground(new Color(245, 222, 173));
        statusLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));

        panel.add(actionRow, BorderLayout.WEST);
        panel.add(statusLabel, BorderLayout.EAST);
        return panel;
    }

    private JButton actionButton(String text) {
        JButton b = new JButton(text);
        b.setFocusPainted(false);
        b.setBackground(new Color(90, 50, 36));
        b.setForeground(new Color(250, 235, 215));
        b.setBorder(BorderFactory.createLineBorder(new Color(180, 120, 80)));
        return b;
    }

    private JPanel themedPanel(java.awt.LayoutManager layout, Color color, int pad) {
        JPanel p = new JPanel(layout);
        p.setBackground(color);
        p.setBorder(BorderFactory.createEmptyBorder(pad, pad, pad, pad));
        return p;
    }

    private JLabel sectionLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(new Color(245, 222, 173));
        label.setFont(new Font(Font.SERIF, Font.BOLD, 16));
        return label;
    }

    private void refreshAll() {
        titleLabel.setText("Level " + profile.getLevel() + " "
                + profile.getCharacterClass().name() + "  |  Diablo Grid Layout");
        refreshEquipmentPanel();
        refreshInventoryPanel();
        refreshStatsPanel();
        runtime.exportToGlobals();
    }

    private void refreshEquipmentPanel() {
        equipmentPanel.removeAll();
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            JLabel slotLabel = new JLabel(slot.name());
            slotLabel.setForeground(new Color(235, 220, 190));
            RpgItemDefinition equipped = profile.getEquipment().getEquippedItems().get(slot);
            JLabel itemLabel = new JLabel(equipped == null ? "-" : shortName(equipped.getName(), 18));
            itemLabel.setForeground(equipped == null
                    ? new Color(140, 130, 120)
                    : new Color(255, 210, 140));
            equipmentPanel.add(slotLabel);
            equipmentPanel.add(itemLabel);
        }
        equipmentPanel.revalidate();
        equipmentPanel.repaint();
    }

    private void refreshInventoryPanel() {
        visibleItems.clear();
        for (InventorySystem.InventoryEntry entry : profile.getInventory().getEntries()) {
            for (int i = 0; i < entry.getQuantity(); i++) {
                visibleItems.add(entry.getDefinition());
            }
        }

        for (int i = 0; i < inventoryCells.size(); i++) {
            JButton cell = inventoryCells.get(i);
            if (i < visibleItems.size()) {
                RpgItemDefinition item = visibleItems.get(i);
                cell.setText(shortName(item.getTypeName(), 12));
                cell.setToolTipText(item.getName() + " | " + item.getKind()
                        + " | " + item.getRarity().name());
                cell.setEnabled(true);
                if (item.getKind() == ItemKind.CONSUMABLE) {
                    cell.setBackground(new Color(90, 72, 42));
                }
                else if (item.getSlot() != null) {
                    cell.setBackground(new Color(62, 44, 74));
                }
                else {
                    cell.setBackground(new Color(56, 44, 36));
                }
            }
            else {
                cell.setText("-");
                cell.setToolTipText(null);
                cell.setEnabled(false);
                cell.setBackground(new Color(40, 34, 30));
            }
        }
        refreshInventorySelection();
    }

    private void refreshInventorySelection() {
        for (int i = 0; i < inventoryCells.size(); i++) {
            JButton cell = inventoryCells.get(i);
            if (i == selectedIndex && i < visibleItems.size()) {
                cell.setBorder(BorderFactory.createLineBorder(new Color(255, 210, 120), 2));
            }
            else {
                cell.setBorder(BorderFactory.createLineBorder(new Color(100, 80, 60), 1));
            }
        }
    }

    private void refreshStatsPanel() {
        RpgStats s = profile.getTotalStats();
        StringBuilder sb = new StringBuilder();
        sb.append("Core\n");
        appendStat(sb, "STR", s.get(RpgAttribute.STRENGTH));
        appendStat(sb, "AGI", s.get(RpgAttribute.AGILITY));
        appendStat(sb, "INT", s.get(RpgAttribute.INTELLIGENCE));
        appendStat(sb, "VIT", s.get(RpgAttribute.VITALITY));
        appendStat(sb, "SPR", s.get(RpgAttribute.SPIRIT));
        appendStat(sb, "LUK", s.get(RpgAttribute.LUCK));
        sb.append('\n');
        sb.append("Combat\n");
        appendStat(sb, "ATK", s.get(RpgAttribute.ATTACK_POWER));
        appendStat(sb, "DEF", s.get(RpgAttribute.DEFENSE));
        appendStat(sb, "RES", s.get(RpgAttribute.RESISTANCE));
        appendStat(sb, "CRIT", s.get(RpgAttribute.CRIT_RATE));
        appendStat(sb, "HASTE", s.get(RpgAttribute.HASTE));
        sb.append('\n');
        sb.append("Resources\n");
        appendStat(sb, "HP MAX", s.get(RpgAttribute.MAX_HP));
        appendStat(sb, "HP CUR", profile.getCurrentHp());
        appendStat(sb, "MP MAX", s.get(RpgAttribute.MAX_MP));
        appendStat(sb, "MP CUR", profile.getCurrentMp());
        appendStat(sb, "SP", s.get(RpgAttribute.SPELL_POWER));
        statsArea.setText(sb.toString());
        statsArea.setCaretPosition(0);
    }

    private void appendStat(StringBuilder sb, String key, int value) {
        sb.append(String.format("%-6s %4d%n", key, value));
    }

    private String shortName(String value, int max) {
        if (value.length() <= max) {
            return value;
        }
        return value.substring(0, Math.max(1, max - 1)) + ".";
    }

    private RpgItemDefinition getSelectedItem() {
        if (selectedIndex < 0 || selectedIndex >= visibleItems.size()) {
            return null;
        }
        return visibleItems.get(selectedIndex);
    }

    private void doEquipSelected() {
        RpgItemDefinition item = getSelectedItem();
        if (item == null) {
            setStatus("Select an item first.");
            return;
        }
        RpgActionResult result = runtime.equipFromInventory(item.getId());
        setStatus(result.getMessage());
        refreshAll();
    }

    private void doUseSelected() {
        RpgItemDefinition item = getSelectedItem();
        if (item == null) {
            setStatus("Select an item first.");
            return;
        }
        RpgActionResult result = runtime.useConsumable(item.getId());
        setStatus(result.getMessage());
        refreshAll();
    }

    private void doUnequip() {
        EquipmentSlot slot = (EquipmentSlot) unequipSlotBox.getSelectedItem();
        if (slot == null) {
            setStatus("Choose a slot.");
            return;
        }
        RpgActionResult result = runtime.unequipToInventory(slot);
        setStatus(result.getMessage());
        refreshAll();
    }

    private void setStatus(String text) {
        statusLabel.setText(text);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DiabloInventoryFrame().setVisible(true));
    }
}
