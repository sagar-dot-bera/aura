package com.aura;

import com.aura.registry.CentralRegistry;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CentralRegistry Tests")
public class CentralRegistryTest {
    @Test
    @DisplayName("Singleton instance is same")
    public void testSingletonInstance() {
        CentralRegistry a = CentralRegistry.getInstance();
        CentralRegistry b = CentralRegistry.getInstance();
        assertSame(a, b);
    }

    @Test
    @DisplayName("Emergency config works")
    public void testEmergencyConfig() {
        CentralRegistry registry = CentralRegistry.getInstance();
        registry.activateEmergencyMode();
        registry.setEmergencyPurchaseLimit(2);
        registry.addEssentialProduct("E001");

        assertTrue(registry.isEmergencyMode());
        assertEquals(2, registry.getEmergencyPurchaseLimit());
        assertTrue(registry.isEssentialProduct("E001"));

        registry.deactivateEmergencyMode();
        assertFalse(registry.isEmergencyMode());
    }
}
