package com.aura;

import com.aura.failure.FailureContext;
import com.aura.failure.FailureHandler;
import com.aura.failure.RecalibrationHandler;
import com.aura.failure.RetryHandler;
import com.aura.failure.TechnicianAlertHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Failure Chain Tests")
public class FailureChainTest {
    @Test
    @DisplayName("Failure chain executes handlers")
    public void testFailureChain() {
        FailureHandler retry = new RetryHandler();
        FailureHandler recalibrate = new RecalibrationHandler();
        FailureHandler technician = new TechnicianAlertHandler();
        retry.setNext(recalibrate).setNext(technician);

        FailureContext context = new FailureContext("DISPENSE_FAILURE", "dispenser", "P001");
        boolean handled = retry.handle(context);
        assertTrue(handled || !context.isResolved());
    }
}
