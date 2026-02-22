package dq1.editor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

/**
 * Lightweight game editor shell.
 * Keeps an extensible editor entrypoint while avoiding hard dependencies
 * on experimental runtime classes.
 */
public class GameEditorFrame extends JFrame {

    public GameEditorFrame() {
        super("Game Engine Editor");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(1000, 700));
        setLayout(new BorderLayout());

        // --- Menu Bar ---
        javax.swing.JMenuBar menuBar = new javax.swing.JMenuBar();

        // File Menu
        javax.swing.JMenu fileMenu = new javax.swing.JMenu("File");
        fileMenu.add(new javax.swing.JMenuItem("New Project"));
        fileMenu.add(new javax.swing.JMenuItem("Open Project"));
        fileMenu.add(new javax.swing.JMenuItem("Save"));
        fileMenu.add(new javax.swing.JMenuItem("Save As"));
        fileMenu.addSeparator();
        fileMenu.add(new javax.swing.JMenuItem("Export Data"));
        fileMenu.addSeparator();
        fileMenu.add(new javax.swing.JMenuItem("Exit"));
        menuBar.add(fileMenu);

        // Edit Menu
        javax.swing.JMenu editMenu = new javax.swing.JMenu("Edit");
        editMenu.add(new javax.swing.JMenuItem("Undo"));
        editMenu.add(new javax.swing.JMenuItem("Redo"));
        editMenu.addSeparator();
        editMenu.add(new javax.swing.JMenuItem("Cut"));
        editMenu.add(new javax.swing.JMenuItem("Copy"));
        editMenu.add(new javax.swing.JMenuItem("Paste"));
        editMenu.addSeparator();
        editMenu.add(new javax.swing.JMenuItem("Find/Replace"));
        menuBar.add(editMenu);

        // Tools Menu
        javax.swing.JMenu toolsMenu = new javax.swing.JMenu("Tools");
        toolsMenu.add(new javax.swing.JMenuItem("World Generator"));
        toolsMenu.add(new javax.swing.JMenuItem("Biome Catalog"));
        toolsMenu.add(new javax.swing.JMenuItem("Enemy Table Editor"));
        toolsMenu.add(new javax.swing.JMenuItem("Weather/Season System"));
        toolsMenu.add(new javax.swing.JMenuItem("Talent Tree Editor"));
        toolsMenu.add(new javax.swing.JMenuItem("Map/Zone Editor"));
        toolsMenu.add(new javax.swing.JMenuItem("Quest/Story Editor"));
        toolsMenu.add(new javax.swing.JMenuItem("Combat Formula Tool"));
        toolsMenu.add(new javax.swing.JMenuItem("Resource Estimator"));
        menuBar.add(toolsMenu);

