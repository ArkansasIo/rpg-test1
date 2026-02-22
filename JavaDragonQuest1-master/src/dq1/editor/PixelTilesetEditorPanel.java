package dq1.editor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

/**
 * PNG pixel-art editor with metadata and indexed assets.
 */
public class PixelTilesetEditorPanel extends JPanel {

    private static final int CELL_SIZE = 22;
    private static final int TRANSPARENT = 0x00000000;
    private static final int[] PALETTE = new int[] {
        0x00000000, 0xFF000000, 0xFFFFFFFF, 0xFFCC0000, 0xFF00AA00, 0xFF0066CC,
        0xFFFFCC00, 0xFFFF8800, 0xFF9900CC, 0xFF00CCCC, 0xFF6E4B2A, 0xFF999999
    };

    private static final String ASSET_DIR = "docs/editor_assets/pixels";
    private static final String INDEX_FILE = "docs/editor_assets/pixels/index.tsv";

    private int pixelWidth = 16;
    private int pixelHeight = 16;
    private int[][] pixels = new int[pixelHeight][pixelWidth];

    private final PixelCanvas canvas = new PixelCanvas();
    private final JLabel status = new JLabel("Ready");
    private final JSpinner brushSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 8, 1));
    private final JSpinner widthSpinner = new JSpinner(new SpinnerNumberModel(16, 8, 64, 1));
    private final JSpinner heightSpinner = new JSpinner(new SpinnerNumberModel(16, 8, 64, 1));
    private final JSpinner idSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 999999, 1));
    private final JTextField nameField = new JTextField(14);
    private final JTextArea detailsArea = new JTextArea(3, 24);
    private final JComboBox<String> assetSelector = new JComboBox<>();
    private final List<AssetMeta> assetList = new ArrayList<>();
    private int selectedColor = 0xFF000000;
    private String currentAssetFile = "";

    public PixelTilesetEditorPanel() {
        super(new BorderLayout(8, 8));
        add(buildTopBar(), BorderLayout.NORTH);
        add(new JScrollPane(canvas), BorderLayout.CENTER);
        add(buildBottomPanel(), BorderLayout.SOUTH);
        initializeCanvas();
        refreshAssetList();
        setPreferredSize(new Dimension(900, 560));
    }

    private JPanel buildTopBar() {
        JPanel top = new JPanel(new BorderLayout(8, 8));

        JPanel palettePanel = new JPanel(new GridLayout(2, 6, 4, 4));
        for (int argb : PALETTE) {
            JButton swatch = new JButton();
            swatch.setPreferredSize(new Dimension(28, 28));
            swatch.setBackground(new Color(argb, true));
            swatch.setOpaque(true);
            swatch.setBorderPainted(true);
            swatch.addActionListener(e -> {
                selectedColor = argb;
                status.setText("Selected color: 0x" + Integer.toHexString(selectedColor).toUpperCase());
            });
            palettePanel.add(swatch);
        }
        top.add(palettePanel, BorderLayout.CENTER);

        JPanel right = new JPanel();
        right.add(new JLabel("Brush:"));
        right.add(brushSpinner);
        right.add(new JLabel("W:"));
        right.add(widthSpinner);
        right.add(new JLabel("H:"));
        right.add(heightSpinner);

        JButton resize = new JButton("Resize");
        resize.addActionListener(e -> resizeCanvas((Integer) widthSpinner.getValue(), (Integer) heightSpinner.getValue()));
        right.add(resize);

        JButton clear = new JButton("Clear");
        clear.addActionListener(e -> {
            clearCanvas();
            status.setText("Canvas cleared.");
        });
        right.add(clear);

        JButton fill = new JButton("Fill");
        fill.addActionListener(e -> {
            for (int r = 0; r < pixelHeight; r++) {
                for (int c = 0; c < pixelWidth; c++) {
                    pixels[r][c] = selectedColor;
                }
            }
            canvas.repaint();
            status.setText("Filled canvas.");
        });
        right.add(fill);

        top.add(right, BorderLayout.EAST);
        return top;
    }

    private JPanel buildBottomPanel() {
        JPanel bottom = new JPanel(new BorderLayout(8, 8));

        JPanel meta = new JPanel();
        meta.add(new JLabel("Asset ID:"));
        meta.add(idSpinner);
        meta.add(new JLabel("Name:"));
        meta.add(nameField);
        meta.add(new JLabel("Details:"));
        detailsArea.setLineWrap(true);
        detailsArea.setWrapStyleWord(true);
        meta.add(new JScrollPane(detailsArea));
        bottom.add(meta, BorderLayout.CENTER);

        JPanel actions = new JPanel();
        actions.add(new JLabel("Asset:"));
        actions.add(assetSelector);

        JButton refresh = new JButton("Refresh Assets");
        refresh.addActionListener(e -> refreshAssetList());
        actions.add(refresh);

        JButton load = new JButton("Load");
        load.addActionListener(e -> loadSelectedAsset());
        actions.add(load);

        JButton importPng = new JButton("Import PNG");
        importPng.addActionListener(e -> importPng());
        actions.add(importPng);

        JButton save = new JButton("Save Asset");
        save.addActionListener(e -> saveAsset());
        actions.add(save);

        bottom.add(actions, BorderLayout.NORTH);
        bottom.add(status, BorderLayout.SOUTH);

        return bottom;
    }

    private void initializeCanvas() {
        clearCanvas();
        canvas.setPreferredSize(new Dimension(pixelWidth * CELL_SIZE + 1, pixelHeight * CELL_SIZE + 1));
        canvas.revalidate();
        canvas.repaint();
    }

    private void clearCanvas() {
        for (int r = 0; r < pixelHeight; r++) {
            for (int c = 0; c < pixelWidth; c++) {
                pixels[r][c] = TRANSPARENT;
            }
        }
        canvas.repaint();
    }

    private void resizeCanvas(int w, int h) {
        int[][] next = new int[h][w];
        for (int r = 0; r < h; r++) {
            for (int c = 0; c < w; c++) {
                if (r < pixelHeight && c < pixelWidth) {
                    next[r][c] = pixels[r][c];
                }
                else {
                    next[r][c] = TRANSPARENT;
                }
            }
        }
        pixelWidth = w;
        pixelHeight = h;
        pixels = next;
        canvas.setPreferredSize(new Dimension(pixelWidth * CELL_SIZE + 1, pixelHeight * CELL_SIZE + 1));
        canvas.revalidate();
        canvas.repaint();
        status.setText("Resized canvas to " + pixelWidth + "x" + pixelHeight + ".");
    }

    private File ensureAssetDir() {
        File dir = new File(ASSET_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    private BufferedImage toImage() {
        BufferedImage out = new BufferedImage(pixelWidth, pixelHeight, BufferedImage.TYPE_INT_ARGB);
        for (int r = 0; r < pixelHeight; r++) {
            for (int c = 0; c < pixelWidth; c++) {
                out.setRGB(c, r, pixels[r][c]);
            }
        }
        return out;
    }

    private void loadFromImage(BufferedImage image) {
        resizeCanvas(image.getWidth(), image.getHeight());
        for (int r = 0; r < pixelHeight; r++) {
            for (int c = 0; c < pixelWidth; c++) {
                pixels[r][c] = image.getRGB(c, r);
            }
        }
        canvas.repaint();
    }

    private void importPng() {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        try {
            BufferedImage src = ImageIO.read(chooser.getSelectedFile());
            if (src == null) {
                status.setText("Import failed: file is not a valid image.");
                return;
            }
            int targetW = (Integer) widthSpinner.getValue();
            int targetH = (Integer) heightSpinner.getValue();
            BufferedImage sized = src;
            if (src.getWidth() != targetW || src.getHeight() != targetH) {
                Image scaled = src.getScaledInstance(targetW, targetH, Image.SCALE_FAST);
                BufferedImage tmp = new BufferedImage(targetW, targetH, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2 = tmp.createGraphics();
                g2.drawImage(scaled, 0, 0, null);
                g2.dispose();
                sized = tmp;
            }
            loadFromImage(sized);
            status.setText("Imported PNG: " + chooser.getSelectedFile().getName());
        }
        catch (Exception ex) {
            status.setText("Import failed: " + ex.getMessage());
        }
    }

    private void saveAsset() {
        try {
            int id = (Integer) idSpinner.getValue();
            String name = nameField.getText().trim();
            String details = detailsArea.getText().trim();
            if (name.isEmpty()) {
                status.setText("Asset name is required.");
                return;
            }
            File dir = ensureAssetDir();
            String fileName = id + "_" + slugify(name) + ".png";
            File pngFile = new File(dir, fileName);
            ImageIO.write(toImage(), "png", pngFile);

            AssetMeta meta = new AssetMeta(id, name, fileName, pixelWidth, pixelHeight, details);
            upsertAsset(meta);
            writeIndex();
            refreshAssetList();
            currentAssetFile = fileName;
            status.setText("Saved asset: " + pngFile.getAbsolutePath());
        }
        catch (Exception ex) {
            status.setText("Save failed: " + ex.getMessage());
        }
    }

    private void refreshAssetList() {
        readIndex();
        assetSelector.removeAllItems();
        for (AssetMeta meta : assetList) {
            assetSelector.addItem(meta.label());
        }
        if (!currentAssetFile.isEmpty()) {
            for (int i = 0; i < assetList.size(); i++) {
                if (assetList.get(i).fileName.equals(currentAssetFile)) {
                    assetSelector.setSelectedIndex(i);
                    break;
                }
            }
        }
        status.setText("Loaded " + assetList.size() + " pixel assets.");
    }

    private void loadSelectedAsset() {
        int idx = assetSelector.getSelectedIndex();
        if (idx < 0 || idx >= assetList.size()) {
            status.setText("Select an asset first.");
            return;
        }
        AssetMeta meta = assetList.get(idx);
        try {
            File file = new File(ensureAssetDir(), meta.fileName);
            if (!file.exists()) {
                status.setText("PNG missing: " + file.getAbsolutePath());
                return;
            }
            BufferedImage img = ImageIO.read(file);
            if (img == null) {
                status.setText("Invalid PNG file.");
                return;
            }
            idSpinner.setValue(meta.id);
            nameField.setText(meta.name);
            detailsArea.setText(meta.details);
            widthSpinner.setValue(meta.width);
            heightSpinner.setValue(meta.height);
            loadFromImage(img);
            currentAssetFile = meta.fileName;
            status.setText("Loaded asset #" + meta.id + " (" + meta.name + ")");
        }
        catch (Exception ex) {
            status.setText("Load failed: " + ex.getMessage());
        }
    }

    private void upsertAsset(AssetMeta value) {
        for (int i = 0; i < assetList.size(); i++) {
            AssetMeta existing = assetList.get(i);
            if (existing.id == value.id || existing.fileName.equals(value.fileName)) {
                assetList.set(i, value);
                return;
            }
        }
        assetList.add(value);
    }

    private void readIndex() {
        assetList.clear();
        File index = new File(INDEX_FILE);
        if (!index.exists()) {
            return;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(index))) {
            String line;
            boolean header = true;
            while ((line = br.readLine()) != null) {
                if (header) {
                    header = false;
                    continue;
                }
                String[] p = line.split("\t", -1);
                if (p.length < 6) {
                    continue;
                }
                assetList.add(new AssetMeta(
                        Integer.parseInt(p[0]),
                        unescape(p[1]),
                        p[2],
                        Integer.parseInt(p[3]),
                        Integer.parseInt(p[4]),
                        unescape(p[5])));
            }
        }
        catch (Exception ex) {
            status.setText("Index load warning: " + ex.getMessage());
        }
    }

    private void writeIndex() throws Exception {
        ensureAssetDir();
        File index = new File(INDEX_FILE);
        try (FileWriter fw = new FileWriter(index, false)) {
            fw.write("id\tname\tfile\twidth\theight\tdetails\n");
            for (AssetMeta meta : assetList) {
                fw.write(meta.id + "\t" + escape(meta.name) + "\t" + meta.fileName + "\t"
                        + meta.width + "\t" + meta.height + "\t" + escape(meta.details) + "\n");
            }
        }
    }

    private static String slugify(String s) {
        String n = s.toLowerCase().replaceAll("[^a-z0-9]+", "_");
        n = n.replaceAll("^_+|_+$", "");
        return n.isEmpty() ? "asset" : n;
    }

    private static String escape(String s) {
        return s.replace("\\", "\\\\").replace("\t", "\\t").replace("\n", "\\n").replace("\r", "");
    }

    private static String unescape(String s) {
        return s.replace("\\t", "\t").replace("\\n", "\n").replace("\\\\", "\\");
    }

    private final class PixelCanvas extends JPanel {
        PixelCanvas() {
            setPreferredSize(new Dimension(pixelWidth * CELL_SIZE + 1, pixelHeight * CELL_SIZE + 1));
            setBackground(new Color(24, 24, 26));
            MouseAdapter painter = new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    paintAt(e);
                }

                @Override
                public void mouseDragged(MouseEvent e) {
                    paintAt(e);
                }
            };
            addMouseListener(painter);
            addMouseMotionListener(painter);
        }

        private void paintAt(MouseEvent e) {
            int col = e.getX() / CELL_SIZE;
            int row = e.getY() / CELL_SIZE;
            if (row < 0 || col < 0 || row >= pixelHeight || col >= pixelWidth) {
                return;
            }
            if (e.getButton() == MouseEvent.BUTTON2) {
                selectedColor = pixels[row][col];
                status.setText("Picked color: 0x" + Integer.toHexString(selectedColor).toUpperCase());
                return;
            }
            int brush = (Integer) brushSpinner.getValue();
            int color = (e.getModifiersEx() & MouseEvent.BUTTON3_DOWN_MASK) != 0 ? TRANSPARENT : selectedColor;
            for (int rr = row; rr < row + brush; rr++) {
                for (int cc = col; cc < col + brush; cc++) {
                    if (rr >= 0 && cc >= 0 && rr < pixelHeight && cc < pixelWidth) {
                        pixels[rr][cc] = color;
                    }
                }
            }
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            for (int row = 0; row < pixelHeight; row++) {
                for (int col = 0; col < pixelWidth; col++) {
                    int x = col * CELL_SIZE;
                    int y = row * CELL_SIZE;
                    if (pixels[row][col] == TRANSPARENT) {
                        g2.setColor(((row + col) & 1) == 0 ? new Color(58, 58, 60) : new Color(74, 74, 76));
                        g2.fillRect(x, y, CELL_SIZE, CELL_SIZE);
                    }
                    else {
                        g2.setColor(new Color(pixels[row][col], true));
                        g2.fillRect(x, y, CELL_SIZE, CELL_SIZE);
                    }
                    g2.setColor(new Color(0, 0, 0, 48));
                    g2.drawRect(x, y, CELL_SIZE, CELL_SIZE);
                }
            }
        }
    }

    private static final class AssetMeta {
        private final int id;
        private final String name;
        private final String fileName;
        private final int width;
        private final int height;
        private final String details;

        private AssetMeta(int id, String name, String fileName, int width, int height, String details) {
            this.id = id;
            this.name = name;
            this.fileName = fileName;
            this.width = width;
            this.height = height;
            this.details = details == null ? "" : details;
        }

        private String label() {
            return id + " - " + name + " (" + width + "x" + height + ")";
        }
    }
}
