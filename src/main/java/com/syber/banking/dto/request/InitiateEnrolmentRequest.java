package com.syber.banking.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InitiateEnrolmentRequest {

    @NotBlank
    @Pattern(regexp = "\\d{13}", message = "National ID must be 13 digits")
    private String nationalId;
}
