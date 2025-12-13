@echo off
if not exist out mkdir out
javac -d out -cp "lib\libjline-3.25.1.jar" src\*.java src\ui\*.java
if %ERRORLEVEL% neq 0 (
    echo Compilation failed!
    pause
) else (
    echo Compilation successful!
)
