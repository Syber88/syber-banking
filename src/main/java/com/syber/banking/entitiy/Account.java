package com.syber.banking.entitiy;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import java.math.BigDecimal;

@Entity
@Getter
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String customerId; /*Foreign key*/
    private Long accountNumber;
    private BigDecimal balance;
    private String accountTye;
    private String status;

    public Account() {
        this.balance = BigDecimal.ZERO;
    }

    public Account(BigDecimal initialBalance){
        if (initialBalance == null) {
            throw new IllegalArgumentException("Initial balance cannot be null");
        }
        if (initialBalance.compareTo(BigDecimal.ZERO) < 0){
            throw new IllegalArgumentException("Initial balance cannot be negative");
        }
        this.balance = initialBalance;
    }
}
