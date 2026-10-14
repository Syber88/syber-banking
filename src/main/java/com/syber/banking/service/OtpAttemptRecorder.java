package com.syber.banking.service;

import com.syber.banking.repository.OtpChallengeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Records a failed OTP guess in its OWN transaction.
 *
 * OtpService.consume throws InvalidOtpException on a wrong code, and Spring
 * rolls back on RuntimeException - which would undo the increment if it were
 * written in the same transaction. Without REQUIRES_NEW here the attempt cap
 * never increments and a six-digit code is brute-forceable.
 *
 * This lives in a separate bean on purpose: @Transactional is applied by a
 * proxy, so a REQUIRES_NEW method called from inside OtpService would be
 * bypassed entirely.
 */
@Service
public class OtpAttemptRecorder {

    private final OtpChallengeRepository otpChallengeRepository;

    public OtpAttemptRecorder(OtpChallengeRepository otpChallengeRepository) {
        this.otpChallengeRepository = otpChallengeRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void recordFailure(Long challengeId) {
        // Re-read inside this transaction: the caller's instance belongs to a
        // persistence context that is about to be rolled back.
        otpChallengeRepository.findById(challengeId).ifPresent(challenge -> {
            challenge.setAttempts(challenge.getAttempts() + 1);
            otpChallengeRepository.save(challenge);
        });
    }
}
