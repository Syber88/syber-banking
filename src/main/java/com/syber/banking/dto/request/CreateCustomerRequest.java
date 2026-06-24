package com.syber.banking.dto.request;

import lombok.Getter;

@Getter
public class CreateCustomerRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String passwordHash;
}
