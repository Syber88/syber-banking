package com.syber.banking.service;

import com.syber.banking.entity.Customer;
import com.syber.banking.entity.OtpChallenge;
import com.syber.banking.exception.InvalidOtpException;
import com.syber.banking.repository.OtpChallengeRepository;
import com.syber.banking.verification.OtpSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;

@Service
public class OtpService {

    private static final Duration TTL = Duration.ofMinutes(5);
    private static final int MAX_ATTEMPTS = 3;

    /** One message for every failure, so expired, wrong, used and
     *  nonexistent are indistinguishable to the caller. */
    private static final String FAILURE_MESSAGE = "Invalid or expired code";

    private final OtpChallengeRepository otpChallengeRepository;
    private final PasswordEncoder passwordEncoder;
    private final OtpSender otpSender;
    private final OtpAttemptRecorder otpAttemptRecorder;

    /** SecureRandom, not Random: Random is predictable from a few outputs,
     *  and this value is a credential. */
    private final SecureRandom random = new SecureRandom();

    public OtpService(OtpChallengeRepository otpChallengeRepository,
                      PasswordEncoder passwordEncoder,
                      OtpSender otpSender,
                      OtpAttemptRecorder otpAttemptRecorder) {
        this.otpChallengeRepository = otpChallengeRepository;
        this.passwordEncoder = passwordEncoder;
        this.otpSender = otpSender;
        this.otpAttemptRecorder = otpAttemptRecorder;
    }

    public void issue(Customer customer) {
        // %06d keeps leading zeros - 000123 is a valid code.
        String code = String.format("%06d", random.nextInt(1_000_000));

        Instant now = Instant.now();
        OtpChallenge challenge = new OtpChallenge();
        challenge.setCustomer(customer);
        challenge.setCodeHash(passwordEncoder.encode(code));
        challenge.setCreatedAt(now);
        challenge.setExpiresAt(now.plus(TTL));
        otpChallengeRepository.save(challenge);

        otpSender.send(customer.getEmail(), code);
    }

    @Transactional
    public void consume(Long customerId, String code) {
        OtpChallenge challenge = otpChallengeRepository
                .findFirstByCustomerIdAndConsumedFalseOrderByIdDesc(customerId)
                .orElseThrow(() -> new InvalidOtpException(FAILURE_MESSAGE));

        if (Instant.now().isAfter(challenge.getExpiresAt())
                || challenge.getAttempts() >= MAX_ATTEMPTS) {
            throw new InvalidOtpException(FAILURE_MESSAGE);
        }

        if (!passwordEncoder.matches(code, challenge.getCodeHash())) {
            // Recorded in a SEPARATE transaction: this method's rollback would
            // otherwise discard the increment, leaving the attempt cap inert.
            otpAttemptRecorder.recordFailure(challenge.getId());
            throw new InvalidOtpException(FAILURE_MESSAGE);
        }

        challenge.setAttempts(challenge.getAttempts() + 1);
        challenge.setConsumed(true);
        otpChallengeRepository.save(challenge);
    }
}
