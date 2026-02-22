public class RunGameWithEditor {
    public static void main(String[] args) throws Exception {
        // Start the game in a separate thread (calls Game.start())
        Class<?> gameCls = Class.forName("dq1.core.Game");
        java.lang.reflect.Method startMethod = gameCls.getMethod("start");
        Thread gameThread = new Thread(() -> {
            try {
                startMethod.invoke(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        gameThread.setDaemon(false);
        gameThread.start();

        // Wait a bit for game frame to initialize
        Thread.sleep(1500);

        // Show the editor frame (should position relative to game frame)
        Class<?> editorCls = Class.forName("dq1.editor.GameEditorFrame");
        java.lang.reflect.Method showEditor = editorCls.getMethod("showEditor");
        showEditor.invoke(null);
    }
}
