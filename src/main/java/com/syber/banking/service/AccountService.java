package com.syber.banking.service;

import com.syber.banking.entitiy.Account;
import com.syber.banking.entitiy.Transaction;
import com.syber.banking.entitiy.TransactionType;
import com.syber.banking.exception.InsufficientFundsException;
import com.syber.banking.repository.AccountRepository;
import com.syber.banking.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class AccountService {

    public final AccountRepository accountRepository;
    public final TransactionRepository transactionRepository;

    public AccountService(AccountRepository accountRepository, TransactionRepository transactionRepository){
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public Transaction deposit(Long accountId, BigDecimal depositAmount){
        if (depositAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Deposit must be greater than zero");
        }
        Account account = accountRepository.findById(accountId).orElseThrow();
        account.deposit(depositAmount);
        accountRepository.save(account);

        Transaction tx = new Transaction(null, accountId, depositAmount, TransactionType.DEPOSIT);
        return transactionRepository.save(tx);
    }

    @Transactional
    public Transaction withdraw(Long accountId, BigDecimal withdrawalAmount){
        if (withdrawalAmount == null || withdrawalAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new InsufficientFundsException("WIthdrawal must be greater than zero");
        }

        Account account = accountRepository.findById(accountId).orElseThrow();

        if (account.getBalance().compareTo(withdrawalAmount) <= 0) {
            throw new InsufficientFundsException("Insufficient Funds");
        }
        account.withdraw(withdrawalAmount);
        accountRepository.save(account);

        Transaction tx = new Transaction(null, accountId, withdrawalAmount, TransactionType.WITHDRAWAL);
        return transactionRepository.save(tx);
    }
}
