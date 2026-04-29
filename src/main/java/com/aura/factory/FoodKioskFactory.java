package com.aura.factory;

import com.aura.core.KioskController;
import com.aura.event.CityMonitoringCenter;
import com.aura.event.EmergencyCoordinator;
import com.aura.event.EventBus;
import com.aura.event.MaintenanceService;
import com.aura.event.SupplyChainSystem;
import com.aura.failure.FailureHandler;
import com.aura.failure.RecalibrationHandler;
import com.aura.failure.RetryHandler;
import com.aura.failure.TechnicianAlertHandler;
import com.aura.hardware.HardwareController;
import com.aura.inventory.InventoryManager;
import com.aura.inventory.Product;
import com.aura.payment.PaymentProcessor;
import com.aura.payment.WalletPaymentProcessor;
import com.aura.persistence.PersistenceService;
import com.aura.pricing.DiscountedPricingStrategy;
import com.aura.pricing.PricingStrategy;
import com.aura.verification.AgeVerificationModule;
import com.aura.verification.VerificationModule;

public class FoodKioskFactory implements AbstractKioskFactory {
    @Override
    public KioskController createKiosk(String kioskId) {
        InventoryManager inventoryManager = createInventoryManager();
        HardwareController hardwareController = createHardwareController();
        PricingStrategy pricingStrategy = createPricingStrategy();
        PaymentProcessor paymentProcessor = createPaymentProcessor();
        VerificationModule verificationModule = createVerificationModule();

        EventBus eventBus = buildEventBus();
        PersistenceService persistenceService = new PersistenceService();
        com.aura.registry.CentralRegistry.getInstance().setPersistenceService(persistenceService);
        FailureHandler failureChain = buildFailureChain();

        inventoryManager.setEventBus(eventBus);
        inventoryManager.setHardwareController(hardwareController);

        return new KioskController(kioskId, inventoryManager, hardwareController, pricingStrategy,
                paymentProcessor, verificationModule, eventBus, persistenceService, failureChain);
    }

    @Override
    public PricingStrategy createPricingStrategy() {
        return new DiscountedPricingStrategy(0.15);
    }

    @Override
    public PaymentProcessor createPaymentProcessor() {
        return new WalletPaymentProcessor();
    }

    @Override
    public VerificationModule createVerificationModule() {
        return new AgeVerificationModule();
    }

    @Override
    public HardwareController createHardwareController() {
        HardwareController controller = new HardwareController();
        controller.initializeModule("refrigeration", true);
        controller.initializeModule("dispenser", true);
        return controller;
    }

    @Override
    public InventoryManager createInventoryManager() {
        InventoryManager manager = new InventoryManager();
        manager.addProduct(new Product("F001", "Energy Drink", 3.5, 25, "dispenser"));
        manager.addProduct(new Product("F002", "Snack Bar", 2.0, 30, "dispenser"));
        return manager;
    }

    private EventBus buildEventBus() {
        EventBus bus = new EventBus();
        bus.subscribe(new MaintenanceService());
        bus.subscribe(new SupplyChainSystem());
        bus.subscribe(new CityMonitoringCenter());
        bus.subscribe(new EmergencyCoordinator());
        return bus;
    }

    private FailureHandler buildFailureChain() {
        FailureHandler retry = new RetryHandler();
        FailureHandler recalibrate = new RecalibrationHandler();
        FailureHandler technician = new TechnicianAlertHandler();
        retry.setNext(recalibrate).setNext(technician);
        return retry;
    }
}
