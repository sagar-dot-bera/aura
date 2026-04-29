package com.aura.factory;

import com.aura.core.KioskController;
import com.aura.hardware.HardwareController;
import com.aura.inventory.InventoryManager;
import com.aura.payment.PaymentProcessor;
import com.aura.pricing.PricingStrategy;
import com.aura.verification.VerificationModule;

// Abstract Factory Pattern: creates compatible kiosk component family
public interface AbstractKioskFactory {
    KioskController createKiosk(String kioskId);

    PricingStrategy createPricingStrategy();

    PaymentProcessor createPaymentProcessor();

    VerificationModule createVerificationModule();

    HardwareController createHardwareController();

    InventoryManager createInventoryManager();
}