        // Systems Menu
        javax.swing.JMenu systemsMenu = new javax.swing.JMenu("Systems");
        javax.swing.JMenu attrMenu = new javax.swing.JMenu("Attributes");
        attrMenu.add(new javax.swing.JMenuItem("Primary Attributes"));
        attrMenu.add(new javax.swing.JMenuItem("Secondary Stats"));
        attrMenu.add(new javax.swing.JMenuItem("Combat Stats"));
        systemsMenu.add(attrMenu);
        javax.swing.JMenu resMenu = new javax.swing.JMenu("Resources");
        resMenu.add(new javax.swing.JMenuItem("Health/HP"));
        resMenu.add(new javax.swing.JMenuItem("Mana/MP"));
        resMenu.add(new javax.swing.JMenuItem("Stamina/STA"));
        resMenu.add(new javax.swing.JMenuItem("Other Resources"));
        systemsMenu.add(resMenu);
        javax.swing.JMenu dmgMenu = new javax.swing.JMenu("Damage Types");
        dmgMenu.add(new javax.swing.JMenuItem("Physical"));
        dmgMenu.add(new javax.swing.JMenuItem("Fire"));
        dmgMenu.add(new javax.swing.JMenuItem("Ice"));
        dmgMenu.add(new javax.swing.JMenuItem("Lightning"));
        dmgMenu.add(new javax.swing.JMenuItem("Earth"));
        dmgMenu.add(new javax.swing.JMenuItem("Wind"));
        dmgMenu.add(new javax.swing.JMenuItem("Water"));
        dmgMenu.add(new javax.swing.JMenuItem("Light"));
        dmgMenu.add(new javax.swing.JMenuItem("Dark"));
        dmgMenu.add(new javax.swing.JMenuItem("Arcane"));
        dmgMenu.add(new javax.swing.JMenuItem("Poison"));
        dmgMenu.add(new javax.swing.JMenuItem("Bleed"));
        dmgMenu.add(new javax.swing.JMenuItem("Void"));
        dmgMenu.add(new javax.swing.JMenuItem("Chaos"));
        dmgMenu.add(new javax.swing.JMenuItem("True Damage"));
        systemsMenu.add(dmgMenu);
        javax.swing.JMenu resistMenu = new javax.swing.JMenu("Resistances");
        resistMenu.add(new javax.swing.JMenuItem("Flat Resistance"));
        resistMenu.add(new javax.swing.JMenuItem("Percentage Resistance"));
        resistMenu.add(new javax.swing.JMenuItem("Absorption"));
        resistMenu.add(new javax.swing.JMenuItem("Immunity"));
        resistMenu.add(new javax.swing.JMenuItem("Vulnerability %"));
        systemsMenu.add(resistMenu);
        javax.swing.JMenu buffsMenu = new javax.swing.JMenu("Buffs/Debuffs");
        buffsMenu.add(new javax.swing.JMenuItem("Buffs"));
        buffsMenu.add(new javax.swing.JMenuItem("Debuffs"));
        buffsMenu.add(new javax.swing.JMenuItem("Crowd Control"));
        buffsMenu.add(new javax.swing.JMenuItem("Damage-over-time"));
        buffsMenu.add(new javax.swing.JMenuItem("Attribute Reduction"));
        systemsMenu.add(buffsMenu);
        javax.swing.JMenu enemyMenu = new javax.swing.JMenu("Enemies/Bosses");
        enemyMenu.add(new javax.swing.JMenuItem("Enemy Taxonomy"));
        enemyMenu.add(new javax.swing.JMenuItem("Boss Classification"));
        enemyMenu.add(new javax.swing.JMenuItem("Boss Schema"));
        enemyMenu.add(new javax.swing.JMenuItem("AI Roles"));
        systemsMenu.add(enemyMenu);
        javax.swing.JMenu lootMenu = new javax.swing.JMenu("Loot/Rewards");
        lootMenu.add(new javax.swing.JMenuItem("Loot Table"));
        lootMenu.add(new javax.swing.JMenuItem("Drop Modifiers"));
        lootMenu.add(new javax.swing.JMenuItem("PvE/PvP Separation"));
        lootMenu.add(new javax.swing.JMenuItem("Gear Slots"));
        systemsMenu.add(lootMenu);
        menuBar.add(systemsMenu);

        // Help Menu
        javax.swing.JMenu helpMenu = new javax.swing.JMenu("Help");
        helpMenu.add(new javax.swing.JMenuItem("Documentation"));
        helpMenu.add(new javax.swing.JMenuItem("About"));
        menuBar.add(helpMenu);

        setJMenuBar(menuBar);

            // Unreal-style layout: left nav, center main, right properties
            JPanel mainPanel = new JPanel(new BorderLayout());

