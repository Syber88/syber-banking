package com.syber.banking.service;

import com.syber.banking.dto.response.AccountResponse;
import com.syber.banking.entitiy.*;
import com.syber.banking.exception.InsufficientFundsException;
import com.syber.banking.repository.AccountRepository;
import com.syber.banking.repository.CustomerRepository;
import com.syber.banking.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class AccountService {

    public final AccountRepository accountRepository;
    public final CustomerRepository customerRepository;
    public final TransactionRepository transactionRepository;
    public static final String BANK_PREFIX = "8800";

    public AccountService(AccountRepository accountRepository, CustomerRepository customerRepository, TransactionRepository transactionRepository){
        this.accountRepository = accountRepository;
        this.customerRepository = customerRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public AccountResponse createAccount(Long customerId, AccountType accountType) {

        Customer customer = customerRepository.findById(customerId).orElseThrow();
        Account account = new Account(customer, BigDecimal.ZERO, accountType, AccountStatus.ACTIVE);
        Account createdAccount = accountRepository.save(account);
        assignAccountNumber(createdAccount.getId());

        return new AccountResponse(
                createdAccount.getId(),
                createdAccount.getAccountNumber(),
                createdAccount.getBalance(),
                createdAccount.getAccountType(),
                createdAccount.getStatus()
        );
    }

    @Transactional
    public Transaction deposit(Long accountId, BigDecimal depositAmount){
        if (depositAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Deposit must be greater than zero");
        }
        Account account = accountRepository.findById(accountId).orElseThrow();
        account.deposit(depositAmount);
        accountRepository.save(account);

        Transaction tx = new Transaction(null, account.getAccountNumber(), depositAmount, TransactionType.DEPOSIT);
        return transactionRepository.save(tx);
    }

    @Transactional
    public Transaction withdraw(Long accountId, BigDecimal withdrawalAmount){
        if (withdrawalAmount == null || withdrawalAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new InsufficientFundsException("Withdrawal must be greater than zero");
        }

        Account account = accountRepository.findById(accountId).orElseThrow();

        if (account.getBalance().compareTo(withdrawalAmount) <= 0) {
            throw new InsufficientFundsException("Insufficient Funds");
        }
        account.withdraw(withdrawalAmount);
        accountRepository.save(account);

        Transaction tx = new Transaction(null, account.getAccountNumber(), withdrawalAmount, TransactionType.WITHDRAWAL);
        return transactionRepository.save(tx);
    }

    @Transactional
    public void assignAccountNumber(Long accountId) {
        Account account = accountRepository.findById(accountId).orElseThrow();
        if (account.getAccountNumber() != null) {
            throw new IllegalStateException("Account number has already been assigned.");
        }
        String sequence = String.format("%08d",account.getId());
        String generatedAccountNumber = BANK_PREFIX + sequence;
        account.assignAccountNumber(generatedAccountNumber);
        accountRepository.save(account);
    }
}
