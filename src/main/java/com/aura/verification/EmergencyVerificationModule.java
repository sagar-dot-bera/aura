package com.aura.verification;

public class EmergencyVerificationModule implements VerificationModule {
    @Override
    public boolean verify(String productId, int quantity) {
        System.out.println("[Verification] Emergency mode verification bypassed for " + productId);
        return true;
    }
}