            // Left: Navigation tabs
            JTabbedPane navTabs = new JTabbedPane(JTabbedPane.LEFT);
            navTabs.addTab("Assets", null);
            navTabs.addTab("Sound/Audio", null);
            navTabs.addTab("Effects", null);
            navTabs.addTab("Sprites/Pixels", null);
            navTabs.addTab("Title", null);
            navTabs.addTab("Monsters", null);
            navTabs.addTab("Zones", null);
            navTabs.addTab("Story", null);
            navTabs.addTab("Items", null);
            navTabs.addTab("Spells", null);
            navTabs.addTab("Bosses", null);
            navTabs.addTab("Attributes", null);
            navTabs.addTab("Stats", null);
            navTabs.addTab("Resources", null);
            navTabs.addTab("Damage Types", null);
            navTabs.addTab("Resistances", null);
            navTabs.addTab("Buffs/Debuffs", null);
            navTabs.addTab("Enemy Taxonomy", null);
            navTabs.addTab("Boss Schema", null);
            navTabs.addTab("Loot", null);
            navTabs.setPreferredSize(new Dimension(140, 0));

            // Center: Main editing panel (card layout)
            java.util.Map<String, JPanel> editorPanels = new java.util.HashMap<>();
            editorPanels.put("Assets", createAssetsPanel());
            editorPanels.put("Sound/Audio", createSoundAudioPanel());
            editorPanels.put("Effects", createEffectsPanel());
            editorPanels.put("Sprites/Pixels", createSpritesPixelsPanel());
                // Editable sound/audio panel
                private JPanel createSoundAudioPanel() {
                    JPanel panel = new JPanel(new BorderLayout());
                    JTextArea area = new JTextArea("Edit sound and audio assets here (music, sfx, etc).\nAdd/Remove/Update sound paths and metadata.");
                    area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
                    JButton saveBtn = new JButton("Save Sounds");
                    JButton loadBtn = new JButton("Load Sounds");
                    JPanel btnPanel = new JPanel();
                    btnPanel.add(saveBtn);
                    btnPanel.add(loadBtn);
                    panel.add(new JScrollPane(area), BorderLayout.CENTER);
                    panel.add(btnPanel, BorderLayout.SOUTH);
                    saveBtn.addActionListener(e -> saveTextAreaToFile(area, "sounds.txt"));
                    loadBtn.addActionListener(e -> loadTextAreaFromFile(area, "sounds.txt"));
                    return panel;
                }

                // Editable effects panel
                private JPanel createEffectsPanel() {
                    JPanel panel = new JPanel(new BorderLayout());
                    JTextArea area = new JTextArea("Edit visual/audio effects here (explosions, flashes, etc).\nAdd/Remove/Update effect definitions.");
                    area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
                    JButton saveBtn = new JButton("Save Effects");
                    JButton loadBtn = new JButton("Load Effects");
                    JPanel btnPanel = new JPanel();
                    btnPanel.add(saveBtn);
                    btnPanel.add(loadBtn);
                    panel.add(new JScrollPane(area), BorderLayout.CENTER);
                    panel.add(btnPanel, BorderLayout.SOUTH);
                    saveBtn.addActionListener(e -> saveTextAreaToFile(area, "effects.txt"));
                    loadBtn.addActionListener(e -> loadTextAreaFromFile(area, "effects.txt"));
                    return panel;
                }

