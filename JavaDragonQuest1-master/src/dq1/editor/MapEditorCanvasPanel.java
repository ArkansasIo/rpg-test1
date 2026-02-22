package dq1.editor;

import dq1.core.GameAPI;
import dq1.core.Resource;
import dq1.core.Tile;
import dq1.core.TileMap;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JPanel;

/**
 * Multi-layer map editing canvas with tools and undo/redo.
 */
public class MapEditorCanvasPanel extends JPanel {

    public enum Tool {
        Paint, Erase, Fill, Rect, Eyedropper
    }

    public enum Layer {
        Base, Decoration, Collision
    }

    private static final class CellChange {
        private final Layer layer;
        private final int row;
        private final int col;
        private final int oldValue;
        private int newValue;

        private CellChange(Layer layer, int row, int col, int oldValue, int newValue) {
            this.layer = layer;
            this.row = row;
            this.col = col;
            this.oldValue = oldValue;
            this.newValue = newValue;
        }
    }

    private TileMap map;
    private String mapId;
    private int brushTileId = 1;
    private int zoom = 2;
    private boolean showGrid = true;
    private Tool tool = Tool.Paint;
    private Layer layer = Layer.Base;
    private int[][] decorationLayer;
    private boolean[][] collisionLayer;
    private Point rectStart;
    private int hoverRow = -1;
    private int hoverCol = -1;
    private final ArrayDeque<List<CellChange>> undo = new ArrayDeque<>();
    private final ArrayDeque<List<CellChange>> redo = new ArrayDeque<>();
    private Map<Long, CellChange> activeStroke = null;
    private java.util.function.IntConsumer tilePickListener;

    public MapEditorCanvasPanel() {
        setBackground(new Color(24, 24, 26));
        java.awt.event.MouseAdapter painter = new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                handlePress(e);
            }

            @Override
            public void mouseDragged(java.awt.event.MouseEvent e) {
                handleDrag(e);
            }

            @Override
            public void mouseReleased(java.awt.event.MouseEvent e) {
                handleRelease(e);
            }

