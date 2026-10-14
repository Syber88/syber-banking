package com.syber.banking.verification;

public interface IdentityVerifier {
    VerificationResult verify(String nationalId, String firstName, String lastName);
}
