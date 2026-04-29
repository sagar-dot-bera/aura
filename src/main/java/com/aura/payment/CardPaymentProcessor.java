package com.aura.payment;

public class CardPaymentProcessor implements PaymentProcessor {
    @Override
    public boolean processPayment(String transactionId, double amount) {
        System.out.println("[Payment] Card payment processed for " + transactionId + " amount: " + amount);
        return true;
    }

    @Override
    public boolean refund(String transactionId, double amount) {
        System.out.println("[Payment] Card refund processed for " + transactionId + " amount: " + amount);
        return true;
    }

    @Override
    public String getProviderName() {
        return "CARD";
    }
}