                // Editable sprites/pixels panel
                private JPanel createSpritesPixelsPanel() {
                    JPanel panel = new JPanel(new BorderLayout());
                    JTextArea area = new JTextArea("Edit sprites and pixel art here (characters, tiles, icons, etc).\nAdd/Remove/Update sprite paths and metadata.");
                    area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
                    JButton saveBtn = new JButton("Save Sprites");
                    JButton loadBtn = new JButton("Load Sprites");
                    JPanel btnPanel = new JPanel();
                    btnPanel.add(saveBtn);
                    btnPanel.add(loadBtn);
                    panel.add(new JScrollPane(area), BorderLayout.CENTER);
                    panel.add(btnPanel, BorderLayout.SOUTH);
                    saveBtn.addActionListener(e -> saveTextAreaToFile(area, "sprites.txt"));
                    loadBtn.addActionListener(e -> loadTextAreaFromFile(area, "sprites.txt"));
                    return panel;
                }
            editorPanels.put("Title", createTitlePanel());
            editorPanels.put("Monsters", createMonstersPanel());
            editorPanels.put("Zones", createEditableTextPanel("Zones"));
            editorPanels.put("Story", createEditableTextPanel("Story"));
            editorPanels.put("Items", createEditableTextPanel("Items"));
            editorPanels.put("Spells", createEditableTextPanel("Spells"));
            editorPanels.put("Bosses", createEditableTextPanel("Bosses"));
            editorPanels.put("Attributes", createEditableTextPanel("Attributes"));
            editorPanels.put("Stats", createEditableTextPanel("Stats"));
            editorPanels.put("Resources", createEditableTextPanel("Resources"));
            editorPanels.put("Damage Types", createEditableTextPanel("Damage Types"));
            editorPanels.put("Resistances", createEditableTextPanel("Resistances"));
            editorPanels.put("Buffs/Debuffs", createEditableTextPanel("Buffs/Debuffs"));
            editorPanels.put("Enemy Taxonomy", createEditableTextPanel("Enemy Taxonomy"));
            editorPanels.put("Boss Schema", createEditableTextPanel("Boss Schema"));
            editorPanels.put("Loot", createEditableTextPanel("Loot"));
            JPanel cardPanel = new JPanel(new java.awt.CardLayout());
            for (String key : editorPanels.keySet()) {
                cardPanel.add(editorPanels.get(key), key);
            }

            // Right: Properties/details panel (placeholder for now)
            JPanel detailsPanel = new JPanel(new BorderLayout());
            detailsPanel.setPreferredSize(new Dimension(220, 0));
            detailsPanel.add(new javax.swing.JLabel("Properties / Details"), BorderLayout.NORTH);

            // Layout
            mainPanel.add(navTabs, BorderLayout.WEST);
            mainPanel.add(cardPanel, BorderLayout.CENTER);
            mainPanel.add(detailsPanel, BorderLayout.EAST);
            add(mainPanel, BorderLayout.CENTER);
            setLocationRelativeTo(null);

            // Navigation logic: switch main panel on tab change
            navTabs.addChangeListener(e -> {
                String tab = navTabs.getTitleAt(navTabs.getSelectedIndex());
                java.awt.CardLayout cl = (java.awt.CardLayout)(cardPanel.getLayout());
                cl.show(cardPanel, tab);
            });

            // Show first panel by default
            ((java.awt.CardLayout)cardPanel.getLayout()).show(cardPanel, "Assets");
            private JPanel createAssetsPanel() {
                JPanel panel = new JPanel(new BorderLayout());
                JTextArea area = new JTextArea("List and edit game assets here (images, sounds, etc).\nAdd/Remove/Update asset paths and metadata.");
                area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
                JButton saveBtn = new JButton("Save Assets");
                JButton loadBtn = new JButton("Load Assets");
                JPanel btnPanel = new JPanel();
                btnPanel.add(saveBtn);
                btnPanel.add(loadBtn);
                panel.add(new JScrollPane(area), BorderLayout.CENTER);
                panel.add(btnPanel, BorderLayout.SOUTH);
                saveBtn.addActionListener(e -> saveTextAreaToFile(area, "assets.txt"));
                loadBtn.addActionListener(e -> loadTextAreaFromFile(area, "assets.txt"));
                return panel;
            }

            // Editable title panel
            private JPanel createTitlePanel() {
                JPanel panel = new JPanel(new BorderLayout());
                JTextField titleField = new JTextField("Edit game title here");
                titleField.setFont(new Font(Font.MONOSPACED, Font.BOLD, 16));
                JButton saveBtn = new JButton("Save Title");
                JButton loadBtn = new JButton("Load Title");
                JPanel btnPanel = new JPanel();
                btnPanel.add(saveBtn);
                btnPanel.add(loadBtn);
                panel.add(titleField, BorderLayout.CENTER);
                panel.add(btnPanel, BorderLayout.SOUTH);
                saveBtn.addActionListener(e -> saveTextFieldToFile(titleField, "title.txt"));
                loadBtn.addActionListener(e -> loadTextFieldFromFile(titleField, "title.txt"));
                return panel;
            }

