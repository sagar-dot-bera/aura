package com.aura;

import com.aura.command.PurchaseItemCommand;
import com.aura.hardware.HardwareController;
import com.aura.inventory.InventoryManager;
import com.aura.inventory.Product;
import com.aura.event.EventBus;
import com.aura.failure.FailureHandler;
import com.aura.failure.RecalibrationHandler;
import com.aura.failure.RetryHandler;
import com.aura.failure.TechnicianAlertHandler;
import com.aura.payment.CardPaymentProcessor;
import com.aura.persistence.PersistenceService;
import com.aura.pricing.StandardPricingStrategy;
import com.aura.verification.NoVerificationModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test that the Command + Memento pattern provides atomic rollback on failure.
 */
@DisplayName("Command Rollback Tests")
public class CommandRollbackTest {
    private InventoryManager inventoryManager;
    private HardwareController hardwareController;
    private StandardPricingStrategy pricingStrategy;
    private EventBus eventBus;
    private PersistenceService persistenceService;
    private FailureHandler failureChain;

    @BeforeEach
    public void setUp() {
        inventoryManager = new InventoryManager();
        hardwareController = new HardwareController();
        pricingStrategy = new StandardPricingStrategy();
        eventBus = new EventBus();
        persistenceService = new PersistenceService();
        failureChain = buildFailureChain();

        // Set up initial inventory
        Product product = new Product("P001", "Paracetamol", 5.0, 10);
        inventoryManager.addProduct(product);

        // Initialize hardware as healthy
        hardwareController.initializeModule("dispenser", true);
    }

    @Test
    @DisplayName("Successful purchase reduces stock correctly")
    public void testSuccessfulPurchase() {
        int initialStock = inventoryManager.getAvailableStock("P001");
        assertEquals(10, initialStock);

        // Execute purchase command
        PurchaseItemCommand cmd = new PurchaseItemCommand("P001", 2, inventoryManager,
                hardwareController, pricingStrategy, new CardPaymentProcessor(),
                new NoVerificationModule(), eventBus, persistenceService, failureChain);
        cmd.execute();

        int stockAfter = inventoryManager.getAvailableStock("P001");
        assertEquals(8, stockAfter);
    }

    @Test
    @DisplayName("Failed purchase (hardware failure) rolls back inventory")
    public void testFailedPurchaseRollback() {
        int stockBefore = inventoryManager.getAvailableStock("P001");
        assertEquals(10, stockBefore);

        // First successful purchase to change the state
        PurchaseItemCommand cmd1 = new PurchaseItemCommand("P001", 2, inventoryManager,
                hardwareController, pricingStrategy, new CardPaymentProcessor(),
                new NoVerificationModule(), eventBus, persistenceService, failureChain);
        cmd1.execute();

        int stockAfterFirst = inventoryManager.getAvailableStock("P001");
        assertEquals(8, stockAfterFirst);

        // Simulate hardware failure
        hardwareController.simulateFailure("dispenser");
        hardwareController.failNextRecalibration();

        // Try to purchase when hardware is failed - should throw exception
        PurchaseItemCommand cmd2 = new PurchaseItemCommand("P001", 3, inventoryManager,
                hardwareController, pricingStrategy, new CardPaymentProcessor(),
                new NoVerificationModule(), eventBus, persistenceService, failureChain);

        // Expecting RuntimeException due to hardware failure
        RuntimeException exception = assertThrows(RuntimeException.class, cmd2::execute);
        assertTrue(exception.getMessage().contains("Hardware dispense failed"));

        // Stock should remain unchanged (rollback happened)
        int stockAfterFailed = inventoryManager.getAvailableStock("P001");
        assertEquals(8, stockAfterFailed);
    }

    @Test
    @DisplayName("Out of stock purchase throws exception and does not modify inventory")
    public void testOutOfStockRollback() {
        int initialStock = inventoryManager.getAvailableStock("P001");
        assertEquals(10, initialStock);

        // Try to purchase more than available
        PurchaseItemCommand cmd = new PurchaseItemCommand("P001", 20, inventoryManager,
                hardwareController, pricingStrategy, new CardPaymentProcessor(),
                new NoVerificationModule(), eventBus, persistenceService, failureChain);

        assertThrows(RuntimeException.class, cmd::execute);

        // Stock should not have changed
        int stockAfter = inventoryManager.getAvailableStock("P001");
        assertEquals(10, stockAfter);
    }

    private FailureHandler buildFailureChain() {
        FailureHandler retry = new RetryHandler();
        FailureHandler recalibrate = new RecalibrationHandler();
        FailureHandler technician = new TechnicianAlertHandler();
        retry.setNext(recalibrate).setNext(technician);
        return retry;
    }
}
