package com.aura.pricing;

import com.aura.inventory.Product;

/**
 * Emergency pricing strategy: no markup, base price only.
 */
public class EmergencyPricingStrategy implements PricingStrategy {

    @Override
    public double computePrice(Product product) {
        return product.getBasePrice();
    }

    @Override
    public String getPolicyName() {
        return "EMERGENCY";
    }
}
