package com.aura.event;

public class EmergencyCoordinator implements EventListener {
    @Override
    public void onEvent(SystemEvent event) {
        if (event instanceof EmergencyModeActivatedEvent) {
            EmergencyModeActivatedEvent emergency = (EmergencyModeActivatedEvent) event;
            System.out.println("[EmergencyCoordinator] Emergency mode activated: " + emergency.getReason());
        }
    }
}
