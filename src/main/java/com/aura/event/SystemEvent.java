package com.aura.event;

import java.time.LocalDateTime;

/**
 * Observer Pattern: base interface for system events.
 */
public interface SystemEvent {
    String getType();

    int getPriority();

    LocalDateTime getTimestamp();
}
