package com.aura.core;

import com.aura.command.Command;
import com.aura.hardware.HardwareController;
import com.aura.inventory.InventoryManager;
import com.aura.inventory.Product;
import com.aura.pricing.PricingStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Core kiosk controller implementing KioskInterface.
 * Manages state, pricing strategy, and command execution.
 */
public class KioskController implements KioskInterface {
    private String kioskId;
    private PricingStrategy pricingStrategy;
    private List<Command> commandHistory;
    private InventoryManager inventoryManager;
    private HardwareController hardwareController;
    private String state;

    public KioskController(String kioskId, InventoryManager inventoryManager,
            HardwareController hardwareController, PricingStrategy pricingStrategy) {
        this.kioskId = kioskId;
        this.inventoryManager = inventoryManager;
        this.hardwareController = hardwareController;
        this.pricingStrategy = pricingStrategy;
        this.commandHistory = new ArrayList<>();
        this.state = "READY";
    }

    public String getKioskId() {
        return kioskId;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public PricingStrategy getPricingStrategy() {
        return pricingStrategy;
    }

    public void setPricingStrategy(PricingStrategy pricingStrategy) {
        this.pricingStrategy = pricingStrategy;
    }

    /**
     * Execute a command and add it to history.
     * 
     * @param cmd The command to execute
     */
    public void executeCommand(Command cmd) {
        cmd.execute();
        commandHistory.add(cmd);
    }

    /**
     * Undo the last command in history.
     */
    public void undoLastCommand() {
        if (!commandHistory.isEmpty()) {
            Command last = commandHistory.remove(commandHistory.size() - 1);
            last.undo();
        }
    }

    public List<Command> getCommandHistory() {
        return new ArrayList<>(commandHistory);
    }

    @Override
    public String purchaseItem(String productId, int qty) {
        Product product = inventoryManager.getProduct(productId);
        if (product == null) {
            return "ERROR: Product not found";
        }

        if (inventoryManager.getAvailableStock(productId) < qty) {
            return "ERROR: Insufficient stock";
        }

        double finalPrice = pricingStrategy.computePrice(product);
        double totalCost = finalPrice * qty;

        return String.format("PURCHASE: %s x%d @ %.2f each = %.2f (Policy: %s)",
                product.getName(), qty, finalPrice, totalCost, pricingStrategy.getPolicyName());
    }

    @Override
    public boolean refundTransaction(String txId) {
        // Stub: handled by RefundCommand in later steps
        throw new UnsupportedOperationException("Use RefundCommand for transactions");
    }

    @Override
    public String runDiagnostics() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Kiosk Diagnostics ===\n");
        sb.append("Kiosk ID: ").append(kioskId).append("\n");
        sb.append("State: ").append(state).append("\n");
        sb.append("Pricing Policy: ").append(pricingStrategy.getPolicyName()).append("\n");
        sb.append("Hardware Modules: ").append(hardwareController.getModuleStatus()).append("\n");
        return sb.toString();
    }

    @Override
    public void restockInventory(Map<String, Integer> items) {
        // Stub: handled by RestockCommand in later steps
        throw new UnsupportedOperationException("Use RestockCommand for restocking");
    }
}
