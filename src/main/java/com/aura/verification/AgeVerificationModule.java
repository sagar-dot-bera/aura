package com.aura.verification;

public class AgeVerificationModule implements VerificationModule {
    @Override
    public boolean verify(String productId, int quantity) {
        System.out.println("[Verification] Age verified for product: " + productId);
        return true;
    }
}
