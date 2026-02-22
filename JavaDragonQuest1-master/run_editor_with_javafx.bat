@echo off
REM Usage: run_editor_with_javafx.bat [PATH_TO_FX_LIB]
setlocal
if "%1"=="" (
  if "%JAVAFX_LIB%"=="" (
    echo Please set JAVAFX_LIB env var or pass PATH_TO_FX_LIB as argument.
    echo Example: run_editor_with_javafx.bat "C:\javafx-sdk-20\lib"
    exit /b 1
  ) else (
    set FX=%JAVAFX_LIB%
  )
) else (
  set FX=%~1
)
echo Using JavaFX lib at %FX%
set SCRIPT_DIR=%~dp0
cd /d "%SCRIPT_DIR%"n
REM compile editor module with JavaFX jars presentncall apache-ant-1.10.14\bin\ant.bat clean compile -Denv.JAVAFX_LIB=%FX%
REM run the project (will now find editor classes)
call apache-ant-1.10.14\bin\ant.bat run
endlocal
