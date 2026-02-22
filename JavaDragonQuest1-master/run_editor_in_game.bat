@echo off
REM Usage: run_editor_in_game.bat [path_to_java]
REM If you don't provide a Java path, this uses java on PATH.
set JAVA_CMD=%1
if "%JAVA_CMD%"=="" set JAVA_CMD=java

echo Running in-game Swing editor (sets editor.inGameSwing=true)
"%JAVA_CMD%" -Deditor.inGameSwing=true -cp "build/classes" main.Main