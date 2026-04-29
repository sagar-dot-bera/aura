package com.aura.pricing;

import com.aura.inventory.Product;

/**
 * Strategy Pattern: defines different pricing policies.
 * Pricing can be computed differently based on the strategy.
 */
public interface PricingStrategy {
    /**
     * Compute the final price for a product.
     * 
     * @param product  The product to price
     * @param quantity The quantity (for bulk discount calculations if needed)
     * @return The total computed price for the quantity
     */
    double computePrice(Product product, int quantity);

    /**
     * Compute the unit price for a single unit.
     * 
     * @param product The product to price
     * @return The computed unit price
     */
    double computePrice(Product product);

    /**
     * Get the name/description of this pricing policy.
     * 
     * @return Policy name
     */
    String getPolicyName();
}
