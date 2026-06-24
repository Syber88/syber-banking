package com.syber.banking.dto.response;

import com.syber.banking.entitiy.Account;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class CustomerResponse {
    private Long id;
    private String firstNamte;
    private String lastName;
    private String email;
}
