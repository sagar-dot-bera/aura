package com.aura.core;

import com.aura.command.Command;
import com.aura.command.PurchaseItemCommand;
import com.aura.command.RefundCommand;
import com.aura.command.RestockCommand;
import com.aura.hardware.HardwareController;
import com.aura.inventory.InventoryManager;
import com.aura.inventory.Product;
import com.aura.payment.CardPaymentProcessor;
import com.aura.payment.PaymentProcessor;
import com.aura.persistence.PersistenceService;
import com.aura.pricing.PricingStrategy;
import com.aura.state.KioskState;
import com.aura.state.ActiveState;
import com.aura.verification.NoVerificationModule;
import com.aura.verification.VerificationModule;
import com.aura.event.EventBus;
import com.aura.event.SystemEvent;
import com.aura.failure.FailureHandler;
import com.aura.failure.RetryHandler;
import com.aura.failure.RecalibrationHandler;
import com.aura.failure.TechnicianAlertHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Facade Pattern: simplified external interface for the kiosk.
 * State Pattern: behavior changes based on current operational state.
 * KioskController acts as the main orchestrator and public facade,
 * hiding internal complexity of inventory, hardware, and pricing systems.
 * External systems interact ONLY through KioskInterface, not directly with
 * subsystems like InventoryManager, HardwareController, or PaymentProcessor.
 */
public class KioskController implements KioskInterface {
    private String kioskId;
    private PricingStrategy pricingStrategy;
    private List<Command> commandHistory;
    private InventoryManager inventoryManager;
    private HardwareController hardwareController;
    private PaymentProcessor paymentProcessor;
    private VerificationModule verificationModule;
    private EventBus eventBus;
    private PersistenceService persistenceService;
    private FailureHandler failureChain;
    private KioskState currentState;
    private String legacyState; // For backward compatibility

    public KioskController(String kioskId, InventoryManager inventoryManager,
            HardwareController hardwareController, PricingStrategy pricingStrategy) {
        this.kioskId = kioskId;
        this.inventoryManager = inventoryManager;
        this.hardwareController = hardwareController;
        this.pricingStrategy = pricingStrategy;
        this.commandHistory = new ArrayList<>();
        this.paymentProcessor = new CardPaymentProcessor();
        this.verificationModule = new NoVerificationModule();
        this.eventBus = new EventBus();
        this.persistenceService = new PersistenceService();
        this.failureChain = buildDefaultFailureChain();
        this.currentState = new ActiveState();
        this.legacyState = "READY";
    }

    public KioskController(String kioskId, InventoryManager inventoryManager,
            HardwareController hardwareController, PricingStrategy pricingStrategy,
            PaymentProcessor paymentProcessor, VerificationModule verificationModule,
            EventBus eventBus, PersistenceService persistenceService, FailureHandler failureChain) {
        this.kioskId = kioskId;
        this.inventoryManager = inventoryManager;
        this.hardwareController = hardwareController;
        this.pricingStrategy = pricingStrategy;
        this.paymentProcessor = paymentProcessor;
        this.verificationModule = verificationModule;
        this.eventBus = eventBus;
        this.persistenceService = persistenceService;
        this.failureChain = failureChain;
        this.commandHistory = new ArrayList<>();
        this.currentState = new ActiveState();
        this.legacyState = "READY";
    }

    public String getKioskId() {
        return kioskId;
    }

    /**
     * Get current state (legacy string format for backward compatibility).
     */
    public String getState() {
        return currentState != null ? currentState.getName() : legacyState;
    }

    /**
     * Set state using legacy string format (backward compatibility).
     */
    public void setState(String state) {
        this.legacyState = state;
    }

    /**
     * Get the current KioskState object.
     */
    public KioskState getCurrentState() {
        return currentState;
    }

    /**
     * Set the current KioskState object.
     * State Pattern: switch behavior by changing state.
     */
    public void setCurrentState(KioskState state) {
        this.currentState = state;
        System.out.println("[STATE] Kiosk switched to: " + state.getName());
    }

    public PricingStrategy getPricingStrategy() {
        return pricingStrategy;
    }

    public void setPricingStrategy(PricingStrategy pricingStrategy) {
        this.pricingStrategy = pricingStrategy;
    }

