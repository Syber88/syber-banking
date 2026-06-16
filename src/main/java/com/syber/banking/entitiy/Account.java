package com.syber.banking.entitiy;

import jakarta.persistence.*;
import lombok.Getter;
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
    private Long accountNumber;
    private BigDecimal balance;
    private AccountType accountType;
    private AccountStatus status;

    public Account() {
        this.balance = BigDecimal.ZERO;
    }

    public Account(Customer customer,
                   BigDecimal initialBalance,
                   Long accountNumber,
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

        if (accountNumber == null) {
            throw new IllegalArgumentException("Account number cannot be null");
        }

        if (type == null) {
            throw new IllegalArgumentException("Account type cannot be null");
        }

        this.customer = customer;
        this.balance = initialBalance;
        this.accountNumber = accountNumber;
        this.accountType = type;
        this.status = status;
    }

    public void deposit(BigDecimal depositAmount){
        if (depositAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Deposit must be greater than zero");
        }
        this.balance = this.balance.add(depositAmount);
    }
}
