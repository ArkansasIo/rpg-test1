package com.javadragonquest.editor;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("JavaDragonQuest 2D Editor (JavaFX) - EditorUI");

        EditorUI editor = new EditorUI();

        // Wire basic handlers
        editor.onSave = () -> {
            showAlert("Save", "Save action triggered (stub)");
        };

        editor.onPlay = () -> {
            showAlert("Play", "Play/PIE started (stub)");
        };

        editor.onOpenLevel = (f) -> {
            showAlert("Open Level", "Requested to open: " + f.getAbsolutePath());
        };

        Scene scene = new Scene(editor.getRoot(), 1100, 720);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Keep existing helpers so other parts of the project can reuse them.
    private void runPythonScript(String scriptPath, List<String> args) {
        List<String> cmd = new ArrayList<>();
        cmd.add("python");
        cmd.add(scriptPath);
        cmd.addAll(args);
        runCmd(cmd);
    }

    private void runPythonString(String pyCmd) {
        List<String> cmd = new ArrayList<>();
        cmd.add("python");
        cmd.add("-c");
        cmd.add(pyCmd);
        runCmd(cmd);
    }

    private void runCmd(List<String> cmd) {
        try {
            ProcessBuilder pb = new ProcessBuilder(cmd);
            pb.redirectErrorStream(true);
            Process p = pb.start();
            BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            StringBuilder out = new StringBuilder();
            while ((line = in.readLine()) != null) {
                out.append(line).append("\n");
            }
            int rc = p.waitFor();
            showAlert("Command finished (rc=" + rc + ")", out.toString());
        } catch (Exception ex) {
            showAlert("Command failed", ex.getMessage());
        }
    }

    private void showAlert(String title, String body) {
        javafx.scene.control.Alert a = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(body);
        a.getDialogPane().setMinHeight(javafx.scene.layout.Region.USE_PREF_SIZE);
        a.showAndWait();
    }
}