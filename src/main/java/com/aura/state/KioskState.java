package com.aura.state;

/**
 * State Pattern: Defines the interface for different kiosk operational modes.
 * The behavior of the kiosk changes based on its current state.
 */
public interface KioskState {
    /**
     * Get the name of this state.
     */
    String getName();

    /**
     * Check if purchase is allowed in this state.
     */
    boolean canPurchase(String productId, int quantity);

    /**
     * Check if refund is allowed in this state.
     */
    boolean canRefund();

    /**
     * Check if restock is allowed in this state.
     */
    boolean canRestock();

    /**
     * Handle a user interaction in this state.
     */
    String handleInteraction();
}
