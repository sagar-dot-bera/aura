package com.aura.failure;

public interface FailureHandler {
    FailureHandler setNext(FailureHandler next);

    boolean handle(FailureContext context);
}
