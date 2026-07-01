package com.syber.banking.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CustomerResponse {
    private Long id;
    private String firstNamte;
    private String lastName;
    private String email;
}
