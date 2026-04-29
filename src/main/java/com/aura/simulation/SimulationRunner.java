package com.aura.simulation;

import com.aura.core.KioskController;
import com.aura.core.KioskInterface;
import com.aura.event.EmergencyModeActivatedEvent;
import com.aura.event.EventBus;
import com.aura.factory.AbstractKioskFactory;
import com.aura.factory.EmergencyReliefKioskFactory;
import com.aura.factory.FoodKioskFactory;
import com.aura.factory.PharmacyKioskFactory;
import com.aura.hardware.HardwareController;
import com.aura.inventory.InventoryManager;
import com.aura.inventory.Product;
import com.aura.payment.CardPaymentProcessor;
import com.aura.payment.PaymentProcessor;
import com.aura.persistence.PersistenceService;
import com.aura.pricing.DiscountedPricingStrategy;
import com.aura.pricing.EmergencyPricingStrategy;
import com.aura.pricing.PricingStrategy;
import com.aura.pricing.StandardPricingStrategy;
import com.aura.registry.CentralRegistry;
import com.aura.state.ActiveState;
import com.aura.state.EmergencyLockdownState;
import com.aura.state.MaintenanceState;
import com.aura.state.PowerSavingState;
import com.aura.verification.NoVerificationModule;
import com.aura.verification.VerificationModule;
import com.aura.failure.FailureHandler;
import com.aura.failure.RecalibrationHandler;
import com.aura.failure.RetryHandler;
import com.aura.failure.TechnicianAlertHandler;

import java.util.HashMap;
import java.util.Map;

public class SimulationRunner {

    public static void main(String[] args) {
        System.out.println("=== AURA RETAIL OS - FINAL SIMULATION ===\n");

        scenario1FactoryCreatedKiosks();
        scenario2NormalPurchase();
        scenario3DynamicPricing();
        scenario4KioskOperationalModes();
        scenario5EmergencyModeActivation();
        scenario6HardwareFailureRecovery();
        scenario7TransactionRollback();
        scenario8ConcurrentTransactions();
        scenario9DelayedHardwareResponse();
        scenario10Persistence();

        System.out.println("\n=== SIMULATION COMPLETE ===");
    }

    private static void scenario1FactoryCreatedKiosks() {
        System.out.println("--- Scenario 1: Factory-Created Kiosks ---");

        KioskController pharmacy = new PharmacyKioskFactory().createKiosk("PHARM-01");
        KioskController food = new FoodKioskFactory().createKiosk("FOOD-01");
        KioskController emergency = new EmergencyReliefKioskFactory().createKiosk("EMG-01");

        System.out.println("Pharmacy pricing: " + pharmacy.getPricingStrategy().getPolicyName());
        System.out.println("Pharmacy verification: " + pharmacy.getVerificationModuleName());
        System.out.println("Pharmacy hardware: " + pharmacy.getHardwareSummary());

        System.out.println("Food pricing: " + food.getPricingStrategy().getPolicyName());
        System.out.println("Food verification: " + food.getVerificationModuleName());
        System.out.println("Food hardware: " + food.getHardwareSummary());

        System.out.println("Emergency pricing: " + emergency.getPricingStrategy().getPolicyName());
        System.out.println("Emergency verification: " + emergency.getVerificationModuleName());
        System.out.println("Emergency hardware: " + emergency.getHardwareSummary());
        System.out.println();
    }

    private static void scenario2NormalPurchase() {
        System.out.println("--- Scenario 2: Normal Purchase ---");

        KioskController kiosk = new PharmacyKioskFactory().createKiosk("PHARM-02");
        KioskInterface facade = kiosk;

        String result = facade.purchaseItem("M001", 2);
        System.out.println(result);
        System.out.println("Stock after purchase: " + kiosk.getAvailableStock("M001"));
        System.out.println();
    }

    private static void scenario3DynamicPricing() {
        System.out.println("--- Scenario 3: Dynamic Pricing ---");

        InventoryManager inventoryManager = new InventoryManager();
        Product product = new Product("D001", "DemoItem", 10.0, 10, "dispenser");
        inventoryManager.addProduct(product);

        HardwareController hardwareController = new HardwareController();
        hardwareController.initializeModule("dispenser", true);

        KioskController kiosk = new KioskController("K-PRICING", inventoryManager, hardwareController,
                new StandardPricingStrategy());

        double standardPrice = kiosk.getPricingStrategy().computePrice(product);
        System.out.println("Standard pricing: " + standardPrice);

        kiosk.setPricingStrategy(new DiscountedPricingStrategy(0.25));
        double discountedPrice = kiosk.getPricingStrategy().computePrice(product);
        System.out.println("Discounted pricing: " + discountedPrice);

        kiosk.setPricingStrategy(new EmergencyPricingStrategy());
        double emergencyPrice = kiosk.getPricingStrategy().computePrice(product);
        System.out.println("Emergency pricing: " + emergencyPrice);
        System.out.println();
    }

