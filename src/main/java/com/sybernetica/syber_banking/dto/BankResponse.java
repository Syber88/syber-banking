package com.sybernetica.syber_banking.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class BankResponse {
    private String responseCode;
    private String responseMessage;
    private AccountInfo accountInfo;
}
