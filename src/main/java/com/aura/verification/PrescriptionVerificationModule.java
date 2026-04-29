package com.aura.verification;

public class PrescriptionVerificationModule implements VerificationModule {
    @Override
    public boolean verify(String productId, int quantity) {
        System.out.println("[Verification] Prescription verified for product: " + productId);
        return true;
    }
}
