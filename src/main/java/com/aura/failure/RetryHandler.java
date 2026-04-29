package com.aura.failure;

public class RetryHandler implements FailureHandler {
    private FailureHandler next;

    @Override
    public FailureHandler setNext(FailureHandler next) {
        this.next = next;
        return next;
    }

    @Override
    public boolean handle(FailureContext context) {
        // Chain of Responsibility Pattern: failure passes through handlers
        if (context.getRetryCount() < 1) {
            context.incrementRetry();
            System.out.println("[FailureChain] Retry attempt " + context.getRetryCount());
            if (next != null) {
                return next.handle(context);
            }
            return false;
        }
        if (next != null) {
            return next.handle(context);
        }
        return false;
    }
}
