package com.aura.event;

import java.time.LocalDateTime;

public class TransactionCompletedEvent implements SystemEvent {
    private final String transactionId;
    private final String productId;
    private final int quantity;
    private final double amount;
    private final LocalDateTime timestamp;

    public TransactionCompletedEvent(String transactionId, String productId, int quantity, double amount) {
        this.transactionId = transactionId;
        this.productId = productId;
        this.quantity = quantity;
        this.amount = amount;
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

    public double getAmount() {
        return amount;
    }

    @Override
    public String getType() {
        return "TRANSACTION_COMPLETED";
    }

    @Override
    public int getPriority() {
        return 5;
    }

    @Override
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
