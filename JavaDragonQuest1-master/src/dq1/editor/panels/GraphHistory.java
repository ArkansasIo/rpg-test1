package dq1.editor.panels;

import java.util.*;

public class GraphHistory {
    public interface Command {
        void execute();
        void undo();
        default boolean mergeWith(Command other) { return false; }
    }

    private final Deque<Command> undoStack = new ArrayDeque<>();
    private final Deque<Command> redoStack = new ArrayDeque<>();
    private final int maxDepth;

    public GraphHistory() { this(200); }
    public GraphHistory(int maxDepth) { this.maxDepth = maxDepth; }

    public void execute(Command cmd) {
        cmd.execute();
        if (!undoStack.isEmpty()) {
            Command top = undoStack.peek();
            if (top != null && top.mergeWith(cmd)) {
                // merged into top, don't push
                redoStack.clear();
                return;
            }
        }
        undoStack.push(cmd);
        redoStack.clear();
        trim();
    }

    private void trim() {
        while (undoStack.size() > maxDepth) undoStack.removeLast();
    }

    public boolean canUndo() { return !undoStack.isEmpty(); }
    public boolean canRedo() { return !redoStack.isEmpty(); }

    public void undo() {
        if (!canUndo()) return;
        Command cmd = undoStack.pop();
        try { cmd.undo(); } catch (Exception ex) { ex.printStackTrace(); }
        redoStack.push(cmd);
    }

    public void redo() {
        if (!canRedo()) return;
        Command cmd = redoStack.pop();
        try { cmd.execute(); } catch (Exception ex) { ex.printStackTrace(); }
        undoStack.push(cmd);
    }

    public void clear() { undoStack.clear(); redoStack.clear(); }
}
