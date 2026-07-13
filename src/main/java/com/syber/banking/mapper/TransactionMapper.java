package com.syber.banking.mapper;

import com.syber.banking.dto.response.TransactionResponse;
import com.syber.banking.entity.Account;
import com.syber.banking.entity.Transaction;
import com.syber.banking.entity.TransactionStatus;
import com.syber.banking.entity.TransactionType;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@AllArgsConstructor
public class TransactionMapper {

    public TransactionResponse toResponse(Transaction transaction) {
        return new TransactionResponse(transaction.getId(),
                transaction.getAccountNumber(),
                transaction.getDestinationAccountNumber(),
                transaction.getAmount(),
                transaction.getCreatedAt(),
                transaction.getStatus());
    }

    public Transaction toTransaction(Account account, BigDecimal amount, TransactionType type) {
        return new Transaction(
                account.getAccountNumber(),
                null,
                amount,
                type,
                TransactionStatus.SUCCESS
        );
    }

    public Transaction toTransaction(Account source, Account destination, BigDecimal amount) {
        return new Transaction(
                source.getAccountNumber(),
                destination.getAccountNumber(),
                amount,
                TransactionType.TRANSFER,
                TransactionStatus.SUCCESS
        );
    }

}
