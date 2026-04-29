package com.aura.failure;

public class TechnicianAlertHandler implements FailureHandler {
    private FailureHandler next;

    @Override
    public FailureHandler setNext(FailureHandler next) {
        this.next = next;
        return next;
    }

    @Override
    public boolean handle(FailureContext context) {
        // Chain of Responsibility Pattern: failure passes through handlers
        System.out.println("[FailureChain] Technician alert: unresolved failure for module "
                + context.getModuleId() + " product " + context.getProductId());
        context.setResolved(false);
        return false;
    }
}
