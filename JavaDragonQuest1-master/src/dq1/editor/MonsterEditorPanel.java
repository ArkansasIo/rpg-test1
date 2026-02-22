package dq1.editor;

import java.awt.*;
import java.io.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/**
 * Full-detail Monster Editor Panel for RPG/MMORPG engine.
 */

public class MonsterEditorPanel extends JPanel {
    private JTable table;
    private DefaultTableModel model;
    private final JButton addBtn, removeBtn, saveBtn, loadBtn, scriptBtn, batchEditBtn, undoBtn, redoBtn;
    private java.util.Stack<Object[]> undoStack = new java.util.Stack<>();
    private java.util.Stack<Object[]> redoStack = new java.util.Stack<>();
    // Store script file paths for each monster row (by index)
    private java.util.Map<Integer, String> monsterScriptFiles = new java.util.HashMap<>();

    public MonsterEditorPanel() {
        setLayout(new BorderLayout(8, 8));
        String[] columns = {"ID", "Name", "STR", "AGI", "HP", "PAT", "SR", "DR", "XP", "GP", "GroupID", "FinalBoss", "Type", "Class", "Attribute", "Subtype", "SubStat"};
        model = new DefaultTableModel(columns, 0);
        table = new JTable(model);
        JScrollPane scroll = new JScrollPane(table);
        add(scroll, BorderLayout.CENTER);

        JPanel controls = new JPanel();
        addBtn = new JButton("Add");
        removeBtn = new JButton("Remove");
        saveBtn = new JButton("Save");
        loadBtn = new JButton("Load");
        scriptBtn = new JButton("Open Visual Scripting");
        controls.add(addBtn);
        controls.add(removeBtn);
        controls.add(saveBtn);
        controls.add(loadBtn);
        controls.add(scriptBtn);
        batchEditBtn = new JButton("Batch Edit");
        controls.add(batchEditBtn);
        undoBtn = new JButton("Undo");
        redoBtn = new JButton("Redo");
        controls.add(undoBtn);
        controls.add(redoBtn);
        add(controls, BorderLayout.SOUTH);

        addBtn.addActionListener(e -> {
            Object[] rowData = new Object[columns.length];
            model.addRow(rowData);
            pushUndo();
        });
        removeBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                model.removeRow(row);
                pushUndo();
            }
        });
        saveBtn.addActionListener(e -> saveToFile());
        loadBtn.addActionListener(e -> loadFromFile());

        scriptBtn.addActionListener(e -> openMonsterScriptDialog());
        batchEditBtn.addActionListener(e -> {
            openBatchEditDialog();
            pushUndo();
        });
        undoBtn.addActionListener(e -> undoEdit());
        redoBtn.addActionListener(e -> redoEdit());
            // Undo/redo support
            private void pushUndo() {
                Object[][] snapshot = new Object[model.getRowCount()][model.getColumnCount()];
                for (int i = 0; i < model.getRowCount(); i++) {
                    for (int j = 0; j < model.getColumnCount(); j++) {
                        snapshot[i][j] = model.getValueAt(i, j);
                    }
                }
                undoStack.push(flatten(snapshot));
                redoStack.clear();
            }

            private void undoEdit() {
                if (!undoStack.isEmpty()) {
                    Object[] prev = undoStack.pop();
                    redoStack.push(flatten(getCurrentTable()));
                    restoreTable(prev);
                }
            }

            private void redoEdit() {
                if (!redoStack.isEmpty()) {
                    Object[] next = redoStack.pop();
                    undoStack.push(flatten(getCurrentTable()));
                    restoreTable(next);
                }
            }

            private Object[][] getCurrentTable() {
                Object[][] snapshot = new Object[model.getRowCount()][model.getColumnCount()];
                for (int i = 0; i < model.getRowCount(); i++) {
                    for (int j = 0; j < model.getColumnCount(); j++) {
                        snapshot[i][j] = model.getValueAt(i, j);
                    }
                }
                return snapshot;
            }

            private Object[] flatten(Object[][] arr) {
                java.util.List<Object> flat = new java.util.ArrayList<>();
                for (Object[] row : arr) for (Object val : row) flat.add(val);
                return flat.toArray();
            }

            private void restoreTable(Object[] flat) {
                int cols = model.getColumnCount();
                int rows = flat.length / cols;
                model.setRowCount(0);
                for (int i = 0; i < rows; i++) {
                    Object[] row = new Object[cols];
                    for (int j = 0; j < cols; j++) {
                        row[j] = flat[i * cols + j];
                    }
                    model.addRow(row);
                }
            }
        // Batch edit dialog for multiple monsters
        private void openBatchEditDialog() {
            int[] selectedRows = table.getSelectedRows();
            if (selectedRows.length == 0) {
                JOptionPane.showMessageDialog(this, "Select one or more monster rows to batch edit.");
                return;
            }
            JPanel panel = new JPanel(new GridLayout(0, 2));
            JTextField hpField = new JTextField();
            panel.add(new JLabel("Set HP to:"));
            panel.add(hpField);
            JTextField strField = new JTextField();
            panel.add(new JLabel("Set STR to:"));
            panel.add(strField);
            int result = JOptionPane.showConfirmDialog(this, panel, "Batch Edit Monsters", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                String hpText = hpField.getText();
                String strText = strField.getText();
                for (int row : selectedRows) {
                    if (!hpText.isEmpty()) model.setValueAt(hpText, row, 4); // HP column
                    if (!strText.isEmpty()) model.setValueAt(strText, row, 2); // STR column
                }
            }
        }
    }

    // Open the visual scripting dialog for the selected monster, allowing association of a script file
    private void openMonsterScriptDialog() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a monster row first.");
            return;
        }
        String monsterName = (String) model.getValueAt(row, 1);
        String scriptFile = monsterScriptFiles.getOrDefault(row, "monster_" + row + ".vsgraph");

        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Visual Scripting for: " + monsterName, Dialog.ModalityType.APPLICATION_MODAL);
        VisualScriptingPanel panel = new VisualScriptingPanel();
        JPanel toolbar = panel.createToolbar();
        JButton associateBtn = new JButton("Associate Script File");
        toolbar.add(associateBtn);
        associateBtn.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setSelectedFile(new File(scriptFile));
            if (chooser.showSaveDialog(dialog) == JFileChooser.APPROVE_OPTION) {
                monsterScriptFiles.put(row, chooser.getSelectedFile().getAbsolutePath());
                JOptionPane.showMessageDialog(dialog, "Script file associated: " + chooser.getSelectedFile().getName());
            }
        });

        // Load script if file exists
        File f = new File(scriptFile);
        if (f.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(f))) {
                java.util.List<String> lines = new java.util.ArrayList<>();
                String line;
                while ((line = br.readLine()) != null) lines.add(line);
                // Use VisualScriptingPanel's load logic
                panel.clearGraph();
                for (String l : lines) {
                    if (l.startsWith("NODE,")) {
                        String[] parts = l.split(",");
                        if (parts.length >= 5) {
                            String label = parts[1];
                            int x = Integer.parseInt(parts[2]);
                            int y = Integer.parseInt(parts[3]);
                            VisualScriptingPanel.NodeType type = VisualScriptingPanel.NodeType.valueOf(parts[4]);
                            panel.addGraphNode(label, x, y, type);
                        }
                    }
                }
                for (String l : lines) {
                    if (l.startsWith("CONN,")) {
                        String[] parts = l.split(",");
                        if (parts.length >= 3) {
                            int fromIdx = Integer.parseInt(parts[1]);
                            int toIdx = Integer.parseInt(parts[2]);
                            panel.addGraphConnection(fromIdx, toIdx);
                        }
                    }
                }
                panel.repaint();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error loading script: " + ex.getMessage());
            }
        }

        dialog.getContentPane().setLayout(new BorderLayout());
        dialog.getContentPane().add(toolbar, BorderLayout.NORTH);
        dialog.getContentPane().add(panel, BorderLayout.CENTER);
        dialog.setSize(900, 650);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void saveToFile() {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("monsters.csv"));
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (PrintWriter pw = new PrintWriter(chooser.getSelectedFile())) {
                for (int i = 0; i < model.getRowCount(); i++) {
                    @SuppressWarnings("unchecked")
                    List<Object> row = (List<Object>) model.getDataVector().elementAt(i);
                    // Data validation
                    StringBuilder errors = new StringBuilder();
                    String name = String.valueOf(row.get(1));
                    String hpStr = String.valueOf(row.get(4));
                    String strStr = String.valueOf(row.get(2));
                    if (name == null || name.trim().isEmpty()) errors.append("Name missing; ");
                    try { int hp = Integer.parseInt(hpStr); if (hp < 0) errors.append("HP negative; "); } catch (Exception e) { errors.append("HP invalid; "); }
                    try { int str = Integer.parseInt(strStr); if (str < 0) errors.append("STR negative; "); } catch (Exception e) { errors.append("STR invalid; "); }
                    if (errors.length() > 0) {
                        JOptionPane.showMessageDialog(this, "Row " + (i+1) + " error: " + errors.toString());
                        return;
                    }
                    for (int j = 0; j < row.size(); j++) {
                        pw.print(row.get(j));
                        if (j < row.size() - 1) pw.print(",");
                    }
                    pw.println();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error saving: " + ex.getMessage());
            }
        }
    }

    private void loadFromFile() {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("monsters.csv"));
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (BufferedReader br = new BufferedReader(new FileReader(chooser.getSelectedFile()))) {
                model.setRowCount(0);
                String line;
                while ((line = br.readLine()) != null) {
                    String[] parts = line.split(",");
                    model.addRow(parts);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error loading: " + ex.getMessage());
            }
        }
    }
}
