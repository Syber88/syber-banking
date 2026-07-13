package com.syber.banking.dto.response;

import com.syber.banking.entity.TransactionStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class TransactionResponse {
    private Long transactionId;
    private String accountNumber;
    private String destinationAccountNumber;
    private BigDecimal depositedAmount;
    private LocalDateTime transactionDate;
    private TransactionStatus transactionStatus;
}
