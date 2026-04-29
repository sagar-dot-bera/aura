package com.aura;

import com.aura.core.KioskController;
import com.aura.hardware.HardwareController;
import com.aura.inventory.InventoryManager;
import com.aura.inventory.Product;
import com.aura.pricing.StandardPricingStrategy;
import com.aura.registry.CentralRegistry;
import com.aura.state.EmergencyLockdownState;
import com.aura.state.MaintenanceState;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Kiosk State Tests")
public class KioskStateTest {
    @Test
    @DisplayName("Maintenance blocks purchase")
    public void testMaintenanceBlocksPurchase() {
        InventoryManager inventoryManager = new InventoryManager();
        inventoryManager.addProduct(new Product("S001", "Soap", 2.0, 5, "dispenser"));
        HardwareController hardwareController = new HardwareController();
        hardwareController.initializeModule("dispenser", true);

        KioskController kiosk = new KioskController("K1", inventoryManager, hardwareController,
                new StandardPricingStrategy());
        kiosk.setCurrentState(new MaintenanceState());

        String result = kiosk.purchaseItem("S001", 1);
        assertTrue(result.contains("blocked"));
    }

    @Test
    @DisplayName("Emergency state enforces limit")
    public void testEmergencyLimit() {
        CentralRegistry registry = CentralRegistry.getInstance();
        registry.setEmergencyPurchaseLimit(1);
        registry.addEssentialProduct("E001");

        InventoryManager inventoryManager = new InventoryManager();
        inventoryManager.addProduct(new Product("E001", "Water", 1.0, 10, "dispenser"));
        inventoryManager.addProduct(new Product("X001", "NonEssential", 1.0, 10, "dispenser"));

        HardwareController hardwareController = new HardwareController();
        hardwareController.initializeModule("dispenser", true);

        KioskController kiosk = new KioskController("K1", inventoryManager, hardwareController,
                new StandardPricingStrategy());
        kiosk.setCurrentState(new EmergencyLockdownState());

        String resultNonEssential = kiosk.purchaseItem("X001", 1);
        assertTrue(resultNonEssential.contains("blocked"));

        String resultLimit = kiosk.purchaseItem("E001", 2);
        assertTrue(resultLimit.contains("blocked"));
    }
}
