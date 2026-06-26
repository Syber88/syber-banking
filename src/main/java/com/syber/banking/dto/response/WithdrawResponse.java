package com.syber.banking.dto.response;

import com.syber.banking.entitiy.TransactionStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class WithdrawResponse {
    private Long transactionId;
    private String accountNumber;
    private BigDecimal withdrawalAmount;
    private LocalDateTime transactionDate;
    private TransactionStatus status;
}