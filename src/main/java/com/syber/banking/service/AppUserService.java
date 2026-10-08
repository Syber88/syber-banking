package com.syber.banking.service;

import com.syber.banking.dto.request.LoginRequest;
import com.syber.banking.dto.request.RegisterRequest;
import com.syber.banking.entity.AppUser;
import com.syber.banking.exception.InvalidCredentialsException;
import com.syber.banking.exception.UserNotFoundException;
import com.syber.banking.exception.UsernameAlreadyExistsException;
import com.syber.banking.repository.AppUserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AppUserService {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;

    public AppUserService(AppUserRepository appUserRepository, PasswordEncoder passwordEncoder) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public AppUser register(RegisterRequest request) {
        if (appUserRepository.existsByUsername(request.getUsername())) {
            throw new UsernameAlreadyExistsException("Username already exists");
        }

        AppUser user = new AppUser();
        user.setUsername(request.getUsername());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole("CUSTOMER");

        return appUserRepository.save(user);
    }

    public AppUser getByUsername(String username) {
        return appUserRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("user not found"));
    }
}
