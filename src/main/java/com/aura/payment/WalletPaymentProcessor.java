package com.aura.payment;

public class WalletPaymentProcessor implements PaymentProcessor {
    @Override
    public boolean processPayment(String transactionId, double amount) {
        System.out.println("[Payment] Wallet payment processed for " + transactionId + " amount: " + amount);
        return true;
    }

    @Override
    public boolean refund(String transactionId, double amount) {
        System.out.println("[Payment] Wallet refund processed for " + transactionId + " amount: " + amount);
        return true;
    }

    @Override
    public String getProviderName() {
        return "WALLET";
    }
}
