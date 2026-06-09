package com.syber.banking;

import java.math.BigDecimal;

public class BankAccount {

    private BigDecimal balance;

    public BankAccount() {
        this.balance = BigDecimal.ZERO;
    }

    public BankAccount(BigDecimal initialBalance){
        this.balance = initialBalance;
    }

    public BigDecimal deposit(BigDecimal depositAmount){
        if (depositAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Deposit must be greater than zero");
        }
        this.balance = this.balance.add(depositAmount);
        return this.balance;
    }

    public BigDecimal withdraw(BigDecimal withdrawalAmount){
        if (withdrawalAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("WIthdrawal must be greater than zero");
        }

        if (this.balance.compareTo(withdrawalAmount) <= 0) {
            throw new IllegalArgumentException("Insufficient Funds");
        }
        this.balance = this.balance.subtract(withdrawalAmount);
        return this.balance;
    }

    public BigDecimal getBalance(){
        return this.balance;
    }

}
