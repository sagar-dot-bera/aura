package com.aura.core;

import java.util.Map;

/**
 * Interface defining core kiosk operations.
 */
public interface KioskInterface {
    /**
     * Purchase one or more items.
     * 
     * @param productId The product identifier
     * @param qty       The quantity to purchase
     * @return Receipt string with transaction details
     */
    String purchaseItem(String productId, int qty);

    /**
     * Refund a transaction.
     * 
     * @param txId The transaction ID to refund
     * @return true if refund succeeded, false otherwise
     */
    boolean refundTransaction(String txId);

    /**
     * Run system diagnostics.
     * 
     * @return Diagnostic report string
     */
    String runDiagnostics();

    /**
     * Restock inventory with new items.
     * 
     * @param items Map of productId to restocking quantity
     */
    void restockInventory(Map<String, Integer> items);
}
