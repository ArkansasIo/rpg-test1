package main;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Main class.
 * 
 * Launches the Editor as the primary UI and provides access to start the game.
 */
public class Main {

    public static void main(String[] args) {
        // Show splash while editor initializes
        Splash.show("JavaDragonQuest Editor");

        // Try to launch the JavaFX editor main via reflection (no compile-time dependency)
        new Thread(() -> {
            try {
                // If system property editor.inGameSwing=true is set, launch the in-game Swing editor
                String inGameSwing = System.getProperty("editor.inGameSwing", "false");
                if ("true".equalsIgnoreCase(inGameSwing)) {
                    try {
                        Class<?> gameEditor = Class.forName("dq1.editor.GameEditorFrame");
                        java.lang.reflect.Method m = gameEditor.getMethod("showEditor");
                        m.invoke(null);
                        // hide splash once editor is requested
                        Splash.hide();
                        return;
                    } catch (ClassNotFoundException cnfe) {
                        System.out.println("In-game Swing editor not found on classpath; continuing to try other editors.");
                    }
                }

                // If system property editor.swing=true is set, prefer the Swing editor
                String preferSwing = System.getProperty("editor.swing", "false");
                if ("true".equalsIgnoreCase(preferSwing)) {
                    try {
                        Class<?> swingMain = Class.forName("com.javadragonquest.editor.SwingEditorMain");
                        java.lang.reflect.Method m = swingMain.getMethod("main", String[].class);
                        m.invoke(null, (Object) args);
                        return;
                    } catch (ClassNotFoundException cnfe) {
                        System.out.println("Swing editor not found on classpath; continuing to try JavaFX editor.");
                    }
                }

                try {
                    Class<?> editorMain = Class.forName("com.javadragonquest.editor.Main");
                    java.lang.reflect.Method m = editorMain.getMethod("main", String[].class);
                    m.invoke(null, (Object) args);
                    return;
                } catch (ClassNotFoundException cnfe) {
                    // Editor not present on classpath; fall back to running the game directly
                    System.out.println("Editor not found on classpath; starting game directly.");
                }

                // If JavaFX editor wasn't available, try Swing editor as a fallback
                try {
                    Class<?> swingMain = Class.forName("com.javadragonquest.editor.SwingEditorMain");
                    java.lang.reflect.Method m = swingMain.getMethod("main", String[].class);
                    m.invoke(null, (Object) args);
                    return;
                } catch (ClassNotFoundException cnfe) {
                    // no swing editor either; proceed to game
                    System.out.println("No Swing editor on classpath; starting legacy game UI.");
                }

                // Fallback: start the original game window (Swing)
                // We keep the old behavior here in case editor module isn't available.
                // Launch Swing game frame (game initialization should be done on EDT)
                javax.swing.SwingUtilities.invokeLater(() -> {
                    try {
                        dq1.core.View.getCanvas().setBackground(java.awt.Color.BLACK);
                        dq1.core.View.getCanvas().setPreferredSize(
                                new java.awt.Dimension(dq1.core.Settings.VIEWPORT_WIDTH, dq1.core.Settings.VIEWPORT_HEIGHT));

                        javax.swing.JFrame frame = new javax.swing.JFrame();
                        frame.setIconImage(dq1.core.Resource.getImage("dq1"));
                        frame.setTitle(dq1.core.Settings.GAME_TITLE + " (" + dq1.core.Settings.GAME_VERSION + ")");
                        frame.getContentPane().add(dq1.core.View.getCanvas());
                        frame.setResizable(false);
                        frame.pack();
                        frame.setLocationRelativeTo(null);
                        frame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
                        frame.setVisible(true);

                        dq1.core.View.start();
                        dq1.core.View.getCanvas().requestFocus();
                        dq1.core.Game.start();
                    } catch (Exception ex) {
                        Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                    } finally {
                        Splash.hide();
                    }
                });

            } catch (Exception ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                Splash.hide();
            }
        }).start();
    }

}