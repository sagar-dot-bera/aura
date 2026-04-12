package com.aura.pricing;

import com.aura.inventory.Product;

/**
 * Standard pricing strategy: charges the full base price.
 */
public class StandardPricingStrategy implements PricingStrategy {

    @Override
    public double computePrice(Product product) {
        return product.getBasePrice() * 1.0;
    }

    @Override
    public String getPolicyName() {
        return "STANDARD";
    }
}
