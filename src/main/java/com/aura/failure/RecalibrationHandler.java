package com.aura.failure;

public class RecalibrationHandler implements FailureHandler {
    private FailureHandler next;

    @Override
    public FailureHandler setNext(FailureHandler next) {
        this.next = next;
        return next;
    }

    @Override
    public boolean handle(FailureContext context) {
        // Chain of Responsibility Pattern: failure passes through handlers
        System.out.println("[FailureChain] Attempting recalibration for module " + context.getModuleId());
        if (context.isRecalibrationSucceeded()) {
            context.setResolved(true);
            return true;
        }
        if (next != null) {
            return next.handle(context);
        }
        return false;
    }
}
