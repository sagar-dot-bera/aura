package com.aura;

import com.aura.command.PurchaseItemCommand;
import com.aura.event.EventBus;
import com.aura.failure.FailureHandler;
import com.aura.failure.RecalibrationHandler;
import com.aura.failure.RetryHandler;
import com.aura.failure.TechnicianAlertHandler;
import com.aura.hardware.HardwareController;
import com.aura.inventory.InventoryManager;
import com.aura.inventory.Product;
import com.aura.payment.CardPaymentProcessor;
import com.aura.persistence.PersistenceService;
import com.aura.pricing.StandardPricingStrategy;
import com.aura.verification.NoVerificationModule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Inventory Concurrency Tests")
public class InventoryConcurrencyTest {
    @Test
    @DisplayName("Two threads competing for one stock")
    public void testConcurrentPurchases() throws InterruptedException {
        InventoryManager inventoryManager = new InventoryManager();
        inventoryManager.addProduct(new Product("C001", "TestItem", 5.0, 1, "dispenser"));

        HardwareController hardwareController = new HardwareController();
        hardwareController.initializeModule("dispenser", true);

        EventBus eventBus = new EventBus();
        PersistenceService persistenceService = new PersistenceService();
        FailureHandler failureChain = buildFailureChain();

        CountDownLatch latch = new CountDownLatch(2);
        AtomicInteger successCount = new AtomicInteger(0);

        Runnable task = () -> {
            try {
                PurchaseItemCommand cmd = new PurchaseItemCommand("C001", 1,
                        inventoryManager, hardwareController, new StandardPricingStrategy(),
                        new CardPaymentProcessor(), new NoVerificationModule(),
                        eventBus, persistenceService, failureChain);
                cmd.execute();
                successCount.incrementAndGet();
            } catch (RuntimeException ignored) {
                // ignore failures
            } finally {
                latch.countDown();
            }
        };

        new Thread(task).start();
        new Thread(task).start();
        latch.await();

        assertEquals(1, successCount.get());
        assertEquals(0, inventoryManager.getAvailableStock("C001"));
    }

    private FailureHandler buildFailureChain() {
        FailureHandler retry = new RetryHandler();
        FailureHandler recalibrate = new RecalibrationHandler();
        FailureHandler technician = new TechnicianAlertHandler();
        retry.setNext(recalibrate).setNext(technician);
        return retry;
    }
}
