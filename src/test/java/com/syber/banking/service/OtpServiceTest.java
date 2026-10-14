package com.syber.banking.service;

import com.syber.banking.entity.Customer;
import com.syber.banking.entity.OtpChallenge;
import com.syber.banking.exception.InvalidOtpException;
import com.syber.banking.repository.OtpChallengeRepository;
import com.syber.banking.verification.OtpSender;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OtpServiceTest {

    @Mock
    OtpChallengeRepository otpChallengeRepository;

    @Mock
    OtpSender otpSender;

    @Mock
    OtpAttemptRecorder otpAttemptRecorder;

    // a real encoder: the hashing behaviour is what these tests assert on
    @Spy
    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @InjectMocks
    OtpService otpService;

    @Test
    void shouldSendASixDigitCodeAndStoreOnlyItsHash() {
        Customer customer = customer();

        otpService.issue(customer);

        ArgumentCaptor<String> sent = ArgumentCaptor.forClass(String.class);
        verify(otpSender).send(eq("siya@gmail.com"), sent.capture());
        String code = sent.getValue();
        assertTrue(code.matches("\\d{6}"), "code must be six digits, was: " + code);

        ArgumentCaptor<OtpChallenge> saved = ArgumentCaptor.forClass(OtpChallenge.class);
        verify(otpChallengeRepository).save(saved.capture());
        OtpChallenge challenge = saved.getValue();

        assertNotEquals(code, challenge.getCodeHash(), "the code must never be stored in plaintext");
        assertTrue(challenge.getCodeHash().startsWith("$2"), "expected a BCrypt hash");
        assertTrue(passwordEncoder.matches(code, challenge.getCodeHash()));
        assertFalse(challenge.isConsumed());
        assertEquals(0, challenge.getAttempts());
    }

    @Test
    void shouldAcceptTheCorrectCodeOnce() {
        OtpChallenge challenge = challengeFor("123456", Instant.now().plus(5, ChronoUnit.MINUTES), 0);
        when(otpChallengeRepository.findFirstByCustomerIdAndConsumedFalseOrderByIdDesc(1L))
                .thenReturn(Optional.of(challenge));

        otpService.consume(1L, "123456");

        assertTrue(challenge.isConsumed(), "a used challenge must be marked consumed");
    }

    @Test
    void shouldRejectAnExpiredChallenge() {
        OtpChallenge challenge = challengeFor("123456", Instant.now().minus(1, ChronoUnit.MINUTES), 0);
        when(otpChallengeRepository.findFirstByCustomerIdAndConsumedFalseOrderByIdDesc(1L))
                .thenReturn(Optional.of(challenge));

        assertThrows(InvalidOtpException.class, () -> otpService.consume(1L, "123456"));
        assertFalse(challenge.isConsumed());
    }

    /**
     * The brute-force guard. Six digits is a million possibilities, so without
     * an attempt cap this is minutes of scripted guessing. Note the code here
     * is CORRECT and must still be refused.
     */
    @Test
    void shouldRejectEvenTheCorrectCodeOnceAttemptsAreExhausted() {
        OtpChallenge challenge = challengeFor("123456", Instant.now().plus(5, ChronoUnit.MINUTES), 3);
        when(otpChallengeRepository.findFirstByCustomerIdAndConsumedFalseOrderByIdDesc(1L))
                .thenReturn(Optional.of(challenge));

        assertThrows(InvalidOtpException.class, () -> otpService.consume(1L, "123456"));
        assertFalse(challenge.isConsumed());
    }

    /**
     * The increment is delegated to OtpAttemptRecorder, which commits in its
     * own transaction. A unit test can only assert the delegation happens;
     * that it SURVIVES this method's rollback needs an integration test
     * against a real database - see OtpAttemptIntegrationTest.
     */
    @Test
    void shouldDelegateAFailedAttemptToTheRecorder() {
        OtpChallenge challenge = challengeFor("123456", Instant.now().plus(5, ChronoUnit.MINUTES), 0);
        challenge.setId(42L);
        when(otpChallengeRepository.findFirstByCustomerIdAndConsumedFalseOrderByIdDesc(1L))
                .thenReturn(Optional.of(challenge));

        assertThrows(InvalidOtpException.class, () -> otpService.consume(1L, "999999"));

        verify(otpAttemptRecorder).recordFailure(42L);
        verify(otpChallengeRepository, never()).save(any(OtpChallenge.class));
        assertFalse(challenge.isConsumed());
    }

    @Test
    void shouldRejectWhenThereIsNoUnconsumedChallenge() {
        when(otpChallengeRepository.findFirstByCustomerIdAndConsumedFalseOrderByIdDesc(1L))
                .thenReturn(Optional.empty());

        assertThrows(InvalidOtpException.class, () -> otpService.consume(1L, "123456"));
    }

    private Customer customer() {
        Customer customer = new Customer(1L, "siya@gmail.com");
        return customer;
    }

    private OtpChallenge challengeFor(String code, Instant expiresAt, int attempts) {
        OtpChallenge challenge = new OtpChallenge();
        challenge.setCustomer(customer());
        challenge.setCodeHash(new BCryptPasswordEncoder().encode(code));
        challenge.setCreatedAt(Instant.now());
        challenge.setExpiresAt(expiresAt);
        challenge.setAttempts(attempts);
        return challenge;
    }
}
