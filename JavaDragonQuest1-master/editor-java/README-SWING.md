ereSwing editor fallback

A minimal Swing-based editor entrypoint was added at:

  src/main/java/com/javadragonquest/editor/SwingEditorMain.java

Usage:
- To prefer the Swing editor when launching `main.Main`, start the JVM with the system property `-Deditor.swing=true`.
  Example (Windows cmd.exe):

  java -Deditor.swing=true -cp build/classes main.Main

- The Swing editor can also be launched directly (if classes are compiled):

  java -cp build/classes com.javadragonquest.editor.SwingEditorMain

Notes:
- The Swing editor is a lightweight fallback and does not implement full editor features. It is intended to appear after the project's splash screen and be usable as a simple editor UI.
- If you want the Swing editor always available at build time, ensure the `editor-java` module is built so the class is present on the runtime classpath.
