package com.aura.inventory;

/**
 * Represents a product in the kiosk inventory.
 */
public class Product {
    private String productId;
    private String name;
    private double basePrice;
    private int stockCount;
    private boolean available;

    public Product(String productId, String name, double basePrice, int stockCount) {
        this.productId = productId;
        this.name = name;
        this.basePrice = basePrice;
        this.stockCount = stockCount;
        this.available = stockCount > 0;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(double basePrice) {
        this.basePrice = basePrice;
    }

    public int getStockCount() {
        return stockCount;
    }

    public void setStockCount(int stockCount) {
        this.stockCount = stockCount;
        this.available = stockCount > 0;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public void decrementStock(int qty) {
        this.stockCount -= qty;
        this.available = this.stockCount > 0;
    }

    @Override
    public String toString() {
        return "Product{" +
                "productId='" + productId + '\'' +
                ", name='" + name + '\'' +
                ", basePrice=" + basePrice +
                ", stockCount=" + stockCount +
                ", available=" + available +
                '}';
    }
}
