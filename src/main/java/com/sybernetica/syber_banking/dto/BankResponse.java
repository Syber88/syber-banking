package com.sybernetica.syber_banking.dto;

import lombok.Builder;

@Builder
public class BankResponse {
    private String responseCode;
    private String responseMessage;
    private AccountInfo accountInfo;
}
