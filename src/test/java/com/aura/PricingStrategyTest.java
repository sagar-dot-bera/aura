package com.aura;

import com.aura.core.KioskController;
import com.aura.hardware.HardwareController;
import com.aura.inventory.InventoryManager;
import com.aura.inventory.Product;
import com.aura.pricing.DiscountedPricingStrategy;
import com.aura.pricing.EmergencyPricingStrategy;
import com.aura.pricing.StandardPricingStrategy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test suite for pricing strategy pattern.
 */
@DisplayName("Pricing Strategy Tests")
public class PricingStrategyTest {

    @Test
    @DisplayName("StandardPricingStrategy returns base price")
    public void testStandardPricingStrategy() {
        Product product = new Product("P001", "Paracetamol", 10.0, 10);
        StandardPricingStrategy strategy = new StandardPricingStrategy();

        double price = strategy.computePrice(product);
        assertEquals(10.0, price, 0.01);
    }

    @Test
    @DisplayName("DiscountedPricingStrategy applies discount correctly")
    public void testDiscountedPricingStrategy() {
        Product product = new Product("P001", "Paracetamol", 10.0, 10);
        DiscountedPricingStrategy strategy = new DiscountedPricingStrategy(0.2); // 20% off

        double price = strategy.computePrice(product);
        assertEquals(8.0, price, 0.01);
    }

    @Test
    @DisplayName("EmergencyPricingStrategy returns base price")
    public void testEmergencyPricingStrategy() {
        Product product = new Product("P001", "Paracetamol", 10.0, 10);
        EmergencyPricingStrategy strategy = new EmergencyPricingStrategy();

        double price = strategy.computePrice(product);
        assertEquals(10.0, price, 0.01);
    }

    @Test
    @DisplayName("Switching strategy on KioskController changes price")
    public void testStrategySwitch() {
        Product product = new Product("P001", "Paracetamol", 10.0, 10);
        InventoryManager invMgr = new InventoryManager();
        invMgr.addProduct(product);
        HardwareController hwCtrl = new HardwareController();
        KioskController kiosk = new KioskController("KIOSK-01", invMgr, hwCtrl,
                new StandardPricingStrategy());

        // Test initial price with standard strategy
        double price1 = kiosk.getPricingStrategy().computePrice(product);
        assertEquals(10.0, price1, 0.01);

        // Switch to discounted strategy
        kiosk.setPricingStrategy(new DiscountedPricingStrategy(0.3)); // 30% off
        double price2 = kiosk.getPricingStrategy().computePrice(product);
        assertEquals(7.0, price2, 0.01);

        // Switch to emergency strategy
        kiosk.setPricingStrategy(new EmergencyPricingStrategy());
        double price3 = kiosk.getPricingStrategy().computePrice(product);
        assertEquals(10.0, price3, 0.01);
    }
}
