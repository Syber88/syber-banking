package com.syber.banking.verification;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AutoIdentityVerifier implements IdentityVerifier {

    private static final Logger log = LoggerFactory.getLogger(AutoIdentityVerifier.class);
    private static final String SOURCE = "AUTO_FORMAT_ONLY";

    @PostConstruct
    void warnLoudly() {
        log.warn("=".repeat(70));
        log.warn("AUTO ID VERIFICATION ACTIVE - DEVELOPMENT ONLY");
        log.warn("Only the ID number's format is checked. No identity is verified.");
        log.warn("Unset banking.verification.auto-approve before deployment.");
        log.warn("=".repeat(70));
    }

    @Override
    public VerificationResult verify(String nationalId, String firstName, String lastName) {
        if (nationalId == null || !nationalId.matches("\\d{13}")) {
            return VerificationResult.rejected("ID number must be 13 digits");
        }
        if (!luhnValid(nationalId)) {
            return VerificationResult.rejected("ID number checksum failed");
        }
        return VerificationResult.verified(SOURCE);
    }

    static boolean luhnValid(String digits) {
        int sum = 0;
        boolean doubleIt = false;
        for (int i = digits.length() - 1; i >= 0; i--) {
            int d = digits.charAt(i) - '0';
            if (doubleIt) {
                d *= 2;
                if (d > 9) {
                    d -= 9;
                }
            }
            sum += d;
            doubleIt = !doubleIt;
        }
        return sum % 10 == 0;
    }
}