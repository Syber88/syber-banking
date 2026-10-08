package com.syber.banking.service;

import com.syber.banking.dto.request.LoginRequest;
import com.syber.banking.dto.request.RegisterRequest;
import com.syber.banking.entity.AppUser;
import com.syber.banking.exception.InvalidCredentialsException;
import com.syber.banking.exception.UserNotFoundException;
import com.syber.banking.exception.UsernameAlreadyExistsException;
import com.syber.banking.repository.AppUserRepository;
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

        return appUserRepository.save(user);
    }

    public AppUser authenticate(LoginRequest request) {
        AppUser user = appUserRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid Credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new InvalidCredentialsException("Invalid Credentials");
        }
        return user;
    }

    public AppUser getById(Long userId){
        return appUserRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }
}
