@echo off
REM Aura Retail OS - Simulation Runner
REM This script compiles the project and runs the SimulationRunner with proper classpath

echo Compiling project...
mvn clean compile
if ERRORLEVEL 1 (
    echo Compilation failed!
    pause
    exit /b 1
)

echo.
echo Building classpath with dependencies...
mvn dependency:build-classpath -Dmdep.outputFile=classpath.txt > nul 2>&1

echo.
echo Running Simulation...
echo.

REM Read classpath from file and run the simulation
setlocal enabledelayedexpansion
for /f %%i in (classpath.txt) do set CLASSPATH=%%i
set CLASSPATH=!CLASSPATH!;target/classes

java -cp "!CLASSPATH!" com.aura.simulation.SimulationRunner

echo.
echo Simulation complete. Check data/ directory for inventory.json and transactions.json
pause
