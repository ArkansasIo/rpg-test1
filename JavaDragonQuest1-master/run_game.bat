@echo off
REM Run the built-in Ant 'run' target using the bundled Ant installation in the repo.
REM Usage: double-click this file or run from cmd.exe in the repo root.
set SCRIPT_DIR=%~dp0
pushd "%SCRIPT_DIR%"
"%SCRIPT_DIR%apache-ant-1.10.14\bin\ant.bat" run
popd
pause