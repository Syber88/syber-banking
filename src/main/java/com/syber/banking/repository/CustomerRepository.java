package com.syber.banking.repository;

import com.syber.banking.entity.Customer;
import com.syber.banking.entity.VerificationStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByEmail(String email);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByNationalId(String nationalId);

    List<Customer> findByVerificationStatus(VerificationStatus verificationStatus);
}
