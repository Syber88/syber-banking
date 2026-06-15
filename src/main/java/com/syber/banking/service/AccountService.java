package com.syber.banking.service;

import com.syber.banking.exception.InsufficientFundsException;

import java.math.BigDecimal;

public class AccountService {

    public void deposit(BigDecimal depositAmount){
        if (depositAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Deposit must be greater than zero");
        }
        this.balance = this.balance.add(depositAmount);
    }

    public void withdraw(BigDecimal withdrawalAmount){
        if (withdrawalAmount == null || withdrawalAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new InsufficientFundsException("WIthdrawal must be greater than zero");
        }

        if (this.balance.compareTo(withdrawalAmount) <= 0) {
            throw new InsufficientFundsException("Insufficient Funds");
        }
        this.balance = this.balance.subtract(withdrawalAmount);
    }

    public BigDecimal getBalance(){
        return this.balance;
    }
}
