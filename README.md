# AURA Retail OS

A pharmacy kiosk management system demonstrating Strategy and Command patterns with atomic transactions.

## Prerequisites

Before running this project, ensure you have the following installed:

- **Java Development Kit (JDK)**: Version 17 or higher
  - Check: `java -version`
  - Download: [Oracle JDK](https://www.oracle.com/java/technologies/downloads/) or [OpenJDK](https://openjdk.org/)

## How to Run Simulation

This project includes pre-configured scripts to easily run the simulation.

### Using the Provided Script (Recommended)

**On Windows:**
```bash
run_simulation.bat
```

**On Unix/Linux/Mac:**
```bash
./run_simula

These scripts will:
1. Compile all Java files using `javac`
2. Execute the simulation runner
3. Display results and pause for review

### What the Simulation Demonstrates

The simulation runs three scenarios:
- **Scenario A**: Normal purchase with standard pricing
- **Scenario B**: Runtime pricing strategy switch
- **Scenario C**: Atomic rollback when hardware fails

Output files are generated in the `data/` directory:
- `inventory.json`: Final inventory state
- `transactions.json`: Transaction history

