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
import com.aura.payment.PaymentProcessor;
import com.aura.persistence.PersistenceService;
import com.aura.pricing.StandardPricingStrategy;
import com.aura.verification.NoVerificationModule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Payment Flow Tests")
public class PaymentFlowTest {
    @Test
    @DisplayName("Payment failure does not reduce stock")
    public void testPaymentFailureDoesNotReduceStock() {
        InventoryManager inventoryManager = new InventoryManager();
        inventoryManager.addProduct(new Product("P001", "TestItem", 5.0, 5, "dispenser"));
        HardwareController hardwareController = new HardwareController();
        hardwareController.initializeModule("dispenser", true);

        PaymentProcessor failingPayment = new PaymentProcessor() {
            @Override
            public boolean processPayment(String transactionId, double amount) {
                return false;
            }

            @Override
            public boolean refund(String transactionId, double amount) {
                return true;
            }

            @Override
            public String getProviderName() {
                return "FAIL";
            }
        };

        PurchaseItemCommand cmd = new PurchaseItemCommand("P001", 1,
                inventoryManager, hardwareController, new StandardPricingStrategy(),
                failingPayment, new NoVerificationModule(),
                new EventBus(), new PersistenceService(), buildFailureChain());

        assertThrows(RuntimeException.class, cmd::execute);
        assertEquals(5, inventoryManager.getAvailableStock("P001"));
    }

    @Test
    @DisplayName("Dispense failure triggers refund")
    public void testDispenseFailureTriggersRefund() {
        InventoryManager inventoryManager = new InventoryManager();
        inventoryManager.addProduct(new Product("P002", "TestItem", 5.0, 5, "dispenser"));
        HardwareController hardwareController = new HardwareController();
        hardwareController.initializeModule("dispenser", true);
        hardwareController.failNextDispense();
        hardwareController.failNextRecalibration();

        TrackingPaymentProcessor paymentProcessor = new TrackingPaymentProcessor();

        PurchaseItemCommand cmd = new PurchaseItemCommand("P002", 1,
                inventoryManager, hardwareController, new StandardPricingStrategy(),
                paymentProcessor, new NoVerificationModule(),
                new EventBus(), new PersistenceService(), buildFailureChain());

        assertThrows(RuntimeException.class, cmd::execute);
        assertTrue(paymentProcessor.refundCalled);
        assertEquals(5, inventoryManager.getAvailableStock("P002"));
    }

    private FailureHandler buildFailureChain() {
        FailureHandler retry = new RetryHandler();
        FailureHandler recalibrate = new RecalibrationHandler();
        FailureHandler technician = new TechnicianAlertHandler();
        retry.setNext(recalibrate).setNext(technician);
        return retry;
    }

    private static class TrackingPaymentProcessor implements PaymentProcessor {
        private boolean refundCalled = false;

        @Override
        public boolean processPayment(String transactionId, double amount) {
            return true;
        }

        @Override
        public boolean refund(String transactionId, double amount) {
            refundCalled = true;
            return true;
        }

        @Override
        public String getProviderName() {
            return "TRACK";
        }
    }
}
