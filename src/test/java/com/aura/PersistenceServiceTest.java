package com.aura;

import com.aura.inventory.Product;
import com.aura.persistence.PersistenceService;
import com.aura.registry.CentralRegistry;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Persistence Service Tests")
public class PersistenceServiceTest {
    @Test
    @DisplayName("Save and load inventory")
    public void testSaveLoadInventory() {
        PersistenceService service = new PersistenceService();
        List<Product> products = new ArrayList<>();
        products.add(new Product("P001", "Test", 3.0, 4, "dispenser"));
        service.saveInventory(products);

        List<Product> loaded = service.loadInventory();
        assertFalse(loaded.isEmpty());
        assertEquals("P001", loaded.get(0).getProductId());
    }

    @Test
    @DisplayName("Save and load transactions")
    public void testSaveLoadTransactions() {
        PersistenceService service = new PersistenceService();
        service.saveTransaction("TX-1");
        List<String> loaded = service.loadTransactions();
        assertTrue(loaded.contains("TX-1"));
    }

    @Test
    @DisplayName("Save and load config")
    public void testSaveLoadConfig() {
        PersistenceService service = new PersistenceService();
        CentralRegistry registry = CentralRegistry.getInstance();
        registry.setSystemStatus("OPERATIONAL");
        registry.setEmergencyPurchaseLimit(2);
        service.saveConfig(registry);

        CentralRegistry other = CentralRegistry.getInstance();
        service.loadConfig(other);
        assertEquals("OPERATIONAL", other.getSystemStatus());
        assertEquals(2, other.getEmergencyPurchaseLimit());
    }
}
