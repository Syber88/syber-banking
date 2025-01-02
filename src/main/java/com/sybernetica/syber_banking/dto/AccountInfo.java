package com.sybernetica.syber_banking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountInfo {
    private String accountName;
    private String accountBalance;
    private String accountNumber;
}
