JavaDragonQuest 2D Editor (JavaFX)

This lightweight JavaFX application is a standalone editor inspired by the Unreal Editor 2D toolkit. It provides menus, tabs, and a simple Flipbook generator that can invoke the plugin Python script included in the project.

Prerequisites
- JDK 20 or later (matching JavaFX 20 used here)
- Gradle (if you want to run via gradle wrapper or use your system Gradle)
- Python installed and on PATH to run Python scripts invoked by the editor

Build & run (Windows)
1. Open a cmd in this folder: `D:\game java\JavaDragonQuest1-master\editor-java`
2. Run:

```cmd
gradle run
```

If you don't have Gradle installed, use the Gradle wrapper or your IDE to import the Gradle project.

Usage
- Menus: Tools -> 2D Toolkit contains entries to open tabs.
- Flipbook Generator tab: enter a frame time and click "Create Flipbooks from Selected Sprites". This runs the Python command:

```py
import batch_create_flipbooks
batch_create_flipbooks.create_flipbooks_from_selected_sprites(0.08)
```

- You can also select Run Python Script... from the Tools menu to run any Python script on your system.

Notes
- This is a standalone JavaFX app and not the Unreal Editor. It is a bridge/editor that mimics the UE menu layout and can call the Python helper scripts in the plugin. The actual plugin must still be compiled and installed in Unreal if you want UE integration.
- The editor runs system Python. Ensure the `batch_create_flipbooks.py` module is discoverable by Python (add the plugin's Python path to PYTHONPATH or run the script by absolute path).

Next steps (optional)
- Implement full UIs for Sprite Slicer, Tile Palette and Atlas Packer.
- Add project-specific logic or integrate a local asset browser to operate on game assets directly.
