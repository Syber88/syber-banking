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
import com.syber.banking.repository.AppUserRepository;
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
    private final AppUserRepository appUserRepository;
    public static final String BANK_PREFIX = "8800";
    private final AccountMapper accountMapper;
    private final TransactionMapper transactionMapper;

    public AccountService(AccountRepository accountRepository,
                          CustomerRepository customerRepository,
                          TransactionRepository transactionRepository,
                          AppUserRepository appUserRepository,
                          AccountMapper accountMapper,
                          TransactionMapper transactionMapper){
        this.accountRepository = accountRepository;
        this.customerRepository = customerRepository;
        this.transactionRepository = transactionRepository;
        this.appUserRepository = appUserRepository;
        this.accountMapper = accountMapper;
        this.transactionMapper = transactionMapper;
    }

    /**
     * Does this username own this account?
     *
     * No URL rule or @PreAuthorize can answer this: the answer depends on the
     * DATA, not on the caller's role. Skipping it is the IDOR class of bug -
     * invisible in testing, because testing with your own account passes by
     * accident.
     *
     * Takes a username rather than an Authentication so the service stays free
     * of Spring Security types and can be unit-tested with a plain string.
     * The role is read from the database rather than from the session's
     * authorities, so a role revoked mid-session takes effect immediately.
     */
    private void assertCanAccess(Account account, String username) {
        AppUser user = appUserRepository.findByUsername(username)
                .orElseThrow(() -> new AccountAccessDeniedException("Not permitted"));

        if ("ADMIN".equals(user.getRole())) {
            return;   // staff act on other people's accounts by design
        }

        Customer owner = account.getCustomer();
        if (user.getCustomer() == null
                || owner == null
                || !owner.getId().equals(user.getCustomer().getId())) {
            throw new AccountAccessDeniedException("Not your account");
        }
    }

    @Transactional
    public AccountResponse createAccount(CreateAccountRequest request) {

        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new CustomerNotFoundException("Customer not Found"));

        if (customer.getVerificationStatus() != VerificationStatus.VERIFIED) {
            throw new CustomerNotVerifiedException("Customer identity is not verified");
        }
        Account account = new Account(customer, BigDecimal.ZERO, request.getAccountType(), AccountStatus.ACTIVE);
        Account createdAccount = accountRepository.save(account);
        assignAccountNumber(createdAccount);

        return accountMapper.toResponse(account);
    }

    @Transactional
    public TransactionResponse deposit(Long accountId, DepositRequest request, String username){
        if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidTransferAmountException("Deposit must be greater than zero");
        }
        Account account = getAccountById(accountId);
        assertCanAccess(account, username);
        account.deposit(request.getAmount());
        Account savedAccount = accountRepository.save(account);

        Transaction tx = transactionMapper.toTransaction(savedAccount, request.getAmount(), TransactionType.DEPOSIT);
        Transaction savedTransaction = transactionRepository.saveAndFlush(tx);

        return transactionMapper.toResponse(savedTransaction);
    }

    @Transactional
    public TransactionResponse withdraw(Long accountId, WithdrawRequest request, String username){
        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidTransferAmountException("Withdrawal must be greater than zero");
        }

        Account account = getAccountById(accountId);
        assertCanAccess(account, username);

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
    public TransactionResponse transfer(Long sourceId, TransferRequest request, String username) {

        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidTransferAmountException("Withdrawal must be greater than zero");
        }

        Account sourceAccount = getAccountById(sourceId);
        // Only the SOURCE needs ownership: paying money into someone else's
        // account is a normal thing to do.
        assertCanAccess(sourceAccount, username);
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
    public void deleteAccount(Long accountId, String username) {
        Account account = getAccountById(accountId);
        assertCanAccess(account, username);
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

    /** Ownership-checked read. Controllers must use this, not getAccountById. */
    public Account getOwnedAccount(Long accountId, String username) {
        Account account = getAccountById(accountId);
        assertCanAccess(account, username);
        return account;
    }

    public Account getAccountById(Long accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Account not Found"));
    }
}
