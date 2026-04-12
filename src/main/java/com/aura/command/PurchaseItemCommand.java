package com.aura.command;

import com.aura.hardware.HardwareController;
import com.aura.inventory.InventoryManager;
import com.aura.inventory.InventorySnapshot;
import com.aura.inventory.Product;
import com.aura.pricing.PricingStrategy;

import java.util.UUID;

/**
 * Command pattern: encapsulates a purchase request with rollback capability.
 * Uses Memento pattern (InventorySnapshot) for atomicity.
 */
public class PurchaseItemCommand implements Command {
    private String productId;
    private int qty;
    private InventoryManager inventoryMgr;
    private HardwareController hardware;
    private PricingStrategy pricing;
    private InventorySnapshot snapshot;
    private String transactionId;

    public PurchaseItemCommand(String productId, int qty, InventoryManager inventoryMgr,
            HardwareController hardware, PricingStrategy pricing) {
        this.productId = productId;
        this.qty = qty;
        this.inventoryMgr = inventoryMgr;
        this.hardware = hardware;
        this.pricing = pricing;
        this.transactionId = UUID.randomUUID().toString();
    }

    @Override
    public void execute() {
        // 1. Create a snapshot of current inventory state
        snapshot = inventoryMgr.createSnapshot();

        // 2. Try to reserve the items
        boolean reserved = inventoryMgr.reserveItem(productId, qty);
        if (!reserved) {
            throw new RuntimeException("Out of stock for product: " + productId);
        }

        // Get the transaction ID of the reservation
        // We need to track which transaction was created
        String txId = inventoryMgr.getLastReservationTxId();

        // 3. Try to dispense from hardware
        boolean dispensed = hardware.dispense(productId);
        if (!dispensed) {
            // Rollback the reservation if dispense fails
            if (txId != null) {
                inventoryMgr.rollbackReservation(txId);
            }
            throw new RuntimeException("Hardware dispense failed for product: " + productId);
        }

        // 4. Commit the reservation (deduct from stock)
        if (txId != null) {
            inventoryMgr.commitReservation(txId);
        }
    }

    @Override
    public void undo() {
        // Restore inventory to the snapshot state
        if (snapshot != null) {
            inventoryMgr.restoreSnapshot(snapshot);
        }
    }

    @Override
    public String log() {
        Product product = inventoryMgr.getProduct(productId);
        String productName = product != null ? product.getName() : productId;
        double price = product != null ? pricing.computePrice(product) : 0.0;
        return String.format("PURCHASE[%s] productId=%s qty=%d price=%.2f total=%.2f",
                transactionId, productId, qty, price, price * qty);
    }

    public String getTransactionId() {
        return transactionId;
    }
}
