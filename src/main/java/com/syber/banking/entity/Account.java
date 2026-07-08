package com.syber.banking.entity;

import com.syber.banking.exception.InsufficientFundsException;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name="customer")
    private Customer customer; /*Foreign key*/
    private String accountNumber;
    private BigDecimal balance;
    private AccountType accountType;
    @Setter
    private AccountStatus status;

    public Account() {
        this.balance = BigDecimal.ZERO;
    }

    public Account(Customer customer,
                   BigDecimal initialBalance,
                   AccountType type,
                   AccountStatus status) {

        if (customer == null) {
            throw new IllegalArgumentException("Customer cannot be null");
        }

        if (initialBalance == null) {
            throw new IllegalArgumentException("Initial balance cannot be null");
        }

        if (initialBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Initial balance cannot be negative");
        }

        if (type == null) {
            throw new IllegalArgumentException("Account type cannot be null");
        }

        this.customer = customer;
        this.balance = initialBalance;
        this.accountType = type;
        this.status = AccountStatus.ACTIVE;
    }

    public void assignAccountNumber(String accountNumber) {
        if (this.accountNumber != null) {
            throw new IllegalStateException("Account number has already been assigned.");
        }
        this.accountNumber = accountNumber;
    }

    public void deposit(BigDecimal depositAmount){
        if (depositAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Deposit must be greater than zero");
        }
        this.balance = this.balance.add(depositAmount);
    }

    public void withdraw(BigDecimal withdrawalAmount){
        if (withdrawalAmount == null || withdrawalAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new InsufficientFundsException("Withdrawal must be greater than zero");
        }

        if (this.balance.compareTo(withdrawalAmount) < 0) {
            throw new InsufficientFundsException("Insufficient Funds");
        }
        this.balance = this.balance.subtract(withdrawalAmount);
    }
}
