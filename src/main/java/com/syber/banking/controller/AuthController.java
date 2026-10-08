package com.syber.banking.controller;

import com.syber.banking.dto.request.LoginRequest;
import com.syber.banking.dto.request.RegisterRequest;
import com.syber.banking.dto.response.AppUserResponse;
import com.syber.banking.entity.AppUser;
import com.syber.banking.service.AppUserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AppUserService appUserService;

    public AuthController(AppUserService appUserService) {
        this.appUserService = appUserService;
    }

    @PostMapping("/register")
    public ResponseEntity<AppUserResponse> register(@Valid @RequestBody RegisterRequest request) {
        AppUser user = appUserService.register(request);
        URI location = URI.create("/api/v1/auth/users/" + user.getId());
        return ResponseEntity.created(location)
                .body(new AppUserResponse(user.getId(), user.getUsername()));
    }

    @GetMapping("/me")
    public ResponseEntity<AppUserResponse> me(Authentication authentication) {
        AppUser user = appUserService.getByUsername(authentication.getName());
        return ResponseEntity.ok(new AppUserResponse(user.getId(), user.getUsername()));
    }
}
