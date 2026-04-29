package com.aura.event;

import java.time.LocalDateTime;

public class TransactionFailedEvent implements SystemEvent {
    private final String transactionId;
    private final String productId;
    private final int quantity;
    private final String reason;
    private final LocalDateTime timestamp;

    public TransactionFailedEvent(String transactionId, String productId, int quantity, String reason) {
        this.transactionId = transactionId;
        this.productId = productId;
        this.quantity = quantity;
        this.reason = reason;
        this.timestamp = LocalDateTime.now();
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getProductId() {
        return productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getReason() {
        return reason;
    }

    @Override
    public String getType() {
        return "TRANSACTION_FAILED";
    }

    @Override
    public int getPriority() {
        return 6;
    }

    @Override
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
