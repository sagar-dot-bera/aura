package com.aura;

import com.aura.core.KioskController;
import com.aura.factory.EmergencyReliefKioskFactory;
import com.aura.factory.FoodKioskFactory;
import com.aura.factory.PharmacyKioskFactory;
import com.aura.pricing.EmergencyPricingStrategy;
import com.aura.pricing.StandardPricingStrategy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Factory Tests")
public class FactoryTest {
    @Test
    @DisplayName("Factories create kiosks with different pricing")
    public void testFactoriesCreateKiosks() {
        KioskController pharmacy = new PharmacyKioskFactory().createKiosk("PHARM-1");
        KioskController food = new FoodKioskFactory().createKiosk("FOOD-1");
        KioskController emergency = new EmergencyReliefKioskFactory().createKiosk("EMG-1");

        assertTrue(pharmacy.getPricingStrategy() instanceof StandardPricingStrategy);
        assertNotNull(food.getPricingStrategy());
        assertTrue(emergency.getPricingStrategy() instanceof EmergencyPricingStrategy);
    }
}
