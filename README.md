# AURA Retail OS

A pharmacy kiosk management system demonstrating Strategy and Command patterns with atomic transactions.

## How to Build

```bash
mvn clean compile
```

## How to Run Tests

Run all tests:
```bash
mvn test
```

Expected output:
- ✓ 4 PricingStrategyTest cases
- ✓ 3 CommandRollbackTest cases

## How to Run Simulation

Create a script `run_simulation.sh` (or `run_simulation.bat` on Windows):

**Windows:**
```batch
@echo off
cd /d %~dp0
mvn compile dependency:build-classpath -Dmdep.outputFile=classpath
for /f %%i in (classpath) do set CLASSPATH=%%i;target/classes
java -cp "%CLASSPATH%" com.aura.simulation.SimulationRunner
pause
```

Or manually:
```bash
mvn compile exec:java@run-sim
```

**Unix/Linux/Mac:**
```bash
#!/bin/bash
mvn compile dependency:build-classpath -Dmdep.outputFile=classpath
CLASSPATH=$(cat classpath):target/classes
java -cp $CLASSPATH com.aura.simulation.SimulationRunner
```

The simulation will run three scenarios:
- **Scenario A**: Normal purchase with standard pricing
- **Scenario B**: Runtime pricing strategy switch
- **Scenario C**: Atomic rollback when hardware fails

