package com.aura.state;

/**
 * State Pattern: Active operational state.
 * The kiosk is fully operational and allows all transactions.
 */
public class ActiveState implements KioskState {

    @Override
    public String getName() {
        return "ACTIVE";
    }

    @Override
    public boolean canPurchase(String productId, int quantity) {
        // Allow all purchases in active state
        return true;
    }

    @Override
    public boolean canRefund() {
        // Allow refunds in active state
        return true;
    }

    @Override
    public boolean canRestock() {
        // Allow restocking in active state
        return true;
    }

    @Override
    public String handleInteraction() {
        return "Kiosk is ACTIVE. All operations allowed.";
    }
}
