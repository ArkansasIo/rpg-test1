Blueprint editor prototype

This folder contains a tiny prototype of a blueprint/node-graph editor for Java Swing.

Files:
- Pin.java, Node.java, Link.java - core model elements
- BlueprintGraph.java - simple container and link validation
- GraphSerializer.java - save/load using Java serialization
- BlueprintRuntime.java - naive runtime to execute registered node implementations
- GraphEditorPanel.java - minimal Swing panel to visualize nodes and links and drag nodes
- GraphEditorTool.java - toolbar with buttons to add nodes, create an example link, save and load

How to run:
- The project uses the repo's existing build. A small test launcher (BlueprintEditorTest) can start the UI.
- This is a prototype: extend with topological sorting, type coercions, custom node editors, undo/redo, copy/paste, and better serialization (JSON).
