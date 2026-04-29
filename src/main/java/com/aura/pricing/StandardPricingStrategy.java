package com.aura.pricing;

import com.aura.inventory.Product;

/**
 * Strategy Pattern: Standard pricing strategy.
 * Charges the full base price without any discounts.
 */
public class StandardPricingStrategy implements PricingStrategy {

    @Override
    public double computePrice(Product product, int quantity) {
        return product.getBasePrice() * quantity;
    }

    @Override
    public double computePrice(Product product) {
        return product.getBasePrice();
    }

    @Override
    public String getPolicyName() {
        return "STANDARD";
    }
}
