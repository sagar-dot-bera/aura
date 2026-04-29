package com.aura.event;

import java.time.LocalDateTime;

public class HardwareFailureEvent implements SystemEvent {
    private final String moduleId;
    private final String productId;
    private final String failureType;
    private final LocalDateTime timestamp;

    public HardwareFailureEvent(String moduleId, String productId, String failureType) {
        this.moduleId = moduleId;
        this.productId = productId;
        this.failureType = failureType;
        this.timestamp = LocalDateTime.now();
    }

    public String getModuleId() {
        return moduleId;
    }

    public String getProductId() {
        return productId;
    }

    public String getFailureType() {
        return failureType;
    }

    @Override
    public String getType() {
        return "HARDWARE_FAILURE";
    }

    @Override
    public int getPriority() {
        return 8;
    }

    @Override
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
