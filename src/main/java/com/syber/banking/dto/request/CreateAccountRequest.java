package com.syber.banking.dto.request;

import com.syber.banking.entity.AccountType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateAccountRequest {
    private Long customerId;
    private AccountType accountType;
}
