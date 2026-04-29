package com.aura.command;

import com.aura.inventory.InventoryManager;
import com.aura.inventory.Product;
import com.aura.persistence.PersistenceService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Command Pattern: Restock inventory by adding items.
 * Memento Pattern: Captures state before and after for rollback.
 */
public class RestockCommand implements Command {
    private String transactionId;
    private Map<String, Integer> items;
    private InventoryManager inventoryManager;
    private Map<String, Integer> previousStock;
    private PersistenceService persistenceService;

    public RestockCommand(Map<String, Integer> items, InventoryManager inventoryManager,
            PersistenceService persistenceService) {
        this.items = new HashMap<>(items);
        this.inventoryManager = inventoryManager;
        this.transactionId = UUID.randomUUID().toString();
        this.previousStock = new HashMap<>();
        this.persistenceService = persistenceService;
    }

    @Override
    public void execute() {
        // Capture previous stock before restocking
        for (String productId : items.keySet()) {
            Product product = inventoryManager.getProduct(productId);
            if (product != null) {
                previousStock.put(productId, product.getStockCount());
                int restockQty = items.get(productId);
                product.incrementStock(restockQty);
                System.out.println("[RESTOCK] Added " + restockQty + " units to " + productId + ". New stock: "
                        + product.getStockCount());
                if (persistenceService != null) {
                    persistenceService.saveTransaction(log());
                }
            } else {
                throw new RuntimeException("Product not found for restocking: " + productId);
            }
        }
    }

    @Override
    public void undo() {
        // Restore previous stock levels
        for (String productId : previousStock.keySet()) {
            Product product = inventoryManager.getProduct(productId);
            if (product != null) {
                product.setStockCount(previousStock.get(productId));
            }
        }
    }

    @Override
    public String log() {
        return "RESTOCK[" + transactionId + "] items=" + items;
    }
}
