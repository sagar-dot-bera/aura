package com.aura.hardware;

import java.util.HashMap;
import java.util.Map;

/**
 * Manages hardware modules and their operational status.
 */
public class HardwareController {
    private Map<String, Boolean> moduleStatus;
    private boolean delayedResponseEnabled;
    private boolean forceNextDispenseFailure;
    private boolean forceNextRecalibrationFailure;

    public HardwareController() {
        this.moduleStatus = new HashMap<>();
        this.delayedResponseEnabled = false;
        this.forceNextDispenseFailure = false;
        this.forceNextRecalibrationFailure = false;
    }

    public void initializeModule(String moduleId, boolean status) {
        moduleStatus.put(moduleId, status);
    }

    /**
     * Attempt to dispense a product.
     * 
     * @param productId The product to dispense
     * @return true if dispense succeeded, false if hardware failed
     */
    public boolean dispense(String productId) {
        if (delayedResponseEnabled) {
            try {
                Thread.sleep(150);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        if (forceNextDispenseFailure) {
            forceNextDispenseFailure = false;
            return false;
        }
        // For simplicity, we check a generic "dispenser" module
        Boolean status = moduleStatus.getOrDefault("dispenser", true);
        return status != null && status;
    }

    /**
     * Check if a module is available (operational).
     * 
     * @param moduleId The module to check
     * @return true if module is operational
     */
    public boolean isModuleAvailable(String moduleId) {
        return moduleStatus.getOrDefault(moduleId, true);
    }

    /**
     * Simulate a hardware failure by marking a module as unavailable.
     * 
     * @param moduleId The module to fail
     */
    public void simulateFailure(String moduleId) {
        moduleStatus.put(moduleId, false);
    }

    /**
     * Repair a module (set it back to operational).
     * 
     * @param moduleId The module to repair
     */
    public void repairModule(String moduleId) {
        moduleStatus.put(moduleId, true);
    }

    public void recoverModule(String moduleId) {
        moduleStatus.put(moduleId, true);
    }

    public String runDiagnostics() {
        StringBuilder sb = new StringBuilder();
        sb.append("Hardware Diagnostics: ");
        sb.append(moduleStatus.isEmpty() ? "No modules initialized" : moduleStatus);
        return sb.toString();
    }

    public boolean recalibrate(String moduleId) {
        if (!moduleStatus.containsKey(moduleId)) {
            return false;
        }
        if (forceNextRecalibrationFailure) {
            forceNextRecalibrationFailure = false;
            return false;
        }
        moduleStatus.put(moduleId, true);
        System.out.println("[Hardware] Recalibrated module " + moduleId);
        return true;
    }

    public void enableDelayedResponse(boolean enabled) {
        this.delayedResponseEnabled = enabled;
    }

    public void failNextDispense() {
        this.forceNextDispenseFailure = true;
    }

    public void failNextRecalibration() {
        this.forceNextRecalibrationFailure = true;
    }

    public Map<String, Boolean> getModuleStatus() {
        return new HashMap<>(moduleStatus);
    }
}
