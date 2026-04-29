package com.aura.failure;

public class FailureContext {
    private String failureType;
    private String moduleId;
    private String productId;
    private int retryCount;
    private boolean resolved;
    private boolean recalibrationSucceeded;

    public FailureContext(String failureType, String moduleId, String productId) {
        this.failureType = failureType;
        this.moduleId = moduleId;
        this.productId = productId;
        this.retryCount = 0;
        this.resolved = false;
        this.recalibrationSucceeded = false;
    }

    public String getFailureType() {
        return failureType;
    }

    public String getModuleId() {
        return moduleId;
    }

    public String getProductId() {
        return productId;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public void incrementRetry() {
        this.retryCount++;
    }

    public boolean isResolved() {
        return resolved;
    }

    public void setResolved(boolean resolved) {
        this.resolved = resolved;
    }

    public boolean isRecalibrationSucceeded() {
        return recalibrationSucceeded;
    }

    public void setRecalibrationSucceeded(boolean recalibrationSucceeded) {
        this.recalibrationSucceeded = recalibrationSucceeded;
    }
}