    private static void scenario4KioskOperationalModes() {
        System.out.println("--- Scenario 4: Kiosk Operational Modes ---");

        InventoryManager inventoryManager = new InventoryManager();
        inventoryManager.addProduct(new Product("S001", "Soap", 2.0, 5, "dispenser"));

        HardwareController hardwareController = new HardwareController();
        hardwareController.initializeModule("dispenser", true);

        KioskController kiosk = new KioskController("K-STATE", inventoryManager, hardwareController,
                new StandardPricingStrategy());

        kiosk.setCurrentState(new ActiveState());
        System.out.println("ActiveState: " + kiosk.purchaseItem("S001", 1));

        kiosk.setCurrentState(new PowerSavingState());
        System.out.println("PowerSavingState: " + kiosk.purchaseItem("S001", 1));

        kiosk.setCurrentState(new MaintenanceState());
        System.out.println("MaintenanceState: " + kiosk.purchaseItem("S001", 1));

        CentralRegistry registry = CentralRegistry.getInstance();
        registry.setEmergencyPurchaseLimit(1);
        registry.addEssentialProduct("S001");
        kiosk.setCurrentState(new EmergencyLockdownState());
        System.out.println("EmergencyLockdownState (essential): " + kiosk.purchaseItem("S001", 1));
        System.out.println();
    }

    private static void scenario5EmergencyModeActivation() {
        System.out.println("--- Scenario 5: Emergency Mode Activation ---");

        KioskController kiosk = new EmergencyReliefKioskFactory().createKiosk("EMG-02");
        kiosk.publishEvent(new EmergencyModeActivatedEvent("City emergency"));

        kiosk.setCurrentState(new EmergencyLockdownState());
        kiosk.setPricingStrategy(new EmergencyPricingStrategy());

        String nonEssential = kiosk.purchaseItem("N001", 1);
        System.out.println("Non-essential purchase: " + nonEssential);

        String aboveLimit = kiosk.purchaseItem("E001", 3);
        System.out.println("Essential above limit: " + aboveLimit);

        String withinLimit = kiosk.purchaseItem("E001", 1);
        System.out.println("Essential within limit: " + withinLimit);
        System.out.println();
    }

    private static void scenario6HardwareFailureRecovery() {
        System.out.println("--- Scenario 6: Hardware Failure Recovery ---");

        InventoryManager inventoryManager = new InventoryManager();
        inventoryManager.addProduct(new Product("H001", "HardwareItem", 4.0, 5, "dispenser"));

        HardwareController hardwareController = new HardwareController();
        hardwareController.initializeModule("dispenser", false);

        KioskController kiosk = new KioskController("K-HW", inventoryManager, hardwareController,
                new StandardPricingStrategy());

        hardwareController.simulateFailure("dispenser");
        System.out.println("Forced dispenser failure");

        try {
            System.out.println(kiosk.purchaseItem("H001", 1));
            System.out.println("Recovery succeeded after recalibration");
        } catch (RuntimeException ex) {
            System.out.println("Recovery failed, rollback executed: " + ex.getMessage());
        }
        System.out.println();
    }

    private static void scenario7TransactionRollback() {
        System.out.println("--- Scenario 7: Transaction Rollback ---");

        InventoryManager inventoryManager = new InventoryManager();
        inventoryManager.addProduct(new Product("R001", "RollbackItem", 6.0, 3, "dispenser"));

        HardwareController hardwareController = new HardwareController();
        hardwareController.initializeModule("dispenser", true);
        hardwareController.failNextDispense();
        hardwareController.failNextRecalibration();

        KioskController kiosk = new KioskController("K-ROLLBACK", inventoryManager, hardwareController,
                new StandardPricingStrategy());

        int before = inventoryManager.getAvailableStock("R001");
        try {
            kiosk.purchaseItem("R001", 1);
        } catch (RuntimeException ex) {
            System.out.println("Dispense failed, refund issued, inventory restored");
        }
        int after = inventoryManager.getAvailableStock("R001");
        System.out.println("Stock before=" + before + ", after=" + after);
        System.out.println();
    }

