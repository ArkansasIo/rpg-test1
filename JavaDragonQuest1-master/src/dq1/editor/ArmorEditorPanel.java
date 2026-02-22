package dq1.editor;

import java.awt.*;
import java.io.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/**
 * Full-detail Armor Editor Panel for RPG/MMORPG engine.
 */
public class ArmorEditorPanel extends JPanel {
    private final JTable table;
    private final DefaultTableModel model;
    private final JButton addBtn, removeBtn, saveBtn, loadBtn;

    public ArmorEditorPanel() {
        setLayout(new BorderLayout(8, 8));
        String[] columns = {"ID", "Name", "Buy", "Sell", "DEF", "MaxCount", "Class", "Attribute", "Subtype", "SubStat"};
        model = new DefaultTableModel(columns, 0);
        table = new JTable(model);
        JScrollPane scroll = new JScrollPane(table);
        add(scroll, BorderLayout.CENTER);

        JPanel controls = new JPanel();
        addBtn = new JButton("Add");
        removeBtn = new JButton("Remove");
        saveBtn = new JButton("Save");
        loadBtn = new JButton("Load");
        controls.add(addBtn);
        controls.add(removeBtn);
        controls.add(saveBtn);
        controls.add(loadBtn);
        add(controls, BorderLayout.SOUTH);

        addBtn.addActionListener(e -> model.addRow(new Object[columns.length]));
        removeBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) model.removeRow(row);
        });
        saveBtn.addActionListener(e -> saveToFile());
        loadBtn.addActionListener(e -> loadFromFile());
    }

    private void saveToFile() {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("armor.csv"));
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (PrintWriter pw = new PrintWriter(chooser.getSelectedFile())) {
                for (int i = 0; i < model.getRowCount(); i++) {
                    @SuppressWarnings("unchecked")
                    List<Object> row = (List<Object>) model.getDataVector().elementAt(i);
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
        chooser.setSelectedFile(new File("armor.csv"));
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
