package com.aura.state;

/**
 * State Pattern: Power Saving state.
 * The kiosk operates in reduced power mode. Purchases are allowed with a
 * warning.
 */
public class PowerSavingState implements KioskState {

    @Override
    public String getName() {
        return "POWER_SAVING";
    }

    @Override
    public boolean canPurchase(String productId, int quantity) {
        // Allow purchases but caller should print warning
        return true;
    }

    @Override
    public boolean canRefund() {
        // Allow refunds
        return true;
    }

    @Override
    public boolean canRestock() {
        // Allow restocking
        return true;
    }

    @Override
    public String handleInteraction() {
        return "Kiosk is in POWER_SAVING mode. Operations may be slower. All transactions allowed.";
    }
}
