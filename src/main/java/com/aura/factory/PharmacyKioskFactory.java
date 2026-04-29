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
import com.aura.payment.CardPaymentProcessor;
import com.aura.payment.PaymentProcessor;
import com.aura.persistence.PersistenceService;
import com.aura.pricing.PricingStrategy;
import com.aura.pricing.StandardPricingStrategy;
import com.aura.verification.PrescriptionVerificationModule;
import com.aura.verification.VerificationModule;

public class PharmacyKioskFactory implements AbstractKioskFactory {
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
        return new StandardPricingStrategy();
    }

    @Override
    public PaymentProcessor createPaymentProcessor() {
        return new CardPaymentProcessor();
    }

    @Override
    public VerificationModule createVerificationModule() {
        return new PrescriptionVerificationModule();
    }

    @Override
    public HardwareController createHardwareController() {
        HardwareController controller = new HardwareController();
        controller.initializeModule("secure-dispenser", true);
        controller.initializeModule("dispenser", true);
        return controller;
    }

    @Override
    public InventoryManager createInventoryManager() {
        InventoryManager manager = new InventoryManager();
        manager.addProduct(new Product("M001", "Paracetamol", 5.0, 20, "secure-dispenser"));
        manager.addProduct(new Product("M002", "Cough Syrup", 8.0, 15, "secure-dispenser"));
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
