package com.aura;

import com.aura.event.EmergencyModeActivatedEvent;
import com.aura.event.EventBus;
import com.aura.event.SystemEvent;
import com.aura.event.LowStockEvent;
import com.aura.event.EventListener;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("EventBus Tests")
public class EventBusTest {
    @Test
    @DisplayName("Subscribers receive events")
    public void testSubscribersReceiveEvents() {
        EventBus bus = new EventBus();
        List<String> received = new ArrayList<>();

        bus.subscribe(new EventListener() {
            @Override
            public void onEvent(SystemEvent event) {
                received.add(event.getType());
            }
        });

        bus.publish(new LowStockEvent("P001", 2));
        assertEquals(1, received.size());
        assertEquals("LOW_STOCK", received.get(0));
    }

    @Test
    @DisplayName("Emergency event has high priority")
    public void testEmergencyPriority() {
        EventBus bus = new EventBus();
        List<String> order = new ArrayList<>();

        bus.subscribe(new EventListener() {
            @Override
            public void onEvent(SystemEvent event) {
                order.add(event.getType());
            }
        });

        List<SystemEvent> batch = Arrays.asList(
                new LowStockEvent("P001", 2),
                new EmergencyModeActivatedEvent("Test"));

        bus.publishAll(batch);
        assertEquals("EMERGENCY_MODE_ACTIVATED", order.get(0));
    }
}
