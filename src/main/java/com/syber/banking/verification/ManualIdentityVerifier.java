package com.syber.banking.verification;

public class ManualIdentityVerifier implements IdentityVerifier {

    @Override
    public VerificationResult verify(String nationalId, String firstName, String lastName) {
        return VerificationResult.pending();   // routed to the staff review queue
    }
}