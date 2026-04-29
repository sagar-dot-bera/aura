package com.aura.inventory;

import com.aura.event.EventBus;
import com.aura.event.LowStockEvent;
import com.aura.hardware.HardwareController;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Manages product inventory, reservations, and state snapshots.
 * Thread-safe using ConcurrentHashMap and ReentrantLock for product-level
 * synchronization.
 */
public class InventoryManager {
    private Map<String, Product> products;
    private Map<String, Integer> reservations; // txId -> qty reserved
    private Map<String, String> reservationProductMap; // txId -> productId
    private Map<String, ReentrantLock> productLocks; // per-product locks
    private Set<String> unavailableProducts;
    private Set<String> unavailableModules;
    private EventBus eventBus;
    private HardwareController hardwareController;
    private int lowStockThreshold;

    public InventoryManager() {
        this.products = new ConcurrentHashMap<>();
        this.reservations = new ConcurrentHashMap<>();
        this.reservationProductMap = new ConcurrentHashMap<>();
        this.productLocks = new ConcurrentHashMap<>();
        this.unavailableProducts = ConcurrentHashMap.newKeySet();
        this.unavailableModules = ConcurrentHashMap.newKeySet();
        this.lowStockThreshold = 3;
    }

    public void setEventBus(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    public void setHardwareController(HardwareController hardwareController) {
        this.hardwareController = hardwareController;
    }

    public void addProduct(Product product) {
        products.put(product.getProductId(), product);
        productLocks.put(product.getProductId(), new ReentrantLock());
    }

    public Product getProduct(String productId) {
        return products.get(productId);
    }

    public Map<String, Product> getAllProducts() {
        return new HashMap<>(products);
    }

    public boolean reserveItem(String productId, int quantity, String transactionId) {
        Product product = products.get(productId);
        if (product == null) {
            return false;
        }
        ReentrantLock lock = productLocks.get(productId);
        if (lock == null) {
            return false;
        }
        lock.lock();
        try {
            int available = getAvailableStock(productId);
            if (available < quantity) {
                return false;
            }
            reservations.put(transactionId, quantity);
            reservationProductMap.put(transactionId, productId);
            return true;
        } finally {
            lock.unlock();
        }
    }

    public void commitReservation(String transactionId) {
        if (!reservations.containsKey(transactionId)) {
            return;
        }
        int qty = reservations.remove(transactionId);
        String productId = reservationProductMap.remove(transactionId);
        if (productId != null) {
            Product product = products.get(productId);
            if (product != null) {
                product.decrementStock(qty);
                if (product.getStockCount() <= lowStockThreshold) {
                    publishLowStock(productId, product.getStockCount());
                }
            }
        }
    }

    public void rollbackReservation(String transactionId) {
        reservations.remove(transactionId);
        reservationProductMap.remove(transactionId);
    }

    public int getAvailableStock(String productId) {
        Product product = products.get(productId);
        if (product == null) {
            return 0;
        }
        if (unavailableProducts.contains(productId)) {
            return 0;
        }
        String moduleId = product.getRequiredHardwareModule();
        if (moduleId != null) {
            if (unavailableModules.contains(moduleId)) {
                return 0;
            }
            if (hardwareController != null && !hardwareController.isModuleAvailable(moduleId)) {
                return 0;
            }
        }
        int reserved = 0;
        for (Map.Entry<String, String> entry : reservationProductMap.entrySet()) {
            if (productId.equals(entry.getValue())) {
                String txId = entry.getKey();
                reserved += reservations.getOrDefault(txId, 0);
            }
        }
        return Math.max(0, product.getStockCount() - reserved);
    }

    public InventorySnapshot createSnapshot() {
        Map<String, Integer> currentState = new HashMap<>();
        for (Map.Entry<String, Product> entry : products.entrySet()) {
            currentState.put(entry.getKey(), entry.getValue().getStockCount());
        }
        Map<String, Integer> reservationState = new HashMap<>();
        for (Map.Entry<String, String> entry : reservationProductMap.entrySet()) {
            String productId = entry.getValue();
            reservationState.put(productId,
                    reservationState.getOrDefault(productId, 0) + reservations.getOrDefault(entry.getKey(), 0));
        }
        String txId = UUID.randomUUID().toString();
        return new InventorySnapshot(txId, currentState, reservationState);
    }

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
        reservations.clear();
        reservationProductMap.clear();
    }

    public void markUnavailable(String productId) {
        unavailableProducts.add(productId);
    }

    public void markAvailable(String productId) {
        unavailableProducts.remove(productId);
    }

    public void markModuleUnavailable(String moduleId) {
        unavailableModules.add(moduleId);
    }

    public void markModuleAvailable(String moduleId) {
        unavailableModules.remove(moduleId);
    }

    private void publishLowStock(String productId, int remainingStock) {
        if (eventBus != null) {
            eventBus.publish(new LowStockEvent(productId, remainingStock));
        }
    }
}
