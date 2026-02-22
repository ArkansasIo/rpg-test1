package com.javadragonquest.editor;

import com.javadragonquest.editor.model.Actor;
import com.javadragonquest.editor.model.LevelIO;
import com.javadragonquest.editor.model.SceneModel;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * EditorUI - a JavaFX-based Unreal-Editor-like layout.
 */
public class EditorUI {

    private final BorderPane root;
    private final SceneModel model = new SceneModel();

    // UI controls we mutate
    private TreeView<String> outlinerTree;
    private Canvas viewportCanvas;
    private Label statusLabel;
    private ListView<String> assetListView;

    // details controls
    private TextField nameField;
    private TextField locField;
    private TextField rotField;
    private TextField scaleField;

    // selection and viewport state
    private Actor selectedActor = null;
    private double canvasOffsetX = 0;
    private double canvasOffsetY = 0;
    private double canvasScale = 1.0;
    private boolean draggingActor = false;
    private double lastMouseX, lastMouseY;

    // Public hooks for actions (can be replaced or used by caller)
    public Consumer<File> onOpenLevel = (f) -> {};
    public Runnable onSave = () -> {};
    public Runnable onPlay = () -> {};

    public EditorUI() {
        root = new BorderPane();
        buildUI();
    }

    public BorderPane getRoot() {
        return root;
    }

    private void buildUI() {
        MenuBar menuBar = buildMenuBar();
        ToolBar toolBar = buildToolBar();

        VBox topBox = new VBox(menuBar, toolBar);

        // Center area: left Outliner, center Viewport, right Details
        SplitPane centerSplit = new SplitPane();
        centerSplit.setOrientation(Orientation.HORIZONTAL);

        Node outliner = buildOutliner();
        Node viewport = buildViewport();
        Node details = buildDetails();

        centerSplit.getItems().addAll(outliner, viewport, details);
        centerSplit.setDividerPositions(0.18, 0.78);

        // Bottom: Asset Browser
        TitledPane assetBrowser = buildAssetBrowser();

        SplitPane verticalSplit = new SplitPane();
        verticalSplit.setOrientation(Orientation.VERTICAL);
        verticalSplit.getItems().addAll(centerSplit, assetBrowser);
        verticalSplit.setDividerPositions(0.82);

        root.setTop(topBox);
        root.setCenter(verticalSplit);
        root.setBottom(buildStatusBar());

        // initial paint
        redrawViewport();

        // scan assets/res automatically (best-effort)
        scanAssets();
    }

    private MenuBar buildMenuBar() {
        MenuBar menuBar = new MenuBar();

        Menu file = new Menu("File");
        MenuItem newLevel = new MenuItem("New Level...");
        MenuItem openLevel = new MenuItem("Open Level...");
        MenuItem save = new MenuItem("Save");
        MenuItem exit = new MenuItem("Exit");
        file.getItems().addAll(newLevel, openLevel, save, new SeparatorMenuItem(), exit);

        newLevel.setOnAction(e -> {
            model.getActors().clear();
            model.setName("Untitled");
            refreshOutliner();
            redrawViewport();
            setStatus("New level created");
        });

        openLevel.setOnAction(e -> {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Open Level");
            File f = null;
            if (root.getScene() != null && root.getScene().getWindow() != null) {
                f = chooser.showOpenDialog(root.getScene().getWindow());
            } else {
                f = chooser.showOpenDialog(null);
            }
            if (f != null) {
                try {
                    SceneModel loaded = LevelIO.load(f);
                    model.getActors().clear();
                    model.getActors().addAll(loaded.getActors());
                    model.setName(loaded.getName());
                    refreshOutliner();
                    redrawViewport();
                    setStatus("Loaded: " + f.getName());
                } catch (Exception ex) {
                    showAlert("Error", "Failed to load: " + ex.getMessage(), ex);
                }
            }
        });

        save.setOnAction(e -> {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Save Level");
            File f = null;
            if (root.getScene() != null && root.getScene().getWindow() != null) {
                f = chooser.showSaveDialog(root.getScene().getWindow());
            } else {
                f = chooser.showSaveDialog(null);
            }
            if (f != null) {
                try {
                    LevelIO.save(model, f);
                    setStatus("Saved: " + f.getName());
                } catch (Exception ex) {
                    showAlert("Error", "Failed to save: " + ex.getMessage(), ex);
                }
            }
        });

        exit.setOnAction(e -> {
            if (root.getScene() != null && root.getScene().getWindow() != null)
                root.getScene().getWindow().hide();
        });

        Menu edit = new Menu("Edit");
        edit.getItems().addAll(new MenuItem("Undo"), new MenuItem("Redo"), new SeparatorMenuItem(), new MenuItem("Cut"), new MenuItem("Copy"), new MenuItem("Paste"));

        Menu view = new Menu("View");
        CheckMenuItem showStats = new CheckMenuItem("Show Stats");
        view.getItems().add(showStats);

        Menu window = new Menu("Window");
        MenuItem openOutliner = new MenuItem("Open Outliner");
        MenuItem openContent = new MenuItem("Open Content Browser");
        window.getItems().addAll(openOutliner, openContent);

        Menu help = new Menu("Help");
        help.getItems().add(new MenuItem("About"));

        menuBar.getMenus().addAll(file, edit, view, window, help);

        // Keyboard accelerators
        save.setAccelerator(KeyCombination.keyCombination("CTRL+S"));

        // Wire simple handlers
        openOutliner.setOnAction(e -> showOutlinerPopup());
        openContent.setOnAction(e -> showAssetBrowserPopup());

        return menuBar;
    }

