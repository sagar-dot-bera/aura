package com.aura.simulation;

import com.aura.command.PurchaseItemCommand;
import com.aura.core.KioskController;
import com.aura.hardware.HardwareController;
import com.aura.inventory.InventoryManager;
import com.aura.inventory.Product;
import com.aura.pricing.DiscountedPricingStrategy;
import com.aura.pricing.EmergencyPricingStrategy;
import com.aura.pricing.StandardPricingStrategy;

/**
 * Simulation runner demonstrating the core functionality:
 * - Strategy pattern for dynamic pricing
 * - Command pattern for atomic operations
 * - Memento pattern for rollback on failure
 */
public class SimulationRunner {

    public static void main(String[] args) {
        System.out.println("=== AURA RETAIL OS - SIMULATION START ===\n");

        // Scenario A: Normal Purchase
        scenarioA();
        System.out.println();

        // Scenario B: Pricing Strategy Switch
        scenarioB();
        System.out.println();

        // Scenario C: Hardware Failure + Rollback
        scenarioC();

        System.out.println("\n=== SIMULATION END ===");
    }

    /**
     * Scenario A: Normal purchase with standard pricing
     */
    private static void scenarioA() {
        System.out.println("--- Scenario A: Normal Purchase ---");

        // Setup
        InventoryManager inventoryManager = new InventoryManager();
        Product paracetamol = new Product("P001", "Paracetamol", 5.0, 10);
        inventoryManager.addProduct(paracetamol);

        HardwareController hardwareController = new HardwareController();
        hardwareController.initializeModule("dispenser", true);

        KioskController kiosk = new KioskController("KIOSK-01", inventoryManager,
                hardwareController, new StandardPricingStrategy());

        // Execute purchase
        int stockBefore = inventoryManager.getAvailableStock("P001");
        try {
            PurchaseItemCommand purchaseCmd = new PurchaseItemCommand("P001", 2,
                    inventoryManager, hardwareController, kiosk.getPricingStrategy());
            kiosk.executeCommand(purchaseCmd);
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            return;
        }

        int stockAfter = inventoryManager.getAvailableStock("P001");
        System.out.println("Scenario A: purchased 2x " + paracetamol.getName() +
                " @ " + paracetamol.getBasePrice() + " each. Stock left: " + stockAfter);
        if (stockAfter != 8) {
            System.out.println("WARNING: Expected stock 8, got " + stockAfter);
        }
    }

    /**
     * Scenario B: Switching pricing strategies at runtime
     */
    private static void scenarioB() {
        System.out.println("--- Scenario B: Pricing Strategy Switch ---");

        // Setup (reuse inventory from Scenario A practically)
        InventoryManager inventoryManager = new InventoryManager();
        Product paracetamol = new Product("P001", "Paracetamol", 10.0, 20);
        inventoryManager.addProduct(paracetamol);

        HardwareController hardwareController = new HardwareController();
        hardwareController.initializeModule("dispenser", true);

        // Start with standard pricing
        StandardPricingStrategy standardPricing = new StandardPricingStrategy();
        KioskController kiosk = new KioskController("KIOSK-01", inventoryManager,
                hardwareController, standardPricing);

        // Print current price
        double standardPrice = kiosk.getPricingStrategy().computePrice(paracetamol);
        System.out.println("Standard pricing: " + standardPrice);

        // Switch to emergency pricing
        EmergencyPricingStrategy emergencyPricing = new EmergencyPricingStrategy();
        kiosk.setPricingStrategy(emergencyPricing);
        double emergencyPrice = kiosk.getPricingStrategy().computePrice(paracetamol);
        System.out.println("Emergency pricing: " + emergencyPrice);
        if (Math.abs(standardPrice - emergencyPrice) > 0.01) {
            System.out.println("WARNING: Prices should be equal, but got " + standardPrice + " vs " + emergencyPrice);
        }

        // Switch to discounted pricing (30% off)
        DiscountedPricingStrategy discountedPricing = new DiscountedPricingStrategy(0.30);
        kiosk.setPricingStrategy(discountedPricing);
        double discountedPrice = kiosk.getPricingStrategy().computePrice(paracetamol);
        System.out.println("Discounted pricing (30% off): " + discountedPrice);
        if (Math.abs(7.0 - discountedPrice) > 0.01) {
            System.out.println("WARNING: Expected discounted price 7.0, got " + discountedPrice);
        }
    }

    /**
     * Scenario C: Hardware failure triggers rollback via Memento
     */
    private static void scenarioC() {
        System.out.println("--- Scenario C: Hardware Failure + Rollback ---");

        // Setup
        InventoryManager inventoryManager = new InventoryManager();
        Product paracetamol = new Product("P001", "Paracetamol", 5.0, 10);
        inventoryManager.addProduct(paracetamol);

        HardwareController hardwareController = new HardwareController();
        hardwareController.initializeModule("dispenser", true);

        KioskController kiosk = new KioskController("KIOSK-01", inventoryManager,
                hardwareController, new StandardPricingStrategy());

        // Record initial stock
        int stockBefore = inventoryManager.getAvailableStock("P001");

        // First successful purchase
        try {
            PurchaseItemCommand purchaseCmd1 = new PurchaseItemCommand("P001", 2,
                    inventoryManager, hardwareController, kiosk.getPricingStrategy());
            kiosk.executeCommand(purchaseCmd1);
        } catch (Exception e) {
            System.out.println("ERROR in first purchase: " + e.getMessage());
            return;
        }

        int stockAfterFirst = inventoryManager.getAvailableStock("P001");
        System.out.println("After first purchase: stock = " + stockAfterFirst);

        // Simulate hardware failure
        hardwareController.simulateFailure("dispenser");
        System.out.println("Hardware failure simulated");

        // Try to purchase when hardware is down
        try {
            PurchaseItemCommand purchaseCmd2 = new PurchaseItemCommand("P001", 3,
                    inventoryManager, hardwareController, kiosk.getPricingStrategy());
            purchaseCmd2.execute();
            System.out.println("ERROR: Purchase should have failed!");
        } catch (RuntimeException e) {
            System.out.println("Purchase failed as expected: " + e.getMessage());
        }

        // Check that stock was rolled back (not modified by failed purchase attempt)
        int stockAfterFailure = inventoryManager.getAvailableStock("P001");
        System.out.println("Scenario C: rollback successful, stock unchanged at " + stockAfterFailure);
        if (stockAfterFirst != stockAfterFailure) {
            System.out.println("WARNING: Stock should remain unchanged, but changed from " + stockAfterFirst + " to "
                    + stockAfterFailure);
        }
    }
}
