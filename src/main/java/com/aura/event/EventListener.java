package com.aura.event;

/**
 * Observer Pattern: EventBus decouples publishers and subscribers.
 */
public interface EventListener {
    void onEvent(SystemEvent event);
}