    private ToolBar buildToolBar() {
        ToolBar tb = new ToolBar();
        Button btnSelect = new Button(null, new ImageView());
        btnSelect.setText("Select");
        Button btnMove = new Button("Move");
        Button btnRotate = new Button("Rotate");
        Button btnScale = new Button("Scale");
        Button btnPlay = new Button("Play");

        btnPlay.setOnAction(e -> onPlay.run());

        tb.getItems().addAll(btnSelect, btnMove, btnRotate, btnScale, new Separator(), btnPlay);
        return tb;
    }

    private Node buildOutliner() {
        VBox box = new VBox();
        box.setPadding(new Insets(6));
        TitledPane tp = new TitledPane();
        tp.setText("Outliner");
        TreeItem<String> rootItem = new TreeItem<>("Level");
        rootItem.setExpanded(true);
        outlinerTree = new TreeView<>(rootItem);
        refreshOutliner();
        outlinerTree.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            // selection handler (populate details)
            if (newV != null && newV.getParent() != null) {
                String name = newV.getValue();
                // find actor
                model.getActors().stream().filter(a -> a.getName().equals(name)).findFirst().ifPresent(a -> {
                    selectActor(a);
                });
            }
        });
        tp.setContent(outlinerTree);
        tp.setExpanded(true);
        box.getChildren().add(tp);
        VBox.setVgrow(tp, Priority.ALWAYS);
        return box;
    }

    private Node buildViewport() {
        VBox box = new VBox();
        box.setPadding(new Insets(6));
        TitledPane tp = new TitledPane();
        tp.setText("Viewport");

        viewportCanvas = new Canvas(800, 600);

        // place canvas into a resizable holder so it updates with layout changes
        StackPane canvasHolder = new StackPane(viewportCanvas);
        canvasHolder.setPrefSize(800, 600);
        // bind canvas size to holder so it resizes with window
        viewportCanvas.widthProperty().bind(canvasHolder.widthProperty());
        viewportCanvas.heightProperty().bind(canvasHolder.heightProperty());

        // mouse pressed: detect actor hit for dragging or start pan
        viewportCanvas.addEventHandler(MouseEvent.MOUSE_PRESSED, e -> {
            lastMouseX = e.getX();
            lastMouseY = e.getY();
            // transform to world coords
            double wx = (e.getX() - canvasOffsetX) / canvasScale;
            double wy = (e.getY() - canvasOffsetY) / canvasScale;
            Actor hit = findActorAt((float) wx, (float) wy);
            if (e.getButton() == MouseButton.PRIMARY) {
                if (hit != null) {
                    selectActor(hit);
                    draggingActor = true;
                } else {
                    // click empty: deselect
                    selectActor(null);
                }
            } else if (e.getButton() == MouseButton.MIDDLE) {
                // start panning
            }
            redrawViewport();
        });

        viewportCanvas.addEventHandler(MouseEvent.MOUSE_DRAGGED, e -> {
            double dx = e.getX() - lastMouseX;
            double dy = e.getY() - lastMouseY;
            if (draggingActor && selectedActor != null) {
                // move actor in world coords
                double wx = (e.getX() - canvasOffsetX) / canvasScale;
                double wy = (e.getY() - canvasOffsetY) / canvasScale;
                selectedActor.setX((float) wx);
                selectedActor.setY((float) wy);
                updateDetailsFromActor(selectedActor);
                refreshOutliner();
            } else if (e.isMiddleButtonDown()) {
                // pan
                canvasOffsetX += dx;
                canvasOffsetY += dy;
            }
            lastMouseX = e.getX();
            lastMouseY = e.getY();
            redrawViewport();
        });

        viewportCanvas.addEventHandler(MouseEvent.MOUSE_RELEASED, e -> {
            draggingActor = false;
        });

        viewportCanvas.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            if (e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 2) {
                // double click empty space: create actor
                double wx = (e.getX() - canvasOffsetX) / canvasScale;
                double wy = (e.getY() - canvasOffsetY) / canvasScale;
                Actor a = new Actor(UUID.randomUUID().toString(), "Actor_" + (model.getActors().size() + 1), (float) wx, (float) wy);
                model.addActor(a);
                refreshOutliner();
                selectActor(a);
                setStatus("Placed: " + a.getName());
                redrawViewport();
            }
        });

        // zoom support
        viewportCanvas.addEventHandler(ScrollEvent.SCROLL, e -> {
            double factor = Math.exp(e.getDeltaY() * 0.001);
            double oldScale = canvasScale;
            canvasScale *= factor;
            // clamp
            canvasScale = Math.max(0.2, Math.min(4.0, canvasScale));
            // adjust offset so zoom focuses on mouse position
            double mx = e.getX();
            double my = e.getY();
            canvasOffsetX = mx - (mx - canvasOffsetX) * (canvasScale / oldScale);
            canvasOffsetY = my - (my - canvasOffsetY) * (canvasScale / oldScale);
            redrawViewport();
            e.consume();
        });

        tp.setContent(canvasHolder);
        tp.setExpanded(true);
        box.getChildren().add(tp);
        VBox.setVgrow(tp, Priority.ALWAYS);
        return box;
    }

    private Node buildDetails() {
        VBox box = new VBox();
        box.setPadding(new Insets(6));
        TitledPane tp = new TitledPane();
        tp.setText("Details");
        GridPane grid = new GridPane();
        grid.setHgap(6);
        grid.setVgap(6);

        nameField = new TextField();
        locField = new TextField();
        rotField = new TextField();
        scaleField = new TextField();

        grid.addRow(0, new Label("Name:"), nameField);
        grid.addRow(1, new Label("Location (x,y):"), locField);
        grid.addRow(2, new Label("Rotation:"), rotField);
        grid.addRow(3, new Label("Scale:"), scaleField);

        ChangeListener<String> detailListener = (obs, oldV, newV) -> {
            if (selectedActor != null) {
                applyDetailsToActor();
                redrawViewport();
                refreshOutliner();
            }
        };

        nameField.textProperty().addListener(detailListener);
        locField.textProperty().addListener(detailListener);
        rotField.textProperty().addListener(detailListener);
        scaleField.textProperty().addListener(detailListener);

        tp.setContent(grid);
        tp.setExpanded(true);
        box.getChildren().add(tp);
        VBox.setVgrow(tp, Priority.ALWAYS);
        return box;
    }

    private TitledPane buildAssetBrowser() {
        TitledPane tp = new TitledPane();
        tp.setText("Asset Browser");
        assetListView = new ListView<>();
        assetListView.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                String sel = assetListView.getSelectionModel().getSelectedItem();
                if (sel != null) {
                    // sel may be full path or display name; we'll create an actor using path
                    Actor a = new Actor(UUID.randomUUID().toString(), "Actor_" + (model.getActors().size() + 1), 100, 100, sel);
                    model.addActor(a);
                    refreshOutliner();
                    selectActor(a);
                    setStatus("Imported asset as actor: " + sel);
                    redrawViewport();
                }
            }
        });
        tp.setContent(assetListView);
        tp.setCollapsible(false);
        return tp;
    }

    private Node buildStatusBar() {
        HBox bar = new HBox();
        bar.setPadding(new Insets(4));
        bar.setSpacing(8);
        statusLabel = new Label("Ready");
        Label coords = new Label("Pos: 0,0");
        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        bar.getChildren().addAll(statusLabel, spacer, coords);
        return bar;
    }

    private void refreshOutliner() {
        TreeItem<String> rootItem = new TreeItem<>(model.getName() == null ? "Level" : model.getName());
        rootItem.setExpanded(true);
        for (Actor a : model.getActors()) {
            rootItem.getChildren().add(new TreeItem<>(a.getName()));
        }
        outlinerTree.setRoot(rootItem);
    }

    private void redrawViewport() {
        if (viewportCanvas == null) return;
        GraphicsContext g = viewportCanvas.getGraphicsContext2D();
        // clear with checker-like background
        g.setFill(Color.gray(0.85));
        g.fillRect(0, 0, viewportCanvas.getWidth(), viewportCanvas.getHeight());

        g.save();
        g.translate(canvasOffsetX, canvasOffsetY);
        g.scale(canvasScale, canvasScale);

        // draw grid
        g.setStroke(Color.LIGHTGRAY);
        for (int x = -2000; x <= 2000; x += 64) {
            g.strokeLine(x, -2000, x, 2000);
        }
        for (int y = -2000; y <= 2000; y += 64) {
            g.strokeLine(-2000, y, 2000, y);
        }

        // draw actors
        for (Actor a : model.getActors()) {
            boolean isSel = (a == selectedActor);
            g.setFill(isSel ? Color.ORANGE : Color.DARKBLUE);
            g.fillOval(a.getX() - 12, a.getY() - 12, 24, 24);
            g.setFill(Color.WHITE);
            g.fillText(a.getName(), a.getX() + 14, a.getY() + 6);
        }

        g.restore();
    }

    private void setStatus(String s) {
        statusLabel.setText(s);
    }

    private Actor findActorAt(float wx, float wy) {
        // simple hit test in reverse order
        List<Actor> list = model.getActors();
        for (int i = list.size() - 1; i >= 0; i--) {
            Actor a = list.get(i);
            double dx = wx - a.getX();
            double dy = wy - a.getY();
            if (dx * dx + dy * dy <= 16 * 16) return a;
        }
        return null;
    }

    private void selectActor(Actor a) {
        selectedActor = a;
        if (a != null) {
            nameField.setText(a.getName());
            locField.setText(String.format("%.1f,%.1f", a.getX(), a.getY()));
            rotField.setText(String.format("%.1f", a.getRotation()));
            scaleField.setText(String.format("%.2f", a.getScale()));
            setStatus("Selected: " + a.getName());
        } else {
            nameField.setText("");
            locField.setText("");
            rotField.setText("");
            scaleField.setText("");
            setStatus("Ready");
        }
        redrawViewport();
    }

    private void updateDetailsFromActor(Actor a) {
        if (a == null) return;
        nameField.setText(a.getName());
        locField.setText(String.format("%.1f,%.1f", a.getX(), a.getY()));
        rotField.setText(String.format("%.1f", a.getRotation()));
        scaleField.setText(String.format("%.2f", a.getScale()));
    }

    private void applyDetailsToActor() {
        if (selectedActor == null) return;
        selectedActor.setName(nameField.getText());
        try {
            String[] parts = locField.getText().split(",");
            if (parts.length >= 2) {
                float x = Float.parseFloat(parts[0].trim());
                float y = Float.parseFloat(parts[1].trim());
                selectedActor.setX(x);
                selectedActor.setY(y);
            }
        } catch (Exception ex) {
            // ignore parse errors
        }
        try {
            selectedActor.setRotation(Float.parseFloat(rotField.getText()));
        } catch (Exception ex) {}
        try {
            selectedActor.setScale(Float.parseFloat(scaleField.getText()));
        } catch (Exception ex) {}
        setStatus("Updated: " + selectedActor.getName());
    }

    private void scanAssets() {
        // Try to find assets/res under the repository root; best-effort
        try {
            Path repoRoot = new File(".").toPath().toRealPath();
            // Common paths in this workspace
            Path candidate = repoRoot.resolve("assets/res");
            if (!Files.exists(candidate)) {
                // try parent folder (if available)
                Path parent = repoRoot.getParent();
                if (parent != null) {
                    candidate = parent.resolve("assets/res");
                }
            }
            if (candidate != null && Files.exists(candidate) && Files.isDirectory(candidate)) {
                List<String> files = Files.walk(candidate, 2)
                        .filter(Files::isRegularFile)
                        .map(Path::toString)
                        .collect(Collectors.toList());
                assetListView.getItems().clear();
                assetListView.getItems().addAll(files);
                setStatus("Scanned assets: " + files.size());
                return;
            }
        } catch (Exception ex) {
            showAlert("Asset scan error", "Failed to scan assets: " + ex.getMessage(), ex);
        }
        // fallback sample items
        if (assetListView != null) {
            assetListView.getItems().setAll("Texture: grass.png", "Sprite: hero_idle.png", "Sound: sfx_jump.wav", "Tileset: dungeon_tiles.png");
        }
        setStatus("Asset scan fallback used");
    }

    // Simple popups
    private void showOutlinerPopup() {
        Dialog<Void> d = new Dialog<>();
        d.setTitle("Outliner");
        d.getDialogPane().setContent(new Label("Outliner window (detached)"));
        d.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        d.showAndWait();
    }

    private void showAssetBrowserPopup() {
        Dialog<Void> d = new Dialog<>();
        d.setTitle("Content Browser");
        d.getDialogPane().setContent(new Label("Content Browser (detached)"));
        d.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        d.showAndWait();
    }

    private void showAlert(String title, String body, Throwable ex) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle(title);
        a.setHeaderText(null);
        String content = body;
        if (ex != null) {
            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            content += "\n\n" + sw.toString();
        }
        a.setContentText(content);
        a.getDialogPane().setMinHeight(javafx.scene.layout.Region.USE_PREF_SIZE);
        a.showAndWait();
    }

    private void showAlert(String title, String body) {
        showAlert(title, body, null);
    }
}
