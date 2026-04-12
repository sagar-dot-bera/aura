package com.aura.inventory;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Memento pattern: captures the state of inventory at a point in time.
 */
public class InventorySnapshot {
    private Map<String, Integer> stockState;
    private String transactionId;
    private LocalDateTime timestamp;

    public InventorySnapshot(String transactionId, Map<String, Integer> stockState) {
        this.transactionId = transactionId;
        // Create a deep copy to ensure immutability
        this.stockState = new HashMap<>(stockState);
        this.timestamp = LocalDateTime.now();
    }

    public String getTransactionId() {
        return transactionId;
    }

    public Map<String, Integer> getState() {
        return new HashMap<>(stockState);
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "InventorySnapshot{" +
                "transactionId='" + transactionId + '\'' +
                ", timestamp=" + timestamp +
                ", stateSize=" + stockState.size() +
                '}';
    }
}
