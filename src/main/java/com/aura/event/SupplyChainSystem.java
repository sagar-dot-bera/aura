package com.aura.event;

public class SupplyChainSystem implements EventListener {
    @Override
    public void onEvent(SystemEvent event) {
        if (event instanceof LowStockEvent) {
            LowStockEvent lowStock = (LowStockEvent) event;
            System.out.println("[SupplyChainSystem] Low stock alert for " + lowStock.getProductId()
                    + ": remaining " + lowStock.getRemainingStock());
        }
    }
}
