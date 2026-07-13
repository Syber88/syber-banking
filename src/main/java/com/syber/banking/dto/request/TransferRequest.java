package com.syber.banking.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class TransferRequest {

    @NotNull(message = "Transfer amount is required")
    private BigDecimal amount;

    @NotNull
    private Long destinationAccountId;
}
