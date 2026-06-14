package com.syber.banking.entitiy;

import com.syber.banking.exception.InsufficientFundsException;

import java.math.BigDecimal;

public class BankAccount {

    private BigDecimal balance;

    public BankAccount() {
        this.balance = BigDecimal.ZERO;
    }

    public BankAccount(BigDecimal initialBalance){
        if (initialBalance == null) {
            throw new IllegalArgumentException("Initial balance cannot be null");
        }
        if (initialBalance.compareTo(BigDecimal.ZERO) < 0){
            throw new IllegalArgumentException("Initial balance cannot be negative");
        }
        this.balance = initialBalance;
    }

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
