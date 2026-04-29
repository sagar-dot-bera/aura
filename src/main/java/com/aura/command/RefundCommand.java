package com.aura.command;

import com.aura.inventory.InventoryManager;
import com.aura.inventory.Product;
import com.aura.persistence.PersistenceService;

/**
 * Command Pattern: Refund a transaction by returning items to inventory.
 * Memento Pattern: Uses snapshot if available to restore state.
 */
public class RefundCommand implements Command {
    private String transactionId;
    private String productId;
    private int quantity;
    private InventoryManager inventoryManager;
    private PersistenceService persistenceService;

    public RefundCommand(String transactionId, String productId, int quantity,
            InventoryManager inventoryManager, PersistenceService persistenceService) {
        this.transactionId = transactionId;
        this.productId = productId;
        this.quantity = quantity;
        this.inventoryManager = inventoryManager;
        this.persistenceService = persistenceService;
    }

    @Override
    public void execute() {
        // Add items back to inventory
        Product product = inventoryManager.getProduct(productId);
        if (product != null) {
            product.incrementStock(quantity);
            System.out.println("[REFUND] Returned " + quantity + " units of " + productId + " to inventory");
            if (persistenceService != null) {
                persistenceService.saveTransaction(log());
            }
        } else {
            throw new RuntimeException("Product not found for refund: " + productId);
        }
    }

    @Override
    public void undo() {
        // Reverse the refund by removing items again
        Product product = inventoryManager.getProduct(productId);
        if (product != null) {
            product.decrementStock(quantity);
        }
    }

    @Override
    public String log() {
        return "REFUND[" + transactionId + "] productId=" + productId + " qty=" + quantity;
    }
}
