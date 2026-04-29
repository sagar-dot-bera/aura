package com.aura.event;

public class CityMonitoringCenter implements EventListener {
    @Override
    public void onEvent(SystemEvent event) {
        if (event.getPriority() >= 7) {
            System.out.println("[CityMonitoringCenter] Critical event: " + event.getType() + " at "
                    + event.getTimestamp());
        }
    }
}
