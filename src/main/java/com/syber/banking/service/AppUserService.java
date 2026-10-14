package com.syber.banking.service;

import com.syber.banking.dto.request.RegisterRequest;
import com.syber.banking.entity.AppUser;
import com.syber.banking.entity.Customer;
import com.syber.banking.entity.VerificationStatus;
import com.syber.banking.exception.CustomerAlreadyEnrolledException;
import com.syber.banking.exception.InvalidOtpException;
import com.syber.banking.exception.UsernameAlreadyExistsException;
import com.syber.banking.repository.AppUserRepository;
import com.syber.banking.repository.CustomerRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AppUserService {

    private final AppUserRepository appUserRepository;
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final OtpService otpService;

    public AppUserService(AppUserRepository appUserRepository,
                          CustomerRepository customerRepository,
                          PasswordEncoder passwordEncoder,
                          OtpService otpService) {
        this.appUserRepository = appUserRepository;
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
        this.otpService = otpService;
    }

    /**
     * Issues an enrolment code to a verified, not-yet-enrolled customer.
     *
     * Returns void and throws nothing on purpose. An unknown national ID, an
     * unverified customer and an already-enrolled one must all look identical
     * to success, or this endpoint becomes a free oracle for "is this person a
     * customer of this bank?". Do not add an else branch here.
     */
    public void initiateEnrolment(String nationalId) {
        customerRepository.findByNationalId(nationalId)
                .filter(customer -> customer.getVerificationStatus() == VerificationStatus.VERIFIED)
                .filter(customer -> !appUserRepository.existsByCustomerId(customer.getId()))
                .ifPresent(otpService::issue);
    }

    @Transactional
    public AppUser completeEnrolment(RegisterRequest request) {
        if (appUserRepository.existsByUsername(request.getUsername())) {
            throw new UsernameAlreadyExistsException("Username already exists");
        }

        // InvalidOtpException, not CustomerNotFoundException: don't tell the
        // caller which half of the pair was wrong.
        Customer customer = customerRepository.findByNationalId(request.getNationalId())
                .orElseThrow(() -> new InvalidOtpException("Invalid or expired code"));

        if (appUserRepository.existsByCustomerId(customer.getId())) {
            throw new CustomerAlreadyEnrolledException("This customer already has a login");
        }

        otpService.consume(customer.getId(), request.getOtp());

        AppUser user = new AppUser();
        user.setUsername(request.getUsername());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole("CUSTOMER");
        user.setCustomer(customer);

        return appUserRepository.save(user);
    }

    public AppUser getByUsername(String username) {
        return appUserRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("user not found"));
    }
}
