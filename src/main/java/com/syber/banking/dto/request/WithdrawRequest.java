package com.syber.banking.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class WithdrawRequest {

    @NotNull(message = "Withdrawal amount is required")
    private BigDecimal amount;
}
