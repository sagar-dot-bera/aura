package com.aura.command;

import com.aura.event.EventBus;
import com.aura.event.HardwareFailureEvent;
import com.aura.event.TransactionCompletedEvent;
import com.aura.event.TransactionFailedEvent;
import com.aura.failure.FailureContext;
import com.aura.failure.FailureHandler;
import com.aura.hardware.HardwareController;
import com.aura.inventory.InventoryManager;
import com.aura.inventory.InventorySnapshot;
import com.aura.inventory.Product;
import com.aura.payment.PaymentProcessor;
import com.aura.persistence.PersistenceService;
import com.aura.pricing.PricingStrategy;
import com.aura.verification.VerificationModule;

import java.util.UUID;

/**
 * Command Pattern: operation object for purchase transactions.
 * Memento Pattern: snapshot used for rollback.
 */
public class PurchaseItemCommand implements Command {
    private final String productId;
    private final int quantity;
    private final InventoryManager inventoryManager;
    private final HardwareController hardwareController;
    private final PricingStrategy pricingStrategy;
    private final PaymentProcessor paymentProcessor;
    private final VerificationModule verificationModule;
    private final EventBus eventBus;
    private final PersistenceService persistenceService;
    private final FailureHandler failureChain;
    // Memento Pattern: snapshot used for rollback
    private InventorySnapshot snapshot;
    private final String transactionId;
    private boolean paymentProcessed;

    public PurchaseItemCommand(String productId, int quantity,
            InventoryManager inventoryManager,
            HardwareController hardwareController,
            PricingStrategy pricingStrategy,
            PaymentProcessor paymentProcessor,
            VerificationModule verificationModule,
            EventBus eventBus,
            PersistenceService persistenceService,
            FailureHandler failureChain) {
        this.productId = productId;
        this.quantity = quantity;
        this.inventoryManager = inventoryManager;
        this.hardwareController = hardwareController;
        this.pricingStrategy = pricingStrategy;
        this.paymentProcessor = paymentProcessor;
        this.verificationModule = verificationModule;
        this.eventBus = eventBus;
        this.persistenceService = persistenceService;
        this.failureChain = failureChain;
        this.transactionId = UUID.randomUUID().toString();
        this.paymentProcessed = false;
    }

    @Override
    public void execute() {
        // Command Pattern: operation object
        snapshot = inventoryManager.createSnapshot();

        if (!inventoryManager.reserveItem(productId, quantity, transactionId)) {
            publishFailure("Insufficient stock");
            persistenceService.saveTransaction(log());
            throw new RuntimeException("Out of stock for product: " + productId);
        }

        if (!verificationModule.verify(productId, quantity)) {
            inventoryManager.rollbackReservation(transactionId);
            inventoryManager.restoreSnapshot(snapshot);
            publishFailure("Verification failed");
            persistenceService.saveTransaction(log());
            throw new RuntimeException("Verification failed for product: " + productId);
        }

        Product product = inventoryManager.getProduct(productId);
        double totalCost = pricingStrategy.computePrice(product, quantity);
        if (!paymentProcessor.processPayment(transactionId, totalCost)) {
            inventoryManager.rollbackReservation(transactionId);
            inventoryManager.restoreSnapshot(snapshot);
            publishFailure("Payment failed");
            persistenceService.saveTransaction(log());
            throw new RuntimeException("Payment failed for product: " + productId);
        }
        paymentProcessed = true;

        boolean dispensed = hardwareController.dispense(productId);
        if (!dispensed) {
            publishHardwareFailure();
            boolean recovered = attemptFailureRecovery();
            if (recovered) {
                dispensed = hardwareController.dispense(productId);
            }
        }

        if (!dispensed) {
            if (paymentProcessed) {
                paymentProcessor.refund(transactionId, totalCost);
            }
            inventoryManager.rollbackReservation(transactionId);
            inventoryManager.restoreSnapshot(snapshot);
            publishFailure("Dispense failed");
            persistenceService.saveTransaction(log());
            throw new RuntimeException("Hardware dispense failed for product: " + productId);
        }

        inventoryManager.commitReservation(transactionId);
        publishSuccess(totalCost);
        persistenceService.saveTransaction(log());
    }

    private void publishSuccess(double amount) {
        if (eventBus != null) {
            eventBus.publish(new TransactionCompletedEvent(transactionId, productId, quantity, amount));
        }
    }

    private void publishFailure(String reason) {
        if (eventBus != null) {
            eventBus.publish(new TransactionFailedEvent(transactionId, productId, quantity, reason));
        }
    }

    private void publishHardwareFailure() {
        if (eventBus != null) {
            eventBus.publish(new HardwareFailureEvent("dispenser", productId, "DISPENSE_FAILURE"));
        }
    }

    private boolean attemptFailureRecovery() {
        if (failureChain == null) {
            return false;
        }
        FailureContext context = new FailureContext("DISPENSE_FAILURE", "dispenser", productId);
        context.setRecalibrationSucceeded(hardwareController.recalibrate("dispenser"));
        return failureChain.handle(context);
    }

    @Override
    public void undo() {
        if (snapshot != null) {
            inventoryManager.restoreSnapshot(snapshot);
        }
    }

    @Override
    public String log() {
        Product product = inventoryManager.getProduct(productId);
        String productName = product != null ? product.getName() : productId;
        double unitPrice = product != null ? pricingStrategy.computePrice(product) : 0.0;
        return "PURCHASE[" + transactionId + "] productId=" + productId
                + " name=" + productName
                + " qty=" + quantity
                + " unitPrice=" + unitPrice
                + " total=" + (unitPrice * quantity)
                + " provider=" + paymentProcessor.getProviderName();
    }

    public String getTransactionId() {
        return transactionId;
    }
}
