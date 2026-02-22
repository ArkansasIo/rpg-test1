Unreal 2D Toolkit Plugin (skeleton)

This folder contains a minimal Unreal Engine 5 Editor plugin skeleton called `Unreal2DToolkit`.

What is included:
- `unreal_2d_toolkit_plugin.uplugin` - plugin descriptor
- `Source/Unreal2DToolkit/Unreal2DToolkit.Build.cs` - plugin build script
- `Source/Unreal2DToolkit/Private/` and `Public/` - C++ module sources (module registration and a sample command)
- `Menus/2d_toolkit_menu.json` - machine-readable menu manifest mapping commands
- `Python/` - example editor Python scripts for batch import and atlas packing
- `EditorUtilitySpecs/` - YAML specs describing Editor Utility Widgets (to be created in-editor)

Usage
-----
1. Copy the `unreal_2d_toolkit_plugin` folder to your Unreal project `Plugins/` directory.
2. Open the project in Unreal Editor; the plugin should load. If using C++, regenerate project files and compile the project.
3. Access the Tools -> "2D Toolkit" menu to find the new commands.

Notes
-----
- This is a skeleton plugin; C++ sources are minimal and intended as templates to expand.
- The Python scripts rely on Unreal's Editor Python plugin to be enabled.
