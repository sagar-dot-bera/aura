package com.aura.pricing;

import com.aura.inventory.Product;

/**
 * Strategy Pattern: Emergency pricing strategy.
 * Fair pricing with no markup during emergencies - capped at base price.
 */
public class EmergencyPricingStrategy implements PricingStrategy {

    @Override
    public double computePrice(Product product, int quantity) {
        // During emergency, provide fair pricing with no unfair markup
        return product.getBasePrice() * quantity;
    }

    @Override
    public double computePrice(Product product) {
        return product.getBasePrice();
    }

    @Override
    public String getPolicyName() {
        return "EMERGENCY (Fair pricing, no markup)";
    }
}
