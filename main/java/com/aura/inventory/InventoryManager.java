package com.aura.inventory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Manages product inventory, reservations, and state snapshots.
 */
public class InventoryManager {
    private Map<String, Product> products;
    private Map<String, Integer> reservations; // txId -> qty reserved
    private Map<String, String> reservationProductMap; // txId -> productId

    public InventoryManager() {
        this.products = new HashMap<>();
        this.reservations = new HashMap<>();
        this.reservationProductMap = new HashMap<>();
    }

    public void addProduct(Product product) {
        products.put(product.getProductId(), product);
    }

    public Product getProduct(String productId) {
        return products.get(productId);
    }

    /**
     * Reserve an item for purchase.
     * 
     * @param productId The product to reserve
     * @param qty       The quantity to reserve
     * @return true if reservation succeeded, false if insufficient stock
     */
    public boolean reserveItem(String productId, int qty) {
        Product product = products.get(productId);
        if (product == null || product.getStockCount() < qty) {
            return false;
        }
        String txId = UUID.randomUUID().toString();
        reservations.put(txId, qty);
        reservationProductMap.put(txId, productId);
        return true;
    }

    /**
     * Get the transaction ID for the most recent reservation.
     * This is a helper method for the workflow.
     */
    public String getLastReservationTxId() {
        if (reservations.isEmpty()) {
            return null;
        }
        // Return the last key (note: real implementation might need better tracking)
        return reservations.keySet().stream().findFirst().orElse(null);
    }

    /**
     * Commit a reservation (deduct from stock).
     * 
     * @param txId The transaction ID to commit
     */
    public void commitReservation(String txId) {
        if (!reservations.containsKey(txId)) {
            return;
        }
        int qty = reservations.remove(txId);
        String productId = reservationProductMap.remove(txId);
        if (productId != null) {
            Product product = products.get(productId);
            if (product != null) {
                product.decrementStock(qty);
            }
        }
    }

    /**
     * Rollback a reservation (cancel without deducting stock).
     * 
     * @param txId The transaction ID to rollback
     */
    public void rollbackReservation(String txId) {
        reservations.remove(txId);
        reservationProductMap.remove(txId);
    }

    /**
     * Get available stock for a product (counting current stock).
     * 
     * @param productId The product ID
     * @return Available stock count
     */
    public int getAvailableStock(String productId) {
        Product product = products.get(productId);
        return product != null ? product.getStockCount() : 0;
    }

    /**
     * Create a snapshot of the current inventory state.
     * 
     * @return InventorySnapshot with current stock state
     */
    public InventorySnapshot createSnapshot() {
        Map<String, Integer> currentState = new HashMap<>();
        for (Map.Entry<String, Product> entry : products.entrySet()) {
            currentState.put(entry.getKey(), entry.getValue().getStockCount());
        }
        String txId = UUID.randomUUID().toString();
        return new InventorySnapshot(txId, currentState);
    }

    /**
     * Restore inventory to a previous snapshot state.
     * 
     * @param snapshot The snapshot to restore from
     */
    public void restoreSnapshot(InventorySnapshot snapshot) {
        if (snapshot == null) {
            return;
        }
        Map<String, Integer> state = snapshot.getState();
        for (Map.Entry<String, Integer> entry : state.entrySet()) {
            Product product = products.get(entry.getKey());
            if (product != null) {
                product.setStockCount(entry.getValue());
            }
        }
    }

    public Map<String, Product> getAllProducts() {
        return new HashMap<>(products);
    }
}
