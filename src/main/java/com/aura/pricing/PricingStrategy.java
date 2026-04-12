package com.aura.pricing;

import com.aura.inventory.Product;

/**
 * Strategy pattern: defines different pricing policies.
 */
public interface PricingStrategy {
    /**
     * Compute the final price for a product.
     * 
     * @param product The product to price
     * @return The computed price
     */
    double computePrice(Product product);

    /**
     * Get the name/description of this pricing policy.
     * 
     * @return Policy name
     */
    String getPolicyName();
}
