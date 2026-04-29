package com.aura.registry;

import com.aura.persistence.PersistenceService;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Singleton pattern: Manages global system configuration and status.
 * Thread-safe and ensures only one instance exists.
 */
public class CentralRegistry {
    private static CentralRegistry instance;
    private static final Object lock = new Object();

    private String systemStatus;
    private boolean emergencyMode;
    private int emergencyPurchaseLimit;
    private Set<String> essentialProductIds;
    private Map<String, Object> config;
    private PersistenceService persistenceService;

    private CentralRegistry() {
        this.systemStatus = "INITIALIZING";
        this.emergencyMode = false;
        this.emergencyPurchaseLimit = 1;
        this.essentialProductIds = new HashSet<>();
        this.config = new HashMap<>();
    }

    /**
     * Get the singleton instance of CentralRegistry.
     * Thread-safe using double-checked locking pattern.
     */
    public static CentralRegistry getInstance() {
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    instance = new CentralRegistry();
                }
            }
        }
        return instance;
    }

    // System Status Methods
    public String getSystemStatus() {
        return systemStatus;
    }

    public void setSystemStatus(String status) {
        this.systemStatus = status;
    }

    // Emergency Mode Methods
    public boolean isEmergencyMode() {
        return emergencyMode;
    }

    public void activateEmergencyMode() {
        this.emergencyMode = true;
        setSystemStatus("EMERGENCY");
        persistConfig();
    }

    public void deactivateEmergencyMode() {
        this.emergencyMode = false;
        setSystemStatus("OPERATIONAL");
        persistConfig();
    }

    // Emergency Purchase Limit Methods
    public int getEmergencyPurchaseLimit() {
        return emergencyPurchaseLimit;
    }

    public void setEmergencyPurchaseLimit(int limit) {
        this.emergencyPurchaseLimit = limit;
    }

    // Essential Product Methods
    public void addEssentialProduct(String productId) {
        essentialProductIds.add(productId);
    }

    public boolean isEssentialProduct(String productId) {
        return essentialProductIds.contains(productId);
    }

    public Set<String> getEssentialProducts() {
        return new HashSet<>(essentialProductIds);
    }

    // Configuration Methods
    public void putConfig(String key, Object value) {
        config.put(key, value);
        persistConfig();
    }

    public Object getConfig(String key) {
        return config.get(key);
    }

    public Map<String, Object> getAllConfig() {
        return new HashMap<>(config);
    }

    public void setPersistenceService(PersistenceService persistenceService) {
        this.persistenceService = persistenceService;
    }

    private void persistConfig() {
        if (persistenceService != null) {
            persistenceService.saveConfig(this);
        }
    }

    @Override
    public String toString() {
        return "CentralRegistry{" +
                "systemStatus='" + systemStatus + '\'' +
                ", emergencyMode=" + emergencyMode +
                ", emergencyPurchaseLimit=" + emergencyPurchaseLimit +
                ", essentialProductIds=" + essentialProductIds.size() +
                ", configKeys=" + config.size() +
                '}';
    }
}