    private static void scenario8ConcurrentTransactions() {
        System.out.println("--- Scenario 8: Concurrent Transactions ---");

        AbstractKioskFactory factory = new AbstractKioskFactory() {
            @Override
            public KioskController createKiosk(String kioskId) {
                InventoryManager inventoryManager = createInventoryManager();
                HardwareController hardwareController = createHardwareController();
                PricingStrategy pricingStrategy = createPricingStrategy();
                PaymentProcessor paymentProcessor = createPaymentProcessor();
                VerificationModule verificationModule = createVerificationModule();
                EventBus eventBus = new EventBus();
                PersistenceService persistenceService = new PersistenceService();
                FailureHandler failureChain = buildFailureChain();

                inventoryManager.setHardwareController(hardwareController);
                inventoryManager.setEventBus(eventBus);

                return new KioskController(kioskId, inventoryManager, hardwareController, pricingStrategy,
                        paymentProcessor, verificationModule, eventBus, persistenceService, failureChain);
            }

            @Override
            public PricingStrategy createPricingStrategy() {
                return new StandardPricingStrategy();
            }

            @Override
            public PaymentProcessor createPaymentProcessor() {
                return new CardPaymentProcessor();
            }

            @Override
            public VerificationModule createVerificationModule() {
                return new NoVerificationModule();
            }

            @Override
            public HardwareController createHardwareController() {
                HardwareController controller = new HardwareController();
                controller.initializeModule("dispenser", true);
                return controller;
            }

            @Override
            public InventoryManager createInventoryManager() {
                InventoryManager manager = new InventoryManager();
                manager.addProduct(new Product("C001", "ConcurrencyItem", 5.0, 1, "dispenser"));
                return manager;
            }

            private FailureHandler buildFailureChain() {
                FailureHandler retry = new RetryHandler();
                FailureHandler recalibrate = new RecalibrationHandler();
                FailureHandler technician = new TechnicianAlertHandler();
                retry.setNext(recalibrate).setNext(technician);
                return retry;
            }
        };

        KioskController kiosk = factory.createKiosk("K-CONCURRENT");

        Thread t1 = new Thread(() -> {
            try {
                kiosk.purchaseItem("C001", 1);
                System.out.println("Thread1 purchase succeeded");
            } catch (RuntimeException ex) {
                System.out.println("Thread1 purchase failed");
            }
        });
        Thread t2 = new Thread(() -> {
            try {
                kiosk.purchaseItem("C001", 1);
                System.out.println("Thread2 purchase succeeded");
            } catch (RuntimeException ex) {
                System.out.println("Thread2 purchase failed");
            }
        });

        t1.start();
        t2.start();
        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("Final stock: " + kiosk.getAvailableStock("C001"));
        System.out.println();
    }

    private static void scenario9DelayedHardwareResponse() {
        System.out.println("--- Scenario 9: Delayed Hardware Response ---");

        InventoryManager inventoryManager = new InventoryManager();
        inventoryManager.addProduct(new Product("D001", "DelayedItem", 5.0, 2, "dispenser"));

        HardwareController hardwareController = new HardwareController();
        hardwareController.initializeModule("dispenser", true);
        hardwareController.enableDelayedResponse(true);

        KioskController kiosk = new KioskController("K-DELAY", inventoryManager, hardwareController,
                new StandardPricingStrategy());

        System.out.println("Transaction status: PENDING");
        String result = kiosk.purchaseItem("D001", 1);
        System.out.println("Transaction status: SUCCESS");
        System.out.println(result);
        System.out.println();
    }

    private static void scenario10Persistence() {
        System.out.println("--- Scenario 10: Persistence ---");

        PersistenceService persistenceService = new PersistenceService();
        CentralRegistry registry = CentralRegistry.getInstance();
        registry.setSystemStatus("OPERATIONAL");
        registry.setEmergencyPurchaseLimit(2);

        Map<String, Integer> restock = new HashMap<>();
        restock.put("P001", 5);

        InventoryManager inventoryManager = new InventoryManager();
        inventoryManager.addProduct(new Product("P001", "SavedItem", 2.0, 5, "dispenser"));

        persistenceService.saveInventory(new java.util.ArrayList<>(inventoryManager.getAllProducts().values()));
        persistenceService.saveTransaction("SIM-TX-01");
        persistenceService.saveConfig(registry);

        System.out.println("Inventory loaded: " + persistenceService.loadInventory().size());
        System.out.println("Transactions loaded: " + persistenceService.loadTransactions().size());
        persistenceService.loadConfig(registry);
        System.out.println("Config loaded: status=" + registry.getSystemStatus());
        System.out.println();
    }
}