            // Editable monsters panel
            private JPanel createMonstersPanel() {
                JPanel panel = new JPanel(new BorderLayout());
                JTextArea area = new JTextArea("Edit monsters here (name, stats, abilities, drops, etc).\nAdd/Remove/Update monsters.");
                area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
                JButton saveBtn = new JButton("Save Monsters");
                JButton loadBtn = new JButton("Load Monsters");
                JPanel btnPanel = new JPanel();
                btnPanel.add(saveBtn);
                btnPanel.add(loadBtn);
                panel.add(new JScrollPane(area), BorderLayout.CENTER);
                panel.add(btnPanel, BorderLayout.SOUTH);
                saveBtn.addActionListener(e -> saveTextAreaToFile(area, "monsters.txt"));
                loadBtn.addActionListener(e -> loadTextAreaFromFile(area, "monsters.txt"));
                return panel;
            }

            // Generic editable text panel for other systems
            private JPanel createEditableTextPanel(String title) {
                JPanel panel = new JPanel(new BorderLayout());
                JTextArea area = new JTextArea("Edit " + title + " here.\nAdd/Remove/Update as needed.");
                area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
                JButton saveBtn = new JButton("Save " + title);
                JButton loadBtn = new JButton("Load " + title);
                JPanel btnPanel = new JPanel();
                btnPanel.add(saveBtn);
                btnPanel.add(loadBtn);
                panel.add(new JScrollPane(area), BorderLayout.CENTER);
                panel.add(btnPanel, BorderLayout.SOUTH);
                saveBtn.addActionListener(e -> saveTextAreaToFile(area, title.toLowerCase().replaceAll(" ", "_") + ".txt"));
                loadBtn.addActionListener(e -> loadTextAreaFromFile(area, title.toLowerCase().replaceAll(" ", "_") + ".txt"));
                return panel;
                // --- File I/O helpers ---
                private void saveTextAreaToFile(JTextArea area, String defaultName) {
                    javax.swing.JFileChooser chooser = new javax.swing.JFileChooser();
                    chooser.setSelectedFile(new java.io.File(defaultName));
                    if (chooser.showSaveDialog(this) == javax.swing.JFileChooser.APPROVE_OPTION) {
                        try (java.io.FileWriter fw = new java.io.FileWriter(chooser.getSelectedFile())) {
                            fw.write(area.getText());
                        } catch (Exception ex) {
                            javax.swing.JOptionPane.showMessageDialog(this, "Error saving file: " + ex.getMessage());
                        }
                    }
                }

                private void loadTextAreaFromFile(JTextArea area, String defaultName) {
                    javax.swing.JFileChooser chooser = new javax.swing.JFileChooser();
                    chooser.setSelectedFile(new java.io.File(defaultName));
                    if (chooser.showOpenDialog(this) == javax.swing.JFileChooser.APPROVE_OPTION) {
                        try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(chooser.getSelectedFile()))) {
                            area.setText("");
                            String line;
                            while ((line = br.readLine()) != null) {
                                area.append(line + "\n");
                            }
                        } catch (Exception ex) {
                            javax.swing.JOptionPane.showMessageDialog(this, "Error loading file: " + ex.getMessage());
                        }
                    }
                }

                private void saveTextFieldToFile(JTextField field, String defaultName) {
                    javax.swing.JFileChooser chooser = new javax.swing.JFileChooser();
                    chooser.setSelectedFile(new java.io.File(defaultName));
                    if (chooser.showSaveDialog(this) == javax.swing.JFileChooser.APPROVE_OPTION) {
                        try (java.io.FileWriter fw = new java.io.FileWriter(chooser.getSelectedFile())) {
                            fw.write(field.getText());
                        } catch (Exception ex) {
                            javax.swing.JOptionPane.showMessageDialog(this, "Error saving file: " + ex.getMessage());
                        }
                    }
                }

                private void loadTextFieldFromFile(JTextField field, String defaultName) {
                    javax.swing.JFileChooser chooser = new javax.swing.JFileChooser();
                    chooser.setSelectedFile(new java.io.File(defaultName));
                    if (chooser.showOpenDialog(this) == javax.swing.JFileChooser.APPROVE_OPTION) {
                        try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(chooser.getSelectedFile()))) {
                            String line = br.readLine();
                            field.setText(line != null ? line : "");
                        } catch (Exception ex) {
                            javax.swing.JOptionPane.showMessageDialog(this, "Error loading file: " + ex.getMessage());
                        }
                    }
                }
            }
        add(tabs, BorderLayout.CENTER);
        setLocationRelativeTo(null);
        private JPanel createAttributesPanel() {
            JPanel panel = new JPanel(new BorderLayout());
            JTextArea area = new JTextArea();
            area.setEditable(false);
            area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
            area.setText("Primary Attributes:\nSTR, DEX, CON, INT, WIS, VIT, SPI, LCK, WIL, CHA\n\n" +
                "STR: Physical power, melee scaling, carry power\n" +
                "DEX: Speed, accuracy, crit chance, evasion\n" +
                "CON: Survivability, stamina, resistance scaling\n" +
                "INT: Spell power, mana scaling\n" +
                "WIS: Healing, status resist, mana efficiency\n" +
                "VIT: HP scaling, durability\n" +
                "SPI: Resource regen, aura power\n" +
                "LCK: Loot quality, proc chance, variance\n" +
                "WIL: Crowd-control resistance, resolve\n" +
                "CHA: Threat shaping, social/reaction systems\n");
            panel.add(new JScrollPane(area), BorderLayout.CENTER);
            return panel;
        }

        private JPanel createStatsPanel() {
            JPanel panel = new JPanel(new BorderLayout());
            JTextArea area = new JTextArea();
            area.setEditable(false);
            area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
            area.setText("Secondary/Sub-Attributes and Combat Stats:\n" +
                "Physical: Physical Power, Armor Penetration, Attack Speed, Accuracy, Evasion, Weapon Handling, Critical Damage %\n" +
                "Magical: Spell Power, Elemental Mastery, Cast Speed, Magic Penetration, Mana Efficiency, Overcharge %\n" +
                "Survival: Max HP, HP Regen, Shield Strength, Damage Reduction %, Block Chance, Parry Chance\n\n" +
                "Combat Stats (Offensive): Base Damage, Skill Damage %, Critical Chance, Critical Damage, Combo Multiplier, Backstab Multiplier, Weak-Point Damage\n" +
                "Combat Stats (Defensive): Armor, Magic Resistance, Damage Mitigation %, Guard Strength, Dodge Chance, Block Value\n" +
                "Tempo/Flow: Attack Speed, Cast Speed, Cooldown Reduction, Global Cooldown, Movement Speed, Turn Priority\n");
            panel.add(new JScrollPane(area), BorderLayout.CENTER);
            return panel;
        }

        private JPanel createResourcesPanel() {
            JPanel panel = new JPanel(new BorderLayout());
            JTextArea area = new JTextArea();
            area.setEditable(false);
            area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
            area.setText("Resources:\nHealth (HP), Mana (MP), Stamina (STA), Energy, Rage, Focus, Faith, Aether, Heat, Sanity\n");
            panel.add(new JScrollPane(area), BorderLayout.CENTER);
            return panel;
        }

        private JPanel createDamageTypesPanel() {
            JPanel panel = new JPanel(new BorderLayout());
            JTextArea area = new JTextArea();
            area.setEditable(false);
            area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
            area.setText("Damage Types:\nPhysical, Fire, Ice, Lightning, Earth, Wind, Water, Light, Dark, Arcane, Poison, Bleed, Void, Chaos, True Damage\n");
            panel.add(new JScrollPane(area), BorderLayout.CENTER);
            return panel;
        }

        private JPanel createResistancesPanel() {
            JPanel panel = new JPanel(new BorderLayout());
            JTextArea area = new JTextArea();
            area.setEditable(false);
            area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
            area.setText("Resistance Layers:\nFlat Resistance, Percentage Resistance, Absorption, Immunity, Vulnerability %\n");
            panel.add(new JScrollPane(area), BorderLayout.CENTER);
            return panel;
        }

        private JPanel createBuffsDebuffsPanel() {
            JPanel panel = new JPanel(new BorderLayout());
            JTextArea area = new JTextArea();
            area.setEditable(false);
            area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
            area.setText("Buffs:\nPower Surge, Berserk, Arcane Amplify, Precision, Shielded, Fortified, Regeneration, Damage Absorption, Invulnerability, Haste, Stealth, Flight, Invisibility, True Sight, Phase Shift\n\n" +
                "Debuffs/Status Effects:\nStun, Freeze, Root, Silence, Fear, Charm, Sleep, Knockback, Knockdown, Burn, Poison, Bleed, Corruption, Frostbite, Shock, Weakness, Frailty, Hex, Slow, Cripple, Curse\n");
            panel.add(new JScrollPane(area), BorderLayout.CENTER);
            return panel;
        }

        private JPanel createEnemyTaxonomyPanel() {
            JPanel panel = new JPanel(new BorderLayout());
            JTextArea area = new JTextArea();
            area.setEditable(false);
            area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
            area.setText("Enemy Taxonomy:\nCommon Mob, Veteran/Strong Mob, Elite, Rare/Named, Boss\n\n" +
                "Boss Classification:\nDungeon Boss, World Boss, Raid Boss, Event Boss, Story Boss, Mythic Boss, Secret Boss\n\n" +
                "Enemy AI Roles:\nBrute, Assassin, Caster, Controller, Summoner, Support, Tank, Sniper\n");
            panel.add(new JScrollPane(area), BorderLayout.CENTER);
            return panel;
        }

        private JPanel createBossSchemaPanel() {
            JPanel panel = new JPanel(new BorderLayout());
            JTextArea area = new JTextArea();
            area.setEditable(false);
            area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
            area.setText("Boss Data Schema:\nName, Rank, Level, Tags, HP Pools (per phase), Damage Profile, Resistances/Immunities, Phase Triggers, Abilities, Adds, Arena Rules, Enrage Rules, AI Profile, Loot Table\n\n" +
                "Boss Phases: HP threshold, Time event, Player performance event, Arena event\n" +
                "Phase Effects: New abilities, Arena hazard changes, Add spawns, Damage type shifts, Enrage\n");
            panel.add(new JScrollPane(area), BorderLayout.CENTER);
            return panel;
        }

        private JPanel createLootPanel() {
            JPanel panel = new JPanel(new BorderLayout());
            JTextArea area = new JTextArea();
            area.setEditable(false);
            area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
            area.setText("Loot and Rewards:\nCurrency, Gear, Crafting materials, Set items, Relics, Mounts/pets, Cosmetics, Titles\n\n" +
                "Drop Modifiers: Luck, Difficulty tier, Boss rank, Kill time, No-death bonus, Weekly lockouts, Pity timers\n");
            panel.add(new JScrollPane(area), BorderLayout.CENTER);
            return panel;
        }
    }

    private JPanel createTextPanel(String title) {
        JPanel panel = new JPanel(new BorderLayout());
        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        area.setText(title + "\n\n"
                + "This editor surface is intentionally minimal.\n"
                + "Hook your specific data providers and save/load actions here.");
        panel.add(new JScrollPane(area), BorderLayout.CENTER);
        return panel;
    }

    public static void showEditor() {
        SwingUtilities.invokeLater(() -> new GameEditorFrame().setVisible(true));
    }
}
