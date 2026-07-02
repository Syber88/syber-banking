package com.syber.banking.dto.response;

import com.syber.banking.entity.AccountStatus;
import com.syber.banking.entity.AccountType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AccountResponse {
    private Long id;
    private String accountNumber;
    private BigDecimal accountBalance;
    private AccountType accountType;
    private AccountStatus accountStatus;
}
