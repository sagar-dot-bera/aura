package com.aura.event;

import java.time.LocalDateTime;

public class EmergencyModeActivatedEvent implements SystemEvent {
    private final String reason;
    private final LocalDateTime timestamp;

    public EmergencyModeActivatedEvent(String reason) {
        this.reason = reason;
        this.timestamp = LocalDateTime.now();
    }

    public String getReason() {
        return reason;
    }

    @Override
    public String getType() {
        return "EMERGENCY_MODE_ACTIVATED";
    }

    @Override
    public int getPriority() {
        return 10; // High priority
    }

    @Override
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