    public int getAvailableStock(String productId) {
        return inventoryManager.getAvailableStock(productId);
    }

    public String getHardwareSummary() {
        return hardwareController.getModuleStatus().toString();
    }

    public String getVerificationModuleName() {
        return verificationModule.getClass().getSimpleName();
    }

    public String getPaymentProviderName() {
        return paymentProcessor.getProviderName();
    }

    public void publishEvent(SystemEvent event) {
        eventBus.publish(event);
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

    /**
     * Facade Pattern: Purchase item through simplified interface.
     * State Pattern: check state before allowing purchase.
     * Handles inventory, pricing, and hardware orchestration internally.
     * External callers do not interact with InventoryManager, PricingStrategy, or
     * HardwareController directly.
     */
    @Override
    public String purchaseItem(String productId, int qty) {
        // State Pattern: check if purchase is allowed in current state
        if (!currentState.canPurchase(productId, qty)) {
            return "ERROR: Purchase blocked in " + currentState.getName() + " state";
        }

        Product product = inventoryManager.getProduct(productId);
        if (product == null) {
            return "ERROR: Product not found";
        }

        if (inventoryManager.getAvailableStock(productId) < qty) {
            return "ERROR: Insufficient stock";
        }

        // Print warning if in PowerSavingState
        if ("POWER_SAVING".equals(currentState.getName())) {
            System.out.println("[WARNING] " + currentState.handleInteraction());
        }

        PurchaseItemCommand cmd = new PurchaseItemCommand(productId, qty,
                inventoryManager, hardwareController, pricingStrategy,
                paymentProcessor, verificationModule, eventBus,
                persistenceService, failureChain);
        executeCommand(cmd);

        double unitPrice = pricingStrategy.computePrice(product);
        double totalCost = pricingStrategy.computePrice(product, qty);
        return String.format("PURCHASE: %s x%d @ %.2f each = %.2f (Policy: %s)",
                product.getName(), qty, unitPrice, totalCost, pricingStrategy.getPolicyName());
    }

    /**
     * Facade Pattern: Refund transaction.
     * State Pattern: check if refund is allowed in current state.
     * Handled by RefundCommand through the Command pattern.
     */
    @Override
    public boolean refundTransaction(String txId) {
        // State Pattern: check if refund is allowed
        if (!currentState.canRefund()) {
            System.out.println("ERROR: Refund blocked in " + currentState.getName() + " state");
            return false;
        }
        // Use RefundCommand for transactions
        RefundCommand cmd = new RefundCommand(txId, "UNKNOWN", 0, inventoryManager, persistenceService);
        executeCommand(cmd);
        return true;
    }

    /**
     * Facade Pattern: Run diagnostics.
     * Aggregates information from all subsystems without exposing them directly.
     */
    @Override
    public String runDiagnostics() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Kiosk Diagnostics ===\n");
        sb.append("Kiosk ID: ").append(kioskId).append("\n");
        sb.append("State: ").append(currentState.getName()).append(" - ").append(currentState.handleInteraction())
                .append("\n");
        sb.append("Pricing Policy: ").append(pricingStrategy.getPolicyName()).append("\n");

        Map<String, Boolean> moduleStatus = hardwareController.getModuleStatus();
        sb.append("Hardware Modules: ").append(moduleStatus.isEmpty() ? "None initialized" : moduleStatus).append("\n");
        sb.append("Total Products: ").append(inventoryManager.getAllProducts().size()).append("\n");

        return sb.toString();
    }

    /**
     * Facade Pattern: Restock inventory.
     * State Pattern: check if restock is allowed in current state.
     * Handled by RestockCommand through the Command pattern.
     */
    @Override
    public void restockInventory(Map<String, Integer> items) {
        // State Pattern: check if restock is allowed
        if (!currentState.canRestock()) {
            throw new IllegalStateException("Restock blocked in " + currentState.getName() + " state");
        }
        RestockCommand cmd = new RestockCommand(items, inventoryManager, persistenceService);
        executeCommand(cmd);
    }

    private FailureHandler buildDefaultFailureChain() {
        FailureHandler retry = new RetryHandler();
        FailureHandler recalibrate = new RecalibrationHandler();
        FailureHandler technician = new TechnicianAlertHandler();
        retry.setNext(recalibrate).setNext(technician);
        return retry;
    }
}
