package com.syber.banking.verification;

import com.syber.banking.entity.VerificationStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pins the format check. If someone "simplifies" the verifier later, these fail.
 */
class AutoIdentityVerifierTest {

    private final AutoIdentityVerifier verifier = new AutoIdentityVerifier();

    // 8001015009087 is Luhn-valid; see shouldAcceptLuhnValidId below.
    private static final String VALID_ID = "8001015009087";

    @Test
    void shouldRejectNull() {
        assertEquals(VerificationStatus.REJECTED,
                verifier.verify(null, "A", "B").status());
    }

    @Test
    void shouldRejectWrongLength() {
        assertEquals(VerificationStatus.REJECTED,
                verifier.verify("123456789012", "A", "B").status());    // 12 digits
        assertEquals(VerificationStatus.REJECTED,
                verifier.verify("12345678901234", "A", "B").status());  // 14 digits
    }

    @Test
    void shouldRejectNonDigits() {
        assertEquals(VerificationStatus.REJECTED,
                verifier.verify("80010150090a7", "A", "B").status());
    }

    @Test
    void shouldRejectThirteenDigitsWithBadChecksum() {
        assertEquals(VerificationStatus.REJECTED,
                verifier.verify("8001015009088", "A", "B").status());
    }

    @Test
    void shouldAcceptValidIdAndMarkItAsFormatOnly() {
        VerificationResult result = verifier.verify(VALID_ID, "A", "B");

        assertEquals(VerificationStatus.VERIFIED, result.status());
        assertEquals("AUTO_FORMAT_ONLY", result.source(),
                "the marker must say no real identity check happened");
    }

    @Test
    void luhnAcceptsKnownValidNumbers() {
        assertTrue(AutoIdentityVerifier.luhnValid("79927398713"));  // canonical Luhn example
        assertTrue(AutoIdentityVerifier.luhnValid(VALID_ID));
    }

    @Test
    void luhnRejectsSingleDigitChanges() {
        assertFalse(AutoIdentityVerifier.luhnValid("79927398714"));
        assertFalse(AutoIdentityVerifier.luhnValid("8001015009088"));
    }
}
