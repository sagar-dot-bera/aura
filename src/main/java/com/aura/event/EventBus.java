package com.aura.event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Observer Pattern: EventBus decouples publishers and subscribers.
 */
public class EventBus {
    private final List<EventListener> listeners = new CopyOnWriteArrayList<>();

    public void subscribe(EventListener listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void unsubscribe(EventListener listener) {
        listeners.remove(listener);
    }

    public void publish(SystemEvent event) {
        if (event == null) {
            return;
        }
        List<SystemEvent> batch = new ArrayList<>();
        batch.add(event);
        publishAll(batch);
    }

    public void publishAll(List<SystemEvent> events) {
        if (events == null || events.isEmpty()) {
            return;
        }
        List<SystemEvent> orderedEvents = new ArrayList<>(events);
        Collections.sort(orderedEvents, new Comparator<SystemEvent>() {
            @Override
            public int compare(SystemEvent a, SystemEvent b) {
                return Integer.compare(b.getPriority(), a.getPriority());
            }
        });
        for (SystemEvent event : orderedEvents) {
            for (EventListener listener : listeners) {
                listener.onEvent(event);
            }
        }
    }
}
