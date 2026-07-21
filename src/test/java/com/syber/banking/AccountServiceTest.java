package com.syber.banking;

import com.syber.banking.dto.request.CreateAccountRequest;
import com.syber.banking.dto.request.DepositRequest;
import com.syber.banking.dto.response.AccountResponse;
import com.syber.banking.dto.response.TransactionResponse;
import com.syber.banking.entity.*;
import com.syber.banking.exception.*;
import com.syber.banking.mapper.AccountMapper;
import com.syber.banking.mapper.TransactionMapper;
import com.syber.banking.repository.AccountRepository;
import com.syber.banking.repository.CustomerRepository;
import com.syber.banking.repository.TransactionRepository;
import com.syber.banking.service.AccountService;
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
    TransactionRepository transactionRepository;

    @Mock
    private AccountMapper accountMapper;

    @Mock
    private TransactionMapper transactionMapper;

    @InjectMocks
    AccountService accountService;

    // --- createAccount ---

    @Test
    void shouldCreateAccount() {

        Customer customer = new Customer(1L, "siyamgz1122@gmail.com");

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

        when(account.getBalance()).thenReturn(BigDecimal.ZERO);

        when(account.getStatus()).thenReturn(AccountStatus.CLOSED);

        accountService.deleteAccount(1L);

        verify(accountRepository).save(account);
    }

    @Test
    void shouldThrowWhenAccountHasBalance() {
        Account account = mock(Account.class);

        when(accountRepository.findById(1L))
                .thenReturn(Optional.of(account));

        when (account.getBalance())
                .thenReturn(BigDecimal.TEN);

        assertThrows(AccountHasBalanceException.class, () -> accountService.deleteAccount(1L));

        verify(accountRepository, never()).save(account);
    }

    @Test
    void shouldThrowWhenAccountIsStillActive() {
        Account account = mock(Account.class);

        when(accountRepository.findById(1L))
                .thenReturn(Optional.of(account));

        when (account.getBalance())
                .thenReturn(BigDecimal.ZERO);

        when(account.getStatus())
                .thenReturn(AccountStatus.ACTIVE);

        assertThrows(AccountisStillActiveException.class, () -> accountService.deleteAccount(1L));

        verify(accountRepository, never()).save(account);
    }

    // --- deposit ---
    @Test
    void shouldIncreaseBalanceWhenDepositIsCalled() {
        DepositRequest request = new DepositRequest(BigDecimal.TEN);
        Account account = mock(Account.class);
        Transaction transaction = mock(Transaction.class);
        TransactionResponse response = mock(TransactionResponse.class);

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

        TransactionResponse result = accountService.deposit(1L, request);

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
                1L, request
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
                accountService.deposit(1L, request));

        verify(accountRepository).findById(1L);
        verify(transactionRepository, never()).saveAndFlush(any());
    }

}
