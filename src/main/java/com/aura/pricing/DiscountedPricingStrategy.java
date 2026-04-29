package com.aura.pricing;

import com.aura.inventory.Product;

/**
 * Strategy Pattern: Discounted pricing strategy.
 * Applies a percentage discount to the base price.
 */
public class DiscountedPricingStrategy implements PricingStrategy {
    private double discountRate; // e.g., 0.10 for 10% off

    public DiscountedPricingStrategy(double discountRate) {
        this.discountRate = discountRate;
    }

    public double getDiscountRate() {
        return discountRate;
    }

    public void setDiscountRate(double discountRate) {
        this.discountRate = discountRate;
    }

    @Override
    public double computePrice(Product product, int quantity) {
        double discountedUnitPrice = product.getBasePrice() * (1.0 - discountRate);
        return discountedUnitPrice * quantity;
    }

    @Override
    public double computePrice(Product product) {
        return product.getBasePrice() * (1.0 - discountRate);
    }

    @Override
    public String getPolicyName() {
        return "DISCOUNTED (" + (int) (discountRate * 100) + "% off)";
    }
}