            @Override
            public void mouseMoved(java.awt.event.MouseEvent e) {
                updateHover(e);
            }
        };
        addMouseListener(painter);
        addMouseMotionListener(painter);
    }

    public void setTilePickListener(java.util.function.IntConsumer tilePickListener) {
        this.tilePickListener = tilePickListener;
    }

    public void setBrushTileId(int value) {
        brushTileId = Math.max(0, value);
    }

    public void setZoom(int value) {
        zoom = Math.max(1, Math.min(4, value));
        refreshPreferredSize();
        repaint();
    }

    public void setShowGrid(boolean value) {
        showGrid = value;
        repaint();
    }

    public void setTool(Tool value) {
        if (value != null) {
            tool = value;
        }
    }

    public void setLayer(Layer value) {
        if (value != null) {
            layer = value;
        }
    }

    public void loadMap(String newMapId) {
        try {
            map = Resource.getTileMap(newMapId);
            mapId = newMapId;
            ensureEditorLayers();
            undo.clear();
            redo.clear();
        }
        catch (Exception ignored) {
            map = null;
            mapId = null;
            decorationLayer = null;
            collisionLayer = null;
        }
        refreshPreferredSize();
        repaint();
    }

    public void clearActiveLayer() {
        if (map == null) {
            return;
        }
        beginStroke();
        int rows = map.getRows();
        int cols = map.getCols();
        int replacement = layer == Layer.Collision ? 0 : -1;
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                changeCell(row, col, replacement);
            }
        }
        commitStroke();
        repaint();
    }

    public void undo() {
        if (undo.isEmpty()) {
            return;
        }
        List<CellChange> stroke = undo.pop();
        for (int i = stroke.size() - 1; i >= 0; i--) {
            apply(stroke.get(i), false);
        }
        redo.push(stroke);
        repaint();
    }

    public void redo() {
        if (redo.isEmpty()) {
            return;
        }
        List<CellChange> stroke = redo.pop();
        for (CellChange change : stroke) {
            apply(change, true);
        }
        undo.push(stroke);
        repaint();
    }

    public String exportSessionCsv(String outputRelativePath) {
        if (map == null) {
            return "Map not loaded.";
        }
        try {
            Path output = Path.of(outputRelativePath);
            if (output.getParent() != null) {
                Files.createDirectories(output.getParent());
            }
            List<String> lines = new ArrayList<>();
            lines.add("map_id," + map.getId());
            lines.add("cols," + map.getCols());
            lines.add("rows," + map.getRows());
            lines.add("base_layer");
            for (int row = 0; row < map.getRows(); row++) {
                lines.add(buildRowBase(row));
            }
            lines.add("decoration_layer");
            for (int row = 0; row < map.getRows(); row++) {
                lines.add(buildRowDecoration(row));
            }
            lines.add("collision_layer");
            for (int row = 0; row < map.getRows(); row++) {
                lines.add(buildRowCollision(row));
            }
            Files.write(output, lines, StandardCharsets.UTF_8);
            return "Exported session: " + output.toAbsolutePath();
        }
        catch (Exception ex) {
            return "Export error: " + ex.getMessage();
        }
    }

    public List<String> getEditorSummaryLines() {
        List<String> lines = new ArrayList<>();
        lines.add("Undo stack: " + undo.size() + " | Redo stack: " + redo.size());
        int decoCount = 0;
        int collisionCount = 0;
        if (map != null && decorationLayer != null && collisionLayer != null) {
            for (int row = 0; row < map.getRows(); row++) {
                for (int col = 0; col < map.getCols(); col++) {
                    if (decorationLayer[row][col] >= 0) {
                        decoCount++;
                    }
                    if (collisionLayer[row][col]) {
                        collisionCount++;
                    }
                }
            }
        }
        lines.add("Decoration cells: " + decoCount);
        lines.add("Collision cells: " + collisionCount);
        if (hoverRow >= 0 && hoverCol >= 0 && map != null) {
            lines.add("Cursor: row=" + hoverRow + ", col=" + hoverCol + ", tile=" + getVisibleTileId(hoverRow, hoverCol));
        }
        return lines;
    }

    private String buildRowBase(int row) {
        StringBuilder sb = new StringBuilder();
        for (int col = 0; col < map.getCols(); col++) {
            if (col > 0) {
                sb.append(',');
            }
            Tile tile = map.getTile(row, col);
            sb.append(tile == null ? -1 : tile.getId());
        }
        return sb.toString();
    }

    private String buildRowDecoration(int row) {
        StringBuilder sb = new StringBuilder();
        for (int col = 0; col < map.getCols(); col++) {
            if (col > 0) {
                sb.append(',');
            }
            sb.append(decorationLayer[row][col]);
        }
        return sb.toString();
    }

    private String buildRowCollision(int row) {
        StringBuilder sb = new StringBuilder();
        for (int col = 0; col < map.getCols(); col++) {
            if (col > 0) {
                sb.append(',');
            }
            sb.append(collisionLayer[row][col] ? 1 : 0);
        }
        return sb.toString();
    }

    private void ensureEditorLayers() {
        if (map == null) {
            return;
        }
        int rows = map.getRows();
        int cols = map.getCols();
        if (decorationLayer == null || decorationLayer.length != rows || decorationLayer[0].length != cols) {
            decorationLayer = new int[rows][cols];
            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < cols; c++) {
                    decorationLayer[r][c] = -1;
                }
            }
        }
        if (collisionLayer == null || collisionLayer.length != rows || collisionLayer[0].length != cols) {
            collisionLayer = new boolean[rows][cols];
        }
    }

    private void handlePress(java.awt.event.MouseEvent e) {
        int[] cell = mouseToCell(e.getX(), e.getY());
        if (cell == null) {
            return;
        }
        int row = cell[0];
        int col = cell[1];
        if (tool == Tool.Eyedropper) {
            int picked = getVisibleTileId(row, col);
            if (picked >= 0 && tilePickListener != null) {
                tilePickListener.accept(picked);
            }
            return;
        }
        if (tool == Tool.Fill) {
            beginStroke();
            floodFill(row, col, getLayerValue(row, col), getReplacementValue());
            commitStroke();
            repaint();
            return;
        }
        if (tool == Tool.Rect) {
            rectStart = new Point(col, row);
            return;
        }
        beginStroke();
        paintSingle(row, col);
        repaint();
    }

    private void handleDrag(java.awt.event.MouseEvent e) {
        updateHover(e);
        int[] cell = mouseToCell(e.getX(), e.getY());
        if (cell == null) {
            return;
        }
        if (tool == Tool.Paint || tool == Tool.Erase) {
            paintSingle(cell[0], cell[1]);
            repaint();
        }
    }

    private void handleRelease(java.awt.event.MouseEvent e) {
        if (tool == Tool.Rect && rectStart != null) {
            int[] cell = mouseToCell(e.getX(), e.getY());
            if (cell != null) {
                int startRow = Math.min(rectStart.y, cell[0]);
                int endRow = Math.max(rectStart.y, cell[0]);
                int startCol = Math.min(rectStart.x, cell[1]);
                int endCol = Math.max(rectStart.x, cell[1]);
                beginStroke();
                int replacement = getReplacementValue();
                for (int row = startRow; row <= endRow; row++) {
                    for (int col = startCol; col <= endCol; col++) {
                        changeCell(row, col, replacement);
                    }
                }
                commitStroke();
                repaint();
            }
            rectStart = null;
            return;
        }
        if (tool == Tool.Paint || tool == Tool.Erase) {
            commitStroke();
        }
    }

    private void updateHover(java.awt.event.MouseEvent e) {
        int[] cell = mouseToCell(e.getX(), e.getY());
        if (cell == null) {
            hoverRow = -1;
            hoverCol = -1;
        }
        else {
            hoverRow = cell[0];
            hoverCol = cell[1];
        }
        repaint();
    }

    private int[] mouseToCell(int px, int py) {
        if (map == null) {
            return null;
        }
        int tileSize = 16 * zoom;
        int col = px / tileSize;
        int row = py / tileSize;
        if (row < 0 || col < 0 || row >= map.getRows() || col >= map.getCols()) {
            return null;
        }
        return new int[] { row, col };
    }

    private void paintSingle(int row, int col) {
        changeCell(row, col, getReplacementValue());
    }

    private int getReplacementValue() {
        if (tool == Tool.Erase) {
            return layer == Layer.Collision ? 0 : -1;
        }
        if (layer == Layer.Collision) {
            return 1;
        }
        return brushTileId;
    }

    private int getVisibleTileId(int row, int col) {
        if (decorationLayer[row][col] >= 0) {
            return decorationLayer[row][col];
        }
        Tile tile = map.getTile(row, col);
        return tile == null ? -1 : tile.getId();
    }

    private int getLayerValue(int row, int col) {
        if (layer == Layer.Base) {
            Tile tile = map.getTile(row, col);
            return tile == null ? -1 : tile.getId();
        }
        if (layer == Layer.Decoration) {
            return decorationLayer[row][col];
        }
        return collisionLayer[row][col] ? 1 : 0;
    }

    private void setLayerValue(int row, int col, int value) {
        if (layer == Layer.Base) {
            if (value >= 0) {
                map.setTile(row, col, value);
                if (mapId != null) {
                    GameAPI.setMapTile(mapId, row, col, value);
                }
            }
            return;
        }
        if (layer == Layer.Decoration) {
            decorationLayer[row][col] = value;
            return;
        }
        collisionLayer[row][col] = value != 0;
    }

    private void beginStroke() {
        activeStroke = new LinkedHashMap<>();
    }

    private void commitStroke() {
        if (activeStroke == null || activeStroke.isEmpty()) {
            activeStroke = null;
            return;
        }
        undo.push(new ArrayList<>(activeStroke.values()));
        while (undo.size() > 120) {
            undo.removeLast();
        }
        redo.clear();
        activeStroke = null;
    }

    private void changeCell(int row, int col, int newValue) {
        int oldValue = getLayerValue(row, col);
        if (oldValue == newValue) {
            return;
        }
        setLayerValue(row, col, newValue);
        if (activeStroke != null) {
            long key = (((long) row) << 32) | (col & 0xffffffffL);
            CellChange existing = activeStroke.get(key);
            if (existing == null) {
                activeStroke.put(key, new CellChange(layer, row, col, oldValue, newValue));
            }
            else {
                existing.newValue = newValue;
            }
        }
    }

    private void floodFill(int startRow, int startCol, int target, int replacement) {
        if (target == replacement) {
            return;
        }
        boolean[][] seen = new boolean[map.getRows()][map.getCols()];
        ArrayDeque<Point> queue = new ArrayDeque<>();
        queue.add(new Point(startCol, startRow));
        while (!queue.isEmpty()) {
            Point p = queue.removeFirst();
            int row = p.y;
            int col = p.x;
            if (row < 0 || col < 0 || row >= map.getRows() || col >= map.getCols() || seen[row][col]) {
                continue;
            }
            seen[row][col] = true;
            if (getLayerValue(row, col) != target) {
                continue;
            }
            changeCell(row, col, replacement);
            queue.add(new Point(col + 1, row));
            queue.add(new Point(col - 1, row));
            queue.add(new Point(col, row + 1));
            queue.add(new Point(col, row - 1));
        }
    }

    private void apply(CellChange change, boolean useNew) {
        Layer previous = layer;
        layer = change.layer;
        setLayerValue(change.row, change.col, useNew ? change.newValue : change.oldValue);
        layer = previous;
    }

    private void refreshPreferredSize() {
        if (map == null) {
            setPreferredSize(new Dimension(640, 480));
        }
        else {
            setPreferredSize(new Dimension(map.getCols() * 16 * zoom, map.getRows() * 16 * zoom));
        }
        revalidate();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        if (map == null) {
            g2.setColor(new Color(220, 220, 220));
            g2.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
            g2.drawString("Map not loaded.", 20, 36);
            return;
        }
        int tileSize = 16 * zoom;
        for (int row = 0; row < map.getRows(); row++) {
            for (int col = 0; col < map.getCols(); col++) {
                Tile tile = map.getTile(row, col);
                if (tile != null && tile.getImage() != null) {
                    g2.drawImage(tile.getImage(), col * tileSize, row * tileSize, tileSize, tileSize, null);
                }
                int decoId = decorationLayer[row][col];
                if (decoId >= 0) {
                    Tile deco = map.getTileSet().get(decoId);
                    if (deco != null && deco.getImage() != null) {
                        g2.drawImage(deco.getImage(), col * tileSize, row * tileSize, tileSize, tileSize, null);
                    }
                }
                if (collisionLayer[row][col]) {
                    g2.setColor(new Color(220, 40, 40, 80));
                    g2.fillRect(col * tileSize, row * tileSize, tileSize, tileSize);
                }
                if (showGrid) {
                    g2.setColor(new Color(0, 0, 0, 28));
                    g2.drawRect(col * tileSize, row * tileSize, tileSize, tileSize);
                }
            }
        }
        if (tool == Tool.Rect && rectStart != null && hoverRow >= 0 && hoverCol >= 0) {
            int startCol = Math.min(rectStart.x, hoverCol);
            int endCol = Math.max(rectStart.x, hoverCol);
            int startRow = Math.min(rectStart.y, hoverRow);
            int endRow = Math.max(rectStart.y, hoverRow);
            Rectangle r = new Rectangle(startCol * tileSize, startRow * tileSize,
                    (endCol - startCol + 1) * tileSize, (endRow - startRow + 1) * tileSize);
            g2.setColor(new Color(255, 255, 255, 140));
            g2.drawRect(r.x, r.y, r.width, r.height);
        }
        if (hoverRow >= 0 && hoverCol >= 0) {
            g2.setColor(new Color(255, 215, 0, 140));
            g2.drawRect(hoverCol * tileSize, hoverRow * tileSize, tileSize, tileSize);
        }
    }
}
