package dq1.editor;

import dq1.tools.AssetCsvTool;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
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

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton refresh = new JButton("Refresh");
        JButton importBtn = new JButton("Import...");
        JButton exportCsv = new JButton("Export CSV");
        top.add(refresh);
        top.add(importBtn);
        top.add(exportCsv);
        add(top, BorderLayout.NORTH);

        refresh.addActionListener(e -> refresh());
        importBtn.addActionListener(e -> importFiles());
        exportCsv.addActionListener(e -> {
            try {
                Path root = Path.of("assets/res");
                Path out = Path.of("assets/res/assets_list.csv");
                AssetCsvTool.exportAssetsCsv(root, out);
                JOptionPane.showMessageDialog(this, "Exported assets CSV to: " + out.toAbsolutePath());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Export failed: " + ex.getMessage());
            }
        });

        // enable drag-out: copy absolute path when dragging
        list.setDragEnabled(true);
        list.setTransferHandler(new TransferHandler("selectedValue"));

        // enable drop-in (simple): accept files dropped into the panel and copy them
        new DropTarget(this, DnDConstants.ACTION_COPY_OR_MOVE, new java.awt.dnd.DropTargetListener() {
            @Override
            public void dragEnter(java.awt.dnd.DropTargetDragEvent dtde) { }

            @Override
            public void dragOver(java.awt.dnd.DropTargetDragEvent dtde) { }

            @Override
            public void dropActionChanged(java.awt.dnd.DropTargetDragEvent dtde) { }

            @Override
            public void dragExit(java.awt.dnd.DropTargetEvent dte) { }

            @Override
            public void drop(DropTargetDropEvent de) {
                try {
                    de.acceptDrop(DnDConstants.ACTION_COPY);
                    @SuppressWarnings("unchecked")
                    java.util.List<File> dropped = (java.util.List<File>) de.getTransferable().getTransferData(java.awt.datatransfer.DataFlavor.javaFileListFlavor);
                    for (File f : dropped) {
                        AssetBrowserPanel.this.copyToAssets(f.toPath());
                    }
                    de.dropComplete(true);
                    AssetBrowserPanel.this.refresh();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    try { de.dropComplete(false); } catch (Exception ignored) {}
                }
            }
        }, true);

        refresh();
    }

    private void importFiles() {
        JFileChooser chooser = new JFileChooser();
        chooser.setMultiSelectionEnabled(true);
        int r = chooser.showOpenDialog(this);
        if (r == JFileChooser.APPROVE_OPTION) {
            File[] files = chooser.getSelectedFiles();
            for (File f : files) {
                try {
                    copyToAssets(f.toPath());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            refresh();
        }
    }

    private void copyToAssets(Path src) throws Exception {
        Path destDir = new File("assets/res").toPath();
        if (!Files.exists(destDir)) Files.createDirectories(destDir);
        Path dest = destDir.resolve(src.getFileName());
        Files.copy(src, dest);
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
