# AURA Retail OS

A comprehensive retail kiosk management system for multi-type dispensing operations (pharmacy, food, emergency relief). Demonstrates advanced OOP patterns including **Factory**, **Strategy**, **Command**, **Observer**, **State**, and **Chain of Responsibility** patterns.

## Table of Contents
- [Features](#features)
- [Architecture](#architecture)
- [Project Structure](#project-structure)
- [Prerequisites](#prerequisites)
- [Running the Demo](#running-the-demo)
- [Code Documentation](#code-documentation)

## Features

**Multi-Kiosk Support**: Factory pattern for creating specialized kiosks (Pharmacy, Food, Emergency Relief)  
**Dynamic Pricing**: Strategy pattern for different pricing models (Standard, Discounted, Emergency)  
**Event-Driven Architecture**: Observer pattern with event publishing and subscription  
**Failure Recovery**: Chain of Responsibility pattern for handling hardware failures  
**State Management**: State pattern for kiosk operational modes  
**Transaction Safety**: Atomic transactions with automatic rollback on failure  
**Concurrent Operations**: Thread-safe inventory and transaction handling  
**Persistence**: JSON-based data persistence for inventory and transactions  

## Architecture

### Design Patterns Used

| Pattern | Use Case |
|---------|----------|
| **Factory Pattern** | Create different kiosk types (PharmacyKioskFactory, FoodKioskFactory, EmergencyReliefKioskFactory) |
| **Strategy Pattern** | Dynamic pricing strategies (StandardPricing, DiscountedPricing, EmergencyPricing) |
| **Command Pattern** | Transactional operations (PurchaseItemCommand, RefundCommand, RestockCommand) |
| **Observer Pattern** | Event publishing and subscription (EventBus, EventListener, various Event types) |
| **State Pattern** | Kiosk operational states (ActiveState, MaintenanceState, PowerSavingState, EmergencyLockdownState) |
| **Chain of Responsibility** | Failure handling (RetryHandler, RecalibrationHandler, TechnicianAlertHandler) |

## Project Structure

```
aura/
├── src/main/java/com/aura/
│   ├── command/           # Command pattern - transactional operations
│   │   ├── Command.java                    (interface)
│   │   ├── PurchaseItemCommand.java        (handle purchases with all validations)
│   │   ├── RefundCommand.java              (process refunds)
│   │   └── RestockCommand.java             (manage inventory restocking)
│   │
│   ├── core/              # Main kiosk controller
│   │   ├── KioskController.java            (orchestrates all kiosk operations)
│   │   └── KioskInterface.java             (defines kiosk contract)
│   │
│   ├── event/             # Observer pattern - event system
│   │   ├── EventBus.java                   (central event publishing hub)
│   │   ├── EventListener.java              (interface for subscribers)
│   │   ├── SystemEvent.java                (base event class)
│   │   ├── LowStockEvent.java              (triggered when stock is low)
│   │   ├── TransactionCompletedEvent.java  (successful transaction)
│   │   ├── TransactionFailedEvent.java     (failed transaction)
│   │   ├── HardwareFailureEvent.java       (hardware issues)
│   │   ├── EmergencyModeActivatedEvent.java (emergency mode activated)
│   │   ├── CityMonitoringCenter.java       (listens to city-wide events)
│   │   ├── EmergencyCoordinator.java       (coordinates emergency responses)
│   │   ├── MaintenanceService.java         (maintenance listener)
│   │   └── SupplyChainSystem.java          (supply chain listener)
│   │
│   ├── factory/           # Factory pattern - kiosk creation
│   │   ├── AbstractKioskFactory.java       (abstract factory)
│   │   ├── PharmacyKioskFactory.java       (creates pharmacy kiosks)
│   │   ├── FoodKioskFactory.java           (creates food kiosks)
│   │   └── EmergencyReliefKioskFactory.java (creates emergency kiosks)
│   │
│   ├── failure/           # Chain of Responsibility - failure handling
│   │   ├── FailureHandler.java             (interface)
│   │   ├── FailureContext.java             (failure details)
│   │   ├── RetryHandler.java               (retry failed operations)
│   │   ├── RecalibrationHandler.java       (recalibrate hardware)
│   │   └── TechnicianAlertHandler.java     (alert technician if unresolved)
│   │
│   ├── hardware/          # Hardware interaction layer
│   │   └── HardwareController.java         (manages hardware modules)
│   │
│   ├── inventory/         # Inventory management
│   │   ├── InventoryManager.java           (manages stock levels)
│   │   ├── Product.java                    (product definition)
│   │   └── InventorySnapshot.java          (inventory state snapshot)
│   │
│   ├── payment/           # Payment processing
│   │   ├── PaymentProcessor.java           (interface)
│   │   ├── CardPaymentProcessor.java       (card payments)
│   │   ├── UPIPaymentProcessor.java        (UPI payments)
│   │   └── WalletPaymentProcessor.java     (wallet payments)
│   │
│   ├── persistence/       # Data persistence
│   │   └── PersistenceService.java         (JSON-based persistence)
│   │
│   ├── pricing/           # Strategy pattern - pricing strategies
│   │   ├── PricingStrategy.java            (interface)
│   │   ├── StandardPricingStrategy.java    (standard pricing)
│   │   ├── DiscountedPricingStrategy.java  (discounted pricing)
│   │   └── EmergencyPricingStrategy.java   (emergency fair pricing)
│   │
│   ├── registry/          # Central registry for kiosks
│   │   └── CentralRegistry.java            (manages all active kiosks)
│   │
│   ├── state/             # State pattern - operational states
│   │   ├── KioskState.java                 (interface)
│   │   ├── ActiveState.java                (normal operation)
│   │   ├── MaintenanceState.java           (under maintenance)
│   │   ├── PowerSavingState.java           (power saving mode)
│   │   └── EmergencyLockdownState.java     (emergency mode)
│   │
│   ├── verification/      # Verification modules
│   │   ├── VerificationModule.java         (interface)
│   │   ├── NoVerificationModule.java       (no verification)
│   │   ├── AgeVerificationModule.java      (age verification for food items)
│   │   ├── PrescriptionVerificationModule.java (prescription for pharmacy)
│   │   └── EmergencyVerificationModule.java (bypassed in emergencies)
│   │
│   └── simulation/        # Demo and testing
│       └── SimulationRunner.java           (runs 10 demo scenarios)
│
├── src/test/java/com/aura/
│   ├── CommandRollbackTest.java            (transaction rollback tests)
│   ├── EventBusTest.java                   (event system tests)
│   ├── FactoryTest.java                    (factory pattern tests)
│   ├── FailureChainTest.java               (failure handling tests)
│   ├── InventoryConcurrencyTest.java       (concurrent operations tests)
│   ├── KioskStateTest.java                 (state management tests)
│   ├── PaymentFlowTest.java                (payment processing tests)
│   ├── PersistenceServiceTest.java         (persistence tests)
│   └── PricingStrategyTest.java            (pricing strategy tests)
│
├── pom.xml                # Maven configuration
├── run_simulation.bat      # Windows demo script
├── run_simulation.sh       # Unix demo script
└── README.md              # This file
```

## Prerequisites

Before running this project, ensure you have the following installed:

- **Java Development Kit (JDK)**: Version 17 or higher
  - Check: `java -version`
  - Download: [Oracle JDK](https://www.oracle.com/java/technologies/downloads/) or [OpenJDK](https://openjdk.org/)

- **Maven** (optional, for advanced build tasks):
  - Check: `mvn -version`
  - Download: [Apache Maven](https://maven.apache.org/download.cgi)

## Running the Demo

### Method 1: Using Quick-Start Scripts (Recommended)

#### Windows:
```bash
run_simulation.bat
```

#### Unix/Linux/Mac:
```bash
chmod +x run_simulation.sh
./run_simulation.sh
```

**What the scripts do:**
1. Create `target/classes` directory
2. Compile all 52 Java source files with javac
3. Execute the simulation runner
4. Display 10 demo scenarios
5. Generate output files in `data/` directory

### Method 2: Using Maven

```bash
# Clean and compile
mvn clean compile

# Run tests
mvn test

# Package the project
mvn package
```

### Method 3: Manual Compilation and Execution

```bash
# Create output directory
mkdir target/classes

# Compile all packages (must compile in dependency order)
javac -d target/classes \
  src/main/java/com/aura/event/*.java \
  src/main/java/com/aura/failure/*.java \
  src/main/java/com/aura/payment/*.java \
  src/main/java/com/aura/persistence/*.java \
  src/main/java/com/aura/verification/*.java \
  src/main/java/com/aura/state/*.java \
  src/main/java/com/aura/inventory/*.java \
  src/main/java/com/aura/pricing/*.java \
  src/main/java/com/aura/hardware/*.java \
  src/main/java/com/aura/factory/*.java \
  src/main/java/com/aura/command/*.java \
  src/main/java/com/aura/registry/*.java \
  src/main/java/com/aura/core/*.java \
  src/main/java/com/aura/simulation/*.java

# Run the simulation
java -cp target/classes com.aura.simulation.SimulationRunner
```

## Demo Scenarios

The simulation runs **10 test scenarios** demonstrating all features:

| Scenario | What It Tests |
|----------|---------------|
| 1. Factory-Created Kiosks | Factory pattern with different kiosk configurations |
| 2. Normal Purchase | Standard purchase flow with payment and verification |
| 3. Dynamic Pricing | Strategy pattern with different pricing models |
| 4. Kiosk Operational Modes | State pattern (Active, PowerSaving, Maintenance, EmergencyLockdown) |
| 5. Emergency Mode Activation | Emergency event handling and mode switching |
| 6. Hardware Failure Recovery | Failure chain recovery with automatic recalibration |
| 7. Transaction Rollback | Atomic transactions with refund on failure |
| 8. Concurrent Transactions | Thread-safe inventory management |
| 9. Delayed Hardware Response | Async hardware operations |
| 10. Persistence | JSON file I/O for inventory and transactions |

### Output Files

After running the demo, check these files in the `data/` directory:

- **inventory.json**: Final inventory state with all products and stock counts
- **transactions.json**: Complete transaction history with timestamps and amounts
- **config.json**: System configuration and operational status

## Code Documentation

### Core Components

#### 1. **KioskController** (`core/KioskController.java`)
Main orchestrator for kiosk operations. Manages:
- Payment processing
- Inventory updates
- State transitions
- Event publishing
- Failure recovery chain

```java
// Example usage
KioskController kiosk = new KioskController(
    paymentProcessor, 
    verificationModule, 
    eventBus, 
    persistenceService,
    failureChain
);
kiosk.purchaseItem("PRODUCT_ID", 2);
```

#### 2. **EventBus** (`event/EventBus.java`)
Central event publishing system using Observer pattern:
- Subscribe to events
- Publish events
- Notify all listeners asynchronously

```java
// Subscribe to events
eventBus.subscribe(listener);

// Publish event
eventBus.publish(new TransactionCompletedEvent(...));
```

#### 3. **Factory Classes** (`factory/`)
Create specialized kiosks based on type:

```java
// Create pharmacy kiosk
KioskController pharmacy = new PharmacyKioskFactory()
    .createKiosk("PHARM-01");

// Create food kiosk  
KioskController food = new FoodKioskFactory()
    .createKiosk("FOOD-01");

// Create emergency kiosk
KioskController emergency = new EmergencyReliefKioskFactory()
    .createKiosk("EMG-01");
```

#### 4. **Pricing Strategies** (`pricing/`)
Dynamic pricing using Strategy pattern:

```java
// Standard pricing
kiosk.setPricingStrategy(new StandardPricingStrategy());

// Discounted pricing (15% off)
kiosk.setPricingStrategy(new DiscountedPricingStrategy(0.15));

// Emergency pricing (fair pricing, no markup)
kiosk.setPricingStrategy(new EmergencyPricingStrategy());
```

#### 5. **Failure Handling** (`failure/`)
Chain of Responsibility pattern for failure recovery:

```java
// Failure chain: Retry → Recalibrate → Technician Alert
FailureHandler retry = new RetryHandler();
FailureHandler recalibrate = new RecalibrationHandler();
FailureHandler technician = new TechnicianAlertHandler();

retry.setNext(recalibrate).setNext(technician);
retry.handle(failureContext);
```

#### 6. **Verification Modules** (`verification/`)
Different verification strategies for product categories:

```java
// Pharmacy verification (prescription check)
kiosk.setVerificationModule(new PrescriptionVerificationModule());

// Food verification (age check)
kiosk.setVerificationModule(new AgeVerificationModule());

// Emergency mode (no verification)
kiosk.setVerificationModule(new EmergencyVerificationModule());
```

#### 7. **Kiosk States** (`state/`)
State pattern for operational modes:

```java
// Switch to different states
kiosk.setCurrentState(new ActiveState());           // Normal operation
kiosk.setCurrentState(new MaintenanceState());      // Under maintenance
kiosk.setCurrentState(new PowerSavingState());      // Power saving
kiosk.setCurrentState(new EmergencyLockdownState()); // Emergency mode
```

#### 8. **Persistence** (`persistence/`)
JSON-based data persistence:

```java
// Save inventory
persistenceService.saveInventory(products);

// Load inventory
List<Product> loaded = persistenceService.loadInventory();

// Save transactions
persistenceService.saveTransaction(transaction);
```

#### 9. **Commands** (`command/`)
Command pattern for transactional operations:

```java
// Create purchase command
Command purchase = new PurchaseItemCommand(
    inventoryManager, 
    paymentProcessor,
    verificationModule, 
    eventBus, 
    persistenceService,
    failureChain
);
purchase.execute(); // Execute transaction

// Create refund command
Command refund = new RefundCommand(inventoryManager, persistenceService);
refund.rollback(); // Rollback on failure
```

## Testing

Run the unit tests:

```bash
# Using Maven
mvn test

# Using provided test runner (if configured)
java -cp target/classes:target/test-classes org.junit.runner.JUnitCore com.aura.EventBusTest
```

Test files include:
- `EventBusTest.java`: Event publishing and subscription
- `FactoryTest.java`: Kiosk creation with different configurations
- `PaymentFlowTest.java`: Payment processing
- `KioskStateTest.java`: State transitions
- `FailureChainTest.java`: Failure recovery
- `InventoryConcurrencyTest.java`: Thread-safe operations
- `CommandRollbackTest.java`: Transaction rollback
- `PricingStrategyTest.java`: Pricing calculations
- `PersistenceServiceTest.java`: Data persistence

## Troubleshooting

### Issue: "package com.aura.* does not exist"
**Solution:** Make sure to compile packages in dependency order. Use the provided scripts or follow the manual compilation steps above.

### Issue: "Main class SimulationRunner not found"
**Solution:** Ensure the classpath includes `target/classes` when running the simulation.

### Issue: "Permission denied" on Unix
**Solution:** Make the script executable:
```bash
chmod +x run_simulation.sh
```

## License

This project is for educational purposes demonstrating OOP design patterns.

## Author

Created as a comprehensive OOP lab project implementing multiple design patterns in a real-world retail kiosk scenario.

