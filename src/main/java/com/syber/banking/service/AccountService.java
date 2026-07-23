package com.syber.banking.service;

import com.syber.banking.dto.request.CreateAccountRequest;
import com.syber.banking.dto.request.DepositRequest;
import com.syber.banking.dto.request.TransferRequest;
import com.syber.banking.dto.request.WithdrawRequest;
import com.syber.banking.dto.response.AccountResponse;
import com.syber.banking.dto.response.TransactionResponse;
import com.syber.banking.entity.*;
import com.syber.banking.exception.*;
import com.syber.banking.mapper.AccountMapper;
import com.syber.banking.mapper.TransactionMapper;
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
    private final AccountMapper accountMapper;
    private final TransactionMapper transactionMapper;

    public AccountService(AccountRepository accountRepository,
                          CustomerRepository customerRepository,
                          TransactionRepository transactionRepository,
                          AccountMapper accountMapper,
                          TransactionMapper transactionMapper){
        this.accountRepository = accountRepository;
        this.customerRepository = customerRepository;
        this.transactionRepository = transactionRepository;
        this.accountMapper = accountMapper;
        this.transactionMapper = transactionMapper;
    }

    @Transactional
    public AccountResponse createAccount(CreateAccountRequest request) {

        Customer customer = customerRepository.findById(request.getCustomerId()).orElseThrow(() ->
                new CustomerNotFoundException("Customer not Found"));
        Account account = new Account(customer, BigDecimal.ZERO, request.getAccountType(), AccountStatus.ACTIVE);
        Account createdAccount = accountRepository.save(account);
        assignAccountNumber(createdAccount);

        return accountMapper.toResponse(account);
    }

    @Transactional
    public TransactionResponse deposit(Long accountId, DepositRequest request){
        if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidTransferAmountException("Deposit must be greater than zero");
        }
        Account account = getAccountById(accountId);
        account.deposit(request.getAmount());
        Account savedAccount = accountRepository.save(account);

        Transaction tx = transactionMapper.toTransaction(savedAccount, request.getAmount(), TransactionType.DEPOSIT);
        Transaction savedTransaction = transactionRepository.saveAndFlush(tx);

        return transactionMapper.toResponse(savedTransaction);
    }

    @Transactional
    public TransactionResponse withdraw(Long accountId, WithdrawRequest request){
        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidTransferAmountException("Withdrawal must be greater than zero");
        }

        Account account = getAccountById(accountId);

        if (account.getBalance().compareTo(request.getAmount()) < 0) {
            throw new InsufficientFundsException("Insufficient Funds");
        }
        account.withdraw(request.getAmount());
        Account savedAccount = accountRepository.save(account);

        Transaction tx = transactionMapper.toTransaction(savedAccount, request.getAmount(), TransactionType.WITHDRAWAL);
        Transaction savedTransaction = transactionRepository.saveAndFlush(tx);
        return transactionMapper.toResponse(savedTransaction);
    }

    @Transactional
    public TransactionResponse transfer(Long sourceId, TransferRequest request) {

        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidTransferAmountException("Withdrawal must be greater than zero");
        }

        Account sourceAccount = getAccountById(sourceId);
        Account destinationAccount = getAccountById(request.getDestinationAccountId());

        if (sourceAccount.getBalance().compareTo(BigDecimal.ZERO) < 0) {
            throw new InsufficientFundsException("Insufficient funds");
        }

        sourceAccount.transfer(request.getAmount(), destinationAccount);

        accountRepository.save(sourceAccount);
        accountRepository.save(destinationAccount);

        Transaction tx = transactionMapper.toTransaction(sourceAccount, destinationAccount, request.getAmount());
        Transaction savedTransaction = transactionRepository.saveAndFlush(tx);
        return transactionMapper.toResponse(savedTransaction);
    }

    @Transactional
    public void deleteAccount(Long accountId) {
        Account account = getAccountById(accountId);
        if (account.getBalance().compareTo(BigDecimal.ZERO) > 0) {
            throw new AccountHasBalanceException("Cannot delete an account with a balance.");
        }
        if (account.getStatus() != AccountStatus.CLOSED) {
            throw new AccountisStillActiveException("Active account cannot be closed.");
        }
        accountRepository.save(account);
    }

    @Transactional
    public void assignAccountNumber(Account account) {
        if (account.getAccountNumber() != null) {
            throw new AccountNumberAlreadyAssignedException("Account number has already been assigned.");
        }
        String sequence = String.format("%08d",account.getId());
        String generatedAccountNumber = BANK_PREFIX + sequence;
        account.assignAccountNumber(generatedAccountNumber);
    }

    public Account getAccountById(Long accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Account not Found"));
    }
}
