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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

    @Mock
    AccountRepository accountRepository;

    @Mock
    CustomerRepository customerRepository;

    @Mock
    AppUserRepository appUserRepository;

    @Mock
    TransactionRepository transactionRepository;

    @Mock
    private AccountMapper accountMapper;

    @Mock
    private TransactionMapper transactionMapper;

    @InjectMocks
    AccountService accountService;

    private static final String OWNER = "owner";

    /**
     * Every account fixture here belongs to customer 1, so OWNER is wired to
     * that customer and the ownership guard passes. Tests that need the guard
     * to FAIL stub a different customer explicitly.
     */
    @BeforeEach
    void givenOwner() {
        AppUser user = new AppUser();
        user.setUsername(OWNER);
        user.setRole("CUSTOMER");
        user.setCustomer(new Customer(1L, "owner@example.com"));
        lenient().when(appUserRepository.findByUsername(OWNER)).thenReturn(Optional.of(user));
    }

    // --- createAccount ---

    @Test
    void shouldCreateAccount() {

        Customer customer = new Customer(1L, "siyamgz1122@gmail.com");
        customer.setVerificationStatus(VerificationStatus.VERIFIED);

        CreateAccountRequest request =
                new CreateAccountRequest(1L, AccountType.SAVINGS);

        when(accountMapper.toResponse(any(Account.class)))
                .thenReturn(new AccountResponse());

        when(customerRepository.findById(1L))
                .thenReturn(Optional.of(customer));

        when(accountRepository.save(any(Account.class)))
                .thenAnswer(invocation -> {
                    Account account = invocation.getArgument(0);
                    account.setId(1L);
                    return account;
                });

        AccountResponse response = accountService.createAccount(request);

        assertNotNull(response);

        verify(customerRepository).findById(1L);
        verify(accountRepository).save(any(Account.class));
    }

    // --- ownership (IDOR) ---

    /**
     * The bug class this guard exists for: a logged-in user asking for an
     * account that is not theirs. No URL rule can catch this, because the
     * answer depends on the data.
     */
    @Test
    void shouldRefuseToReadAnAccountBelongingToAnotherCustomer() {
        Account someoneElses = foreignAccount();

        when(accountRepository.findById(1L)).thenReturn(Optional.of(someoneElses));

        assertThrows(AccountAccessDeniedException.class,
                () -> accountService.getOwnedAccount(1L, OWNER));
    }

    @Test
    void shouldRefuseToWithdrawFromAnotherCustomersAccount() {
        Account someoneElses = foreignAccount();
        WithdrawRequest request = new WithdrawRequest(BigDecimal.ONE);

        when(accountRepository.findById(1L)).thenReturn(Optional.of(someoneElses));

        assertThrows(AccountAccessDeniedException.class,
                () -> accountService.withdraw(1L, request, OWNER));

        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    void shouldFailToCreateAccountIfCustomerNotVerified() {
        Customer customer = new Customer(1L, "siyamgz1122@gmail.com");
        customer.setVerificationStatus(VerificationStatus.PENDING);

        CreateAccountRequest request = new CreateAccountRequest(1L, AccountType.SAVINGS);

        when(customerRepository.findById(1L))
                .thenReturn(Optional.of(customer));

        assertThrows(CustomerNotVerifiedException.class,
                () -> accountService.createAccount(request));

        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    void shouldFailToCreateAccountIfCustomerNotFound() {
        CreateAccountRequest request = new CreateAccountRequest(3L, AccountType.SAVINGS);
        when(customerRepository.findById(3L))
                .thenReturn(Optional.empty());
        assertThrows(CustomerNotFoundException.class, () -> accountService.createAccount(request));
        verify(customerRepository).findById(3L);
        verify(accountRepository, never()).save(any(Account.class));
    }

    // --- deleteAccount ---

    @Test
    void shouldDeleteAccount() {
        Account account = mock(Account.class);

        when(accountRepository.findById(1L)).
                thenReturn(Optional.of(account));

        when(account.getCustomer()).thenReturn(new Customer(1L, "owner@example.com"));
        when(account.getBalance()).thenReturn(BigDecimal.ZERO);

        when(account.getStatus()).thenReturn(AccountStatus.CLOSED);

        accountService.deleteAccount(1L, OWNER);

        verify(accountRepository).save(account);
    }

    @Test
    void shouldThrowWhenAccountHasBalance() {
        Account account = mock(Account.class);

        when(accountRepository.findById(1L))
                .thenReturn(Optional.of(account));

        when(account.getCustomer()).thenReturn(new Customer(1L, "owner@example.com"));
        when (account.getBalance())
                .thenReturn(BigDecimal.TEN);

        assertThrows(AccountHasBalanceException.class, () -> accountService.deleteAccount(1L, OWNER));

        verify(accountRepository, never()).save(account);
    }

    @Test
    void shouldThrowWhenAccountIsStillActive() {
        Account account = mock(Account.class);

        when(accountRepository.findById(1L))
                .thenReturn(Optional.of(account));

        when(account.getCustomer()).thenReturn(new Customer(1L, "owner@example.com"));
        when (account.getBalance())
                .thenReturn(BigDecimal.ZERO);

        when(account.getStatus())
                .thenReturn(AccountStatus.ACTIVE);

        assertThrows(AccountisStillActiveException.class, () -> accountService.deleteAccount(1L, OWNER));

        verify(accountRepository, never()).save(account);
    }

    // --- deposit ---
    @Test
    void shouldIncreaseBalanceWhenDepositIsCalled() {
        DepositRequest request = new DepositRequest(BigDecimal.TEN);
        Account account = mock(Account.class);
        Transaction transaction = mock(Transaction.class);
        TransactionResponse response = mock(TransactionResponse.class);

        when(account.getCustomer()).thenReturn(createCustomer());

        when(accountRepository.findById(1L))
                .thenReturn(Optional.of(account));

        when(accountRepository.save(any(Account.class)))
                .thenReturn(account);

        when(transactionMapper.toTransaction(
                account,
                request.getAmount(),
                TransactionType.DEPOSIT
        )).thenReturn(transaction);

        when(transactionRepository.saveAndFlush(transaction))
                .thenReturn(transaction);

        when(transactionMapper.toResponse(transaction))
                .thenReturn(response);

        TransactionResponse result = accountService.deposit(1L, request, OWNER);

        assertEquals(response, result);

        verify(account).deposit(BigDecimal.TEN);
        verify(accountRepository).save(account);
        verify(transactionRepository).saveAndFlush(transaction);
        verify(transactionMapper).toResponse(transaction);
    }

    @Test
    void shouldFailToDepositWhenAmountIsNegative() {
        DepositRequest request = new DepositRequest(BigDecimal.valueOf(-5L));

        assertThrows(InvalidTransferAmountException.class, () -> accountService.deposit(
                1L, request, OWNER
        ));

        verifyNoInteractions(
                accountRepository,
                transactionMapper,
                transactionRepository
        );
    }

    @Test
    void shouldFailToDepositWhenAccountDoesNotExist() {
        DepositRequest request = new DepositRequest(BigDecimal.TEN);

        when(accountRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class, () ->
                accountService.deposit(1L, request, OWNER));

        verify(accountRepository).findById(1L);
        verify(transactionRepository, never()).saveAndFlush(any());
    }

    // ---withdraw ---

    @Test
    void shouldWithdrawFromAccount() {
        WithdrawRequest withdrawRequest = new WithdrawRequest(BigDecimal.TEN);

        Account account = createAccount(1L, BigDecimal.valueOf(20L));
        Transaction tx = mock(Transaction.class);
        TransactionResponse response = mock(TransactionResponse.class);

        when(accountRepository.findById(1L))
                .thenReturn(Optional.of(account));
        when(accountRepository.save(any(Account.class)))
                .thenReturn(account);
        when(transactionMapper.toTransaction(
                account,
                withdrawRequest.getAmount(),
                TransactionType.WITHDRAWAL
        )).thenReturn(tx);
        when(transactionMapper.toResponse(tx))
                .thenReturn(response);
        when(transactionRepository.saveAndFlush(tx))
                .thenReturn(tx);

        TransactionResponse result = accountService.withdraw(1L, withdrawRequest, OWNER);

        assertEquals(response, result);
        assertEquals(BigDecimal.TEN, account.getBalance());

        verify(accountRepository).findById(1L);
        verify(accountRepository).save(account);
        verify(transactionMapper).toTransaction(
                account,
                withdrawRequest.getAmount(),
                TransactionType.WITHDRAWAL
        );
        verify(transactionRepository).saveAndFlush(tx);
        verify(transactionMapper).toResponse(tx);
    }

    @Test
    void shouldFailToWithdrawIfAmountIsInvalid() {
        WithdrawRequest request = new WithdrawRequest(BigDecimal.valueOf(-5));

        assertThrows(InvalidTransferAmountException.class, () -> accountService.withdraw(1L, request, OWNER));

        verifyNoInteractions(
                accountRepository,
                transactionRepository,
                transactionMapper
        );
    }

    @Test
    void shouldFailToWithdrawIfAmountIsInsufficient() {
        Account account = createAccount(1L, BigDecimal.ZERO);
        WithdrawRequest request = new WithdrawRequest(BigDecimal.valueOf(10L));

        when(accountRepository.findById(1L))
                .thenReturn(Optional.of(account));

        assertThrows(InsufficientFundsException.class, () -> accountService.withdraw(1L, request, OWNER));

        verify(accountRepository).findById(1L);
        verify(accountRepository, never()).save(any(Account.class));
        verifyNoInteractions(
                transactionRepository,
                transactionMapper
        );
    }

    @Test
    void shouldFailToWithdrawIfAccountIsNotFound() {
        WithdrawRequest request = new WithdrawRequest(BigDecimal.TEN);
        when(accountRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class, () -> accountService.withdraw(1L, request, OWNER));
        verify(accountRepository).findById(1L);
        verify(accountRepository, never()).save(any(Account.class));

         verifyNoInteractions(
                 transactionMapper,
                 transactionRepository);
    }

    // --- transfer ---
    @Test
    void shouldTransferFromSourceToDestinationAccount() {
        Account sourceAccount = createAccount(1L, BigDecimal.TEN);
        Account destinationAccount = createAccount(2L, BigDecimal.TEN);
        TransferRequest request = new TransferRequest(BigDecimal.valueOf(5L), destinationAccount.getId());
        TransactionResponse response = mock(TransactionResponse.class);
        Transaction transaction = mock(Transaction.class);

        when(accountRepository.findById(1L))
                .thenReturn(Optional.of(sourceAccount));
        when(accountRepository.findById(2L))
                .thenReturn(Optional.of(destinationAccount));
        when(accountRepository.save(any(Account.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(transactionMapper.toTransaction(sourceAccount, destinationAccount, request.getAmount()))
                .thenReturn(transaction);
        when(transactionRepository.saveAndFlush(transaction))
                .thenReturn(transaction);
        when(transactionMapper.toResponse(transaction))
                .thenReturn(response);

        TransactionResponse result = accountService.transfer(sourceAccount.getId(), request, OWNER);

        assertEquals(result, response);
        assertEquals(sourceAccount.getBalance(), BigDecimal.valueOf(5L));
        assertEquals(destinationAccount.getBalance(), BigDecimal.valueOf(15L));

        verify(accountRepository).findById(1L);
        verify(accountRepository).findById(2L);
        verify(accountRepository).save(sourceAccount);
        verify(accountRepository).save(destinationAccount);
        verify(transactionMapper).toTransaction(
                sourceAccount, destinationAccount, request.getAmount()
        );
        verify(transactionRepository).saveAndFlush(transaction);
        verify(transactionMapper).toResponse(transaction);
    }

    @Test
    void shouldFailWhenAmountIsInvalid() {
        Account sourceAccount = createAccount(1L, BigDecimal.TEN);
        Account destinationAccount = createAccount(2L, BigDecimal.TEN);
        TransferRequest request = new TransferRequest(BigDecimal.valueOf(-5L), destinationAccount.getId());

        assertThrows(InvalidTransferAmountException.class, () ->
                accountService.transfer(sourceAccount.getId(), request, OWNER));

        verifyNoInteractions(
                accountRepository,
                transactionRepository,
                transactionMapper
        );
    }

    @Test
    void shouldFailWhenAccountHasInsufficientFunds() {
        Account sourceAccount = createAccount(1L, BigDecimal.ZERO);
        Account destinationAccount = createAccount(2L, BigDecimal.TEN);
        TransferRequest request = new TransferRequest(BigDecimal.TEN, destinationAccount.getId());

        when(accountRepository.findById(1L))
                .thenReturn(Optional.of(sourceAccount));
        when(accountRepository.findById(2L))
                .thenReturn(Optional.of(destinationAccount));

        assertThrows(InsufficientFundsException.class, () ->
                accountService.transfer(sourceAccount.getId(), request, OWNER));

        verifyNoMoreInteractions(
                accountRepository,
                transactionMapper,
                transactionMapper
        );
    }

    private Customer createCustomer() {
        return new Customer(1L, "siya@gmail.com");
    }

    /** An account belonging to customer 999, i.e. NOT the OWNER's. */
    private Account foreignAccount() {
        Account account = new Account(
                new Customer(999L, "other@example.com"),
                BigDecimal.TEN,
                AccountType.SAVINGS,
                AccountStatus.ACTIVE
        );
        account.setId(1L);
        return account;
    }

    private Account createAccount(Long accountId, BigDecimal balance) {

        Account account = new Account(
                createCustomer(),
                balance,
                AccountType.SAVINGS,
                AccountStatus.ACTIVE
        );
        account.setId(accountId);
        return account;
    }

}
