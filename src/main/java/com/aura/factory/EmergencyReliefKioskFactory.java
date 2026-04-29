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
import com.aura.payment.UPIPaymentProcessor;
import com.aura.persistence.PersistenceService;
import com.aura.pricing.EmergencyPricingStrategy;
import com.aura.pricing.PricingStrategy;
import com.aura.registry.CentralRegistry;
import com.aura.state.EmergencyLockdownState;
import com.aura.verification.EmergencyVerificationModule;
import com.aura.verification.VerificationModule;

public class EmergencyReliefKioskFactory implements AbstractKioskFactory {
    @Override
    public KioskController createKiosk(String kioskId) {
        CentralRegistry registry = CentralRegistry.getInstance();
        registry.setEmergencyPurchaseLimit(2);

        InventoryManager inventoryManager = createInventoryManager();
        HardwareController hardwareController = createHardwareController();
        PricingStrategy pricingStrategy = createPricingStrategy();
        PaymentProcessor paymentProcessor = createPaymentProcessor();
        VerificationModule verificationModule = createVerificationModule();

        EventBus eventBus = buildEventBus();
        PersistenceService persistenceService = new PersistenceService();
        CentralRegistry.getInstance().setPersistenceService(persistenceService);
        FailureHandler failureChain = buildFailureChain();

        inventoryManager.setEventBus(eventBus);
        inventoryManager.setHardwareController(hardwareController);

        KioskController kiosk = new KioskController(kioskId, inventoryManager, hardwareController, pricingStrategy,
                paymentProcessor, verificationModule, eventBus, persistenceService, failureChain);
        kiosk.setCurrentState(new EmergencyLockdownState());
        return kiosk;
    }

    @Override
    public PricingStrategy createPricingStrategy() {
        return new EmergencyPricingStrategy();
    }

    @Override
    public PaymentProcessor createPaymentProcessor() {
        return new UPIPaymentProcessor();
    }

    @Override
    public VerificationModule createVerificationModule() {
        return new EmergencyVerificationModule();
    }

    @Override
    public HardwareController createHardwareController() {
        HardwareController controller = new HardwareController();
        controller.initializeModule("dispenser", true);
        return controller;
    }

    @Override
    public InventoryManager createInventoryManager() {
        CentralRegistry registry = CentralRegistry.getInstance();
        registry.addEssentialProduct("E001");
        registry.addEssentialProduct("E002");

        InventoryManager manager = new InventoryManager();
        manager.addProduct(new Product("E001", "Water Pack", 1.0, 50, "dispenser"));
        manager.addProduct(new Product("E002", "First Aid Kit", 5.0, 20, "dispenser"));
        manager.addProduct(new Product("N001", "NonEssential Item", 7.0, 10, "dispenser"));
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
