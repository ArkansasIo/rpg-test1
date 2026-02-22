package com.javadragonquest.editor;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Main extends Application {

    private static final double SPLASH_TIME_SECONDS = 3.0;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        // Show JavaFX splash with boot logo and progress bar
        Stage splashStage = new Stage();
        splashStage.setTitle("JavaDragonQuest Editor - Booting");

        ImageView logoView = new ImageView();
        Image logo = loadLogo();
        if (logo != null) {
            logoView.setImage(logo);
            logoView.setPreserveRatio(true);
            logoView.setFitWidth(380);
        } else {
            Label placeholder = new Label("JavaDragonQuest Editor");
            placeholder.setStyle("-fx-font-size:20px; -fx-font-weight:bold;");
            logoView.setGraphic(placeholder);
        }

        ProgressBar progressBar = new ProgressBar(0);
        progressBar.setPrefWidth(380);

        Label status = new Label("Loading editor...");

        VBox splashBox = new VBox(12, logoView, progressBar, status);
        splashBox.setPadding(new Insets(12));
        splashBox.setAlignment(Pos.CENTER);

        Scene splashScene = new Scene(new StackPane(splashBox), 420, 260);
        splashStage.setScene(splashScene);
        splashStage.show();

        // Simulate loading with Timeline; update progress and status
        Timeline timeline = new Timeline();
        final int steps = 60; // update 60 times over SPLASH_TIME_SECONDS
        for (int i = 1; i <= steps; i++) {
            double t = i / (double) steps;
            timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(t * SPLASH_TIME_SECONDS), ev -> {
                progressBar.setProgress(t);
                status.setText(String.format("Loading... %d%%", (int) (t * 100)));
            }));
        }
        timeline.setOnFinished(ev -> {
            // When loading finished, show title screen and wait for user to continue
            splashStage.close();
            showTitleScreen(primaryStage, logo);
        });
        timeline.play();
    }

    private void showTitleScreen(Stage stage, Image logo) {
        stage.setTitle("JavaDragonQuest Editor");

        ImageView logoView = new ImageView();
        if (logo != null) {
            logoView.setImage(logo);
            logoView.setPreserveRatio(true);
            logoView.setFitWidth(480);
        }

        Label title = new Label("JavaDragonQuest Editor");
        title.setStyle("-fx-font-size:28px; -fx-font-weight:bold;");

        Label subtitle = new Label("A 2D RPG editor for JavaDragonQuest");
        subtitle.setStyle("-fx-font-size:12px; -fx-opacity:0.8;");

        Button btnContinue = new Button("Enter Editor");
        btnContinue.setOnAction(e -> {
            // Load main editor UI
            EditorUI editor = new EditorUI();
            // Wire basic handlers
            editor.onSave = () -> showAlert("Save", "Save action triggered (stub)");
            editor.onPlay = () -> showAlert("Play", "Play/PIE started (stub)");
            editor.onOpenLevel = (f) -> showAlert("Open Level", "Requested to open: " + f.getAbsolutePath());

            Scene scene = new Scene(editor.getRoot(), 1100, 720);
            stage.setScene(scene);
            stage.show();
        });

        VBox root = new VBox(16);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));
        if (logo != null) root.getChildren().add(logoView);
        root.getChildren().addAll(title, subtitle, btnContinue);

        Scene scene = new Scene(new StackPane(root), 800, 520);
        stage.setScene(scene);
        stage.show();
    }

    private Image loadLogo() {
        try {
            // Try several common locations (classpath, editor module assets, repo assets)
            // 1) classpath resource
            try {
                Image img = new Image(getClass().getResourceAsStream("/boot_logo.png"));
                if (img.getWidth() > 0) return img;
            } catch (Exception ignored) {}

            // 2) editor module assets
            File f1 = new File("editor-java/assets/res/boot_logo.png");
            if (f1.exists()) return new Image(f1.toURI().toString());

            // 3) repo-level assets
            File f2 = new File("assets/res/boot_logo.png");
            if (f2.exists()) return new Image(f2.toURI().toString());

            // 4) fallback to world.png if present
            File f3 = new File("world.png");
            if (f3.exists()) return new Image(f3.toURI().toString());

        } catch (Exception ex) {
            // ignore and return null
        }
        return null;
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
