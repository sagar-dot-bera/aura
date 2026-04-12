package com.aura.registry;

import java.util.HashMap;
import java.util.Map;

/**
 * Singleton registry for managing global system configuration and status.
 */
public class CentralRegistry {
    private static CentralRegistry instance;
    private Map<String, Object> config;
    private String systemStatus;

    private CentralRegistry() {
        this.config = new HashMap<>();
        this.systemStatus = "INITIALIZING";
    }

    public static synchronized CentralRegistry getInstance() {
        if (instance == null) {
            instance = new CentralRegistry();
        }
        return instance;
    }

    public Map<String, Object> getConfig() {
        return config;
    }

    public void setConfig(Map<String, Object> config) {
        this.config = config;
    }

    public String getSystemStatus() {
        return systemStatus;
    }

    public void setSystemStatus(String systemStatus) {
        this.systemStatus = systemStatus;
    }
}
