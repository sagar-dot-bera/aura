package com.aura.payment;

public interface PaymentProcessor {
    boolean processPayment(String transactionId, double amount);

    boolean refund(String transactionId, double amount);

    String getProviderName();
}
