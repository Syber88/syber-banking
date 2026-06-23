package com.syber.banking.dto.response;

import com.syber.banking.entitiy.TransactionStatus;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor
public class DepositResponse {
    private Long transactionId;
    private String accountNumber;
    private BigDecimal depositedAmount;
    private LocalDateTime transactionDate;
    private TransactionStatus transactionStatus;
}
