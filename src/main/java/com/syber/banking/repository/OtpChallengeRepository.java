package com.syber.banking.repository;

import com.syber.banking.entity.OtpChallenge;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OtpChallengeRepository extends JpaRepository<OtpChallenge, Long> {

    /**
     * The newest unconsumed challenge for this customer. Issuing a new code
     * leaves older ones unconsumed but unreachable, so they are effectively dead.
     */
    Optional<OtpChallenge> findFirstByCustomerIdAndConsumedFalseOrderByIdDesc(Long customerId);
}
