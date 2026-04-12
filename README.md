# AURA Retail OS - Subtask 2 Implementation

A pharmacy kiosk management system demonstrating two core OOP design patterns: **Strategy** (dynamic pricing) and **Command+Memento** (atomic transactions with rollback).

## Project Overview

This project demonstrates a modular pharmacy retail kiosk that can:
- Switch pricing policies at runtime (Standard, Discounted, Emergency)
- Execute atomic purchase transactions that rollback on hardware failures
- Manage inventory with reservation and snapshot capabilities

## Design Patterns Implemented

### 1. **Strategy Pattern** (Pricing)
The pricing module uses the Strategy pattern to allow runtime switching between different pricing policies:
- **StandardPricingStrategy**: Charges the full base price
- **DiscountedPricingStrategy**: Applies a percentage discount (e.g., 30% off)
- **EmergencyPricingStrategy**: Charges the base price without markup

Location: `src/main/java/com/aura/pricing/*`

### 2. **Command + Memento Pattern** (Transactions)
Purchases are implemented as first-class Command objects that can be executed and undone. The Memento pattern (InventorySnapshot) captures inventory state before a purchase, enabling atomic rollback if hardware fails.

- **PurchaseItemCommand**: Executes a purchase with automatic rollback on failure
- **InventorySnapshot (Memento)**: Captures the inventory state before operations
- **InventoryManager**: Manages reservations and snapshot restoration

Location: `src/main/java/com/aura/command/*` and `src/main/java/com/aura/inventory/*`

## Project Structure

```
src/
├── main/java/com/aura/
│   ├── core/              # KioskInterface, KioskController
│   ├── inventory/         # Product, InventoryManager, InventorySnapshot
│   ├── pricing/           # PricingStrategy implementations
│   ├── command/           # Command interface and implementations
│   ├── hardware/          # HardwareController
│   ├── registry/          # CentralRegistry (Singleton)
│   ├── persistence/       # PersistenceService (Gson-based JSON)
│   └── simulation/        # SimulationRunner
└── test/java/com/aura/
    ├── PricingStrategyTest
    └── CommandRollbackTest
```

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
- **Scenario B**: Runtime pricing strategy switch (Standard → Emergency → Discounted)
- **Scenario C**: Atomic rollback when hardware fails

## Build Details

- **Language**: Java 17
- **Build Tool**: Maven
- **Testing**: JUnit 5

## Dependencies

```xml
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter</artifactId>
    <version>5.10.0</version>
    <scope>test</scope>
</dependency>
```

