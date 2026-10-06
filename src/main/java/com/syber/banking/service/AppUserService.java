package com.syber.banking.service;

import com.syber.banking.dto.request.RegisterRequest;
import com.syber.banking.entity.AppUser;
import com.syber.banking.exception.UsernameAlreadyExistsException;
import com.syber.banking.repository.AppUserRepository;

public class AppUserService {

    private final AppUserRepository appUserRepository;

    public AppUserService(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    public AppUser register(RegisterRequest request) {
        if (appUserRepository.existsByUsername(request.getUsername())) {
            throw new UsernameAlreadyExistsException("Username already exists");
        }

        AppUser user = new AppUser();
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());

        return appUserRepository.save(user);
    }
}
