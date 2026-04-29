package com.aura.payment;

public class UPIPaymentProcessor implements PaymentProcessor {
    @Override
    public boolean processPayment(String transactionId, double amount) {
        System.out.println("[Payment] UPI payment processed for " + transactionId + " amount: " + amount);
        return true;
    }

    @Override
    public boolean refund(String transactionId, double amount) {
        System.out.println("[Payment] UPI refund processed for " + transactionId + " amount: " + amount);
        return true;
    }

    @Override
    public String getProviderName() {
        return "UPI";
    }
}
