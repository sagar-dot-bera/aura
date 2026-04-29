package com.aura.event;

public class MaintenanceService implements EventListener {
    @Override
    public void onEvent(SystemEvent event) {
        if (event instanceof HardwareFailureEvent) {
            HardwareFailureEvent failure = (HardwareFailureEvent) event;
            System.out.println("[MaintenanceService] Hardware failure on module " + failure.getModuleId()
                    + " for product " + failure.getProductId() + ": " + failure.getFailureType());
        }
    }
}
