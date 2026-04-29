package com.aura.verification;

public class NoVerificationModule implements VerificationModule {
    @Override
    public boolean verify(String productId, int quantity) {
        return true;
    }
}
