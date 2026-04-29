package com.aura.state;

/**
 * State Pattern: Maintenance state.
 * The kiosk is under maintenance. Purchases and refunds are blocked, but
 * diagnostics and restocking are allowed.
 */
public class MaintenanceState implements KioskState {

    @Override
    public String getName() {
        return "MAINTENANCE";
    }

    @Override
    public boolean canPurchase(String productId, int quantity) {
        // Block purchases during maintenance
        return false;
    }

    @Override
    public boolean canRefund() {
        // Block refunds during maintenance
        return false;
    }

    @Override
    public boolean canRestock() {
        // Allow restocking during maintenance
        return true;
    }

    @Override
    public String handleInteraction() {
        return "Kiosk is under MAINTENANCE. Purchases and refunds are blocked. Restocking and diagnostics allowed.";
    }
}
