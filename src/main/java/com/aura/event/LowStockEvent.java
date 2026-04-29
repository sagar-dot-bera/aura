package com.aura.event;

import java.time.LocalDateTime;

public class LowStockEvent implements SystemEvent {
    private final String productId;
    private final int remainingStock;
    private final LocalDateTime timestamp;

    public LowStockEvent(String productId, int remainingStock) {
        this.productId = productId;
        this.remainingStock = remainingStock;
        this.timestamp = LocalDateTime.now();
    }

    public String getProductId() {
        return productId;
    }

    public int getRemainingStock() {
        return remainingStock;
    }

    @Override
    public String getType() {
        return "LOW_STOCK";
    }

    @Override
    public int getPriority() {
        return 3;
    }

    @Override
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
