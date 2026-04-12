#!/bin/bash
# Aura Retail OS - Simulation Runner
# This script compiles the project and runs the SimulationRunner with proper classpath

echo "Compiling project..."
mvn clean compile
if [ $? -ne 0 ]; then
    echo "Compilation failed!"
    exit 1
fi

echo ""
echo "Building classpath with dependencies..."
mvn dependency:build-classpath -Dmdep.outputFile=classpath.txt > /dev/null 2>&1

echo ""
echo "Running Simulation..."
echo ""

# Read classpath from file and run the simulation
CLASSPATH=$(cat classpath.txt):target/classes
java -cp "$CLASSPATH" com.aura.simulation.SimulationRunner

echo ""
echo "Simulation complete. Check data/ directory for inventory.json and transactions.json"
