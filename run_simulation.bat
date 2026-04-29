@echo off
REM Aura Retail OS - Simulation Runner
REM This script compiles the project and runs the SimulationRunner

echo Creating target/classes directory...
if not exist target\classes mkdir target\classes

echo.
echo Compiling Java files...
javac -d target\classes ^
    src\main\java\com\aura\event\*.java ^
    src\main\java\com\aura\failure\*.java ^
    src\main\java\com\aura\payment\*.java ^
    src\main\java\com\aura\persistence\*.java ^
    src\main\java\com\aura\verification\*.java ^
    src\main\java\com\aura\state\*.java ^
    src\main\java\com\aura\inventory\*.java ^
    src\main\java\com\aura\pricing\*.java ^
    src\main\java\com\aura\hardware\*.java ^
    src\main\java\com\aura\factory\*.java ^
    src\main\java\com\aura\command\*.java ^
    src\main\java\com\aura\registry\*.java ^
    src\main\java\com\aura\core\*.java ^
    src\main\java\com\aura\simulation\*.java

if ERRORLEVEL 1 (
    echo Compilation failed!
    pause
    exit /b 1
)

echo.
echo Running Simulation...
echo.

java -cp target\classes com.aura.simulation.SimulationRunner

echo.
echo Simulation complete. Check data/ directory for inventory.json and transactions.json
pause
