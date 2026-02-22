package dq1.editor;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AssetBrowserPanel extends JPanel {

    private JList<File> list;
    private DefaultListModel<File> model;

    public AssetBrowserPanel() {
        setLayout(new BorderLayout());
        model = new DefaultListModel<>();
        list = new JList<>(model);
        list.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof File) setText(((File) value).getName());
                return this;
            }
        });
        add(new JScrollPane(list), BorderLayout.CENTER);
        refresh();
    }

    public void refresh() {
        model.clear();
        File dir = new File("assets/res");
        if (!dir.exists()) return;
        List<File> files = listFilesRecursive(dir);
        for (File f : files) model.addElement(f);
    }

    private List<File> listFilesRecursive(File dir) {
        List<File> out = new ArrayList<>();
        File[] children = dir.listFiles();
        if (children == null) return out;
        for (File f : children) {
            if (f.isDirectory()) out.addAll(listFilesRecursive(f));
            else out.add(f);
        }
        return out;
    }

    public void addSelectionListener(ListSelectionListener l) {
        list.addListSelectionListener(l);
    }

    public File getSelected() {
        return list.getSelectedValue();
    }
}
