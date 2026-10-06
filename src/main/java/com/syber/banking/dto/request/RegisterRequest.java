package com.syber.banking.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class RegisterRequest {

    @NotBlank
    @Size(min=3, max=15)
    private String username;

    @NotBlank
    @Size(min=3, max=15)
    private String password;
}
