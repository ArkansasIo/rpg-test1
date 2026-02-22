Unreal 2D Toolkit Plugin - Usage

Installation
------------
1. Copy `unreal_2d_toolkit_plugin` into your Unreal project `Plugins/` directory.
2. If your project is a C++ project, right-click the `.uproject` and choose "Generate Visual Studio project files" (or use the editor option), then build the project.
3. If it's a Blueprint-only project, open the project in the Epic Games Launcher and enable the plugin in Edit → Plugins.
4. Restart the editor.

Available features (skeleton)
-----------------------------
- Tools → 2D Toolkit menu entries (Open 2D Toolkit, Sprite Slicer, Flipbook Generator, Tile Palette, Atlas Packer)
- Python scripts in `Python/` for batch tasks (flipbook creation)
- Editor Utility Widget specs in `EditorUtilitySpecs/` to help create in-editor widgets

Extending the plugin
--------------------
- Add C++ classes under `Source/Unreal2DToolkit/Private` and expose headers in `Public`.
- Register commands and toolbar buttons in `Unreal2DToolkit.cpp` using `UToolMenus` and `FLevelEditorModule`.
- Implement custom tabs by registering `FGlobalTabmanager::Get()->RegisterNomadTabSpawner` in `StartupModule()`.

Running Python scripts
----------------------
1. Enable Editor Scripting (Python Editor Script Plugin) in Edit → Plugins.
2. Open the Python Console (Window → Developer Tools → Python Console)
3. Run the script:

```py
import batch_create_flipbooks
batch_create_flipbooks.create_flipbooks_from_selected_sprites(0.08)
```

Editor Utility Widgets
----------------------
Use the YAML specs in `EditorUtilitySpecs/` as templates when building Editor Utility Widgets in the Content Browser (Add New → Editor Utilities → Editor Utility Widget).

Build & Test
-----------
- Ensure the plugin folder `unreal_2d_toolkit_plugin` is under your project's `Plugins/` directory.
- If your project is C++, regenerate Visual Studio project files and build, or open the `.uproject` in the Editor and let it compile the plugin.
- On Windows, you can launch the Editor from command line (adjust the path to your UE5 install):

```cmd
"C:\Program Files\Epic Games\UE_5.xx\Engine\Binaries\Win64\UE5Editor.exe" "D:\Path\To\YourProject\YourProject.uproject"
```

- To build manually using the included Build.bat (replace paths with your project and engine paths):

```cmd
"C:\Program Files\Epic Games\UE_5.xx\Engine\Build\BatchFiles\Build.bat" YourProjectEditor Win64 Development "D:\Path\To\YourProject\YourProject.uproject" -waitmutex
```

Quick Menu & Tab check
----------------------
- After opening the Editor:
  - Tools -> 2D Toolkit -> Open 2D Toolkit
  - Tools -> 2D Toolkit -> Sprite Slicer
  - Tools -> 2D Toolkit -> Flipbook Generator
  - Tools -> 2D Toolkit -> Tile Palette
  - Tools -> 2D Toolkit -> Atlas Packer
- Window -> 2D Toolkit should show "Open 2D Toolkit" to restore the dashboard tab.
- Toolbar: Check the Level Editor toolbar for the "2D Toolkit" section and the buttons added.

Testing the Flipbook Python script
---------------------------------
1. Enable Editor Scripting (Python) in Edit -> Plugins.
2. Ensure the plugin `unreal_2d_toolkit_plugin` is enabled and restarted.
3. Select one or more Paper2D sprites in the Content Browser.
4. Use Tools -> 2D Toolkit -> Flipbook Generator.
5. Watch Output Log for created assets under `/Game/Flipbooks`.

Notes & Troubleshooting
-----------------------
- If `py import ...` fails, open the Python console in-editor and run `import sys; sys.path.append(r"<full_plugin_python_path>")` to include the plugin Python folder.
- If tabs don't appear, ensure the plugin is enabled and compiled. Check the editor Output Log for errors during plugin loading.