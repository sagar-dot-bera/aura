package com.aura.state;

import com.aura.registry.CentralRegistry;

/**
 * State Pattern: Emergency Lockdown state.
 * The kiosk operates in emergency mode with restricted access.
 * Only essential products up to the emergency purchase limit can be purchased.
 */
public class EmergencyLockdownState implements KioskState {
    public EmergencyLockdownState() {
    }

    @Override
    public String getName() {
        return "EMERGENCY_LOCKDOWN";
    }

    /**
     * Purchase allowed only for essential products within emergency limit.
     */
    @Override
    public boolean canPurchase(String productId, int quantity) {
        CentralRegistry registry = CentralRegistry.getInstance();

        // Check if product is essential
        if (!registry.isEssentialProduct(productId)) {
            System.out.println("[EMERGENCY] Non-essential product rejected: " + productId);
            return false;
        }

        // Check if quantity exceeds emergency limit
        if (quantity > registry.getEmergencyPurchaseLimit()) {
            System.out.println("[EMERGENCY] Purchase quantity " + quantity
                    + " exceeds limit " + registry.getEmergencyPurchaseLimit());
            return false;
        }

        return true;
    }

    @Override
    public boolean canRefund() {
        // Allow refunds in emergency
        return true;
    }

    @Override
    public boolean canRestock() {
        // Allow restocking in emergency
        return true;
    }

    @Override
    public String handleInteraction() {
        CentralRegistry registry = CentralRegistry.getInstance();
        return "Kiosk is in EMERGENCY_LOCKDOWN. Only essential products up to "
                + registry.getEmergencyPurchaseLimit() + " units allowed per transaction.";
    }
}
