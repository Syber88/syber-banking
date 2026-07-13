package com.syber.banking.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String accountNumber;
    private String destinationAccountNumber;
    private BigDecimal amount;
    private TransactionType type;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionStatus status;

    public Transaction(
            String accountNumber,
            String destinationAccountNumber,
            BigDecimal amount,
            TransactionType type,
            TransactionStatus status) {

        this.accountNumber = accountNumber;
        this.destinationAccountNumber = destinationAccountNumber;
        this.amount = amount;
        this.type = type;
        this.status = status;
    }

}
