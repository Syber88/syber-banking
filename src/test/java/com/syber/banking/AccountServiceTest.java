package com.syber.banking;

import com.syber.banking.dto.request.CreateAccountRequest;
import com.syber.banking.dto.response.AccountResponse;
import com.syber.banking.entity.Account;
import com.syber.banking.entity.AccountStatus;
import com.syber.banking.entity.AccountType;
import com.syber.banking.entity.Customer;
import com.syber.banking.exception.AccountHasBalanceException;
import com.syber.banking.exception.AccountisStillActiveException;
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

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

}
