package com.syber.banking.entity;

import com.syber.banking.exception.AccountNotFoundException;
import com.syber.banking.exception.InsufficientFundsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AccountTest {

    private Account account;
    private Customer customer;

    @BeforeEach
    void setUp() {

        this.customer = Customer.builder()
                .id(1L)
                .firstName("Siyabonga")
                .lastName("Syber")
                .email("Siya@gmail.com")
                .nationalId("123456789")
                .build();

        this.account = new Account(
                customer,
                BigDecimal.ZERO,
                AccountType.SAVINGS,
                AccountStatus.ACTIVE
        );
    }

    // --- createAccount ---
    @Test
    void shouldCreateAccount() {
        assertEquals(customer, account.getCustomer());
        assertEquals("Siyabonga", account.getCustomer().getFirstName());
        assertEquals(BigDecimal.ZERO, account.getBalance());
        assertEquals(AccountType.SAVINGS, account.getAccountType());
        assertEquals(AccountStatus.ACTIVE, account.getStatus());
    }

    @Test
    void shouldNotCreateAccountIfCustomerIsNull() {

        assertThrows(IllegalArgumentException.class, () ->
                new Account(
                        null,
                        BigDecimal.TEN,
                        AccountType.SAVINGS,
                        AccountStatus.ACTIVE
                ));
    }
    @Test
    void shouldNotCreateAccountIfInitialBalanceIsNull() {
        assertThrows(IllegalArgumentException.class, () ->
                new Account(
                        customer,
                        null,
                        AccountType.SAVINGS,
                        AccountStatus.ACTIVE
                ));
    }

    @Test
    void shouldNotCreateAccountIfInitialBalanceIsNegative() {
        assertThrows(IllegalArgumentException.class, () ->
                new Account(
                        customer,
                        BigDecimal.valueOf(-5),
                        AccountType.SAVINGS,
                        AccountStatus.ACTIVE
                ));
        }

    @Test
    void shouldNotCreateAccountIfAccountTypeIsNull() {
        assertThrows(IllegalArgumentException.class, () ->
                new Account(
                        customer,
                        BigDecimal.TEN,
                        null,
                        AccountStatus.ACTIVE
                ));
        }


    // --- assignAccountNumber ---
    @Test
    void shouldAssignAccountNumber() {
        account.assignAccountNumber("880000001");

        assertEquals("880000001", account.getAccountNumber());
    }

    @Test
    void shouldNotAssignAccountNumberTwice() {
        account.assignAccountNumber("880000001");

        assertThrows(IllegalStateException.class, () ->
                account.assignAccountNumber("880000002"));
    }

    // --- deposit ---
    @Test
    void shouldDepositToAccount() {
        account.deposit(BigDecimal.TEN);

        assertEquals(BigDecimal.TEN, account.getBalance());
    }

    @ParameterizedTest
    @ValueSource(longs = {0, -5, -100})
    void shouldNotDepositIfAmountIsInvalid(long amount) {
        assertThrows(IllegalArgumentException.class, () ->
                account.deposit(BigDecimal.valueOf(amount)));
    }

    @Test
    void shouldNotDepositIfAmountIsNull() {
        assertThrows(
                IllegalArgumentException.class,
                () -> account.deposit(null)
        );
    }

    // --- withdraw ---
    @Test
    void shouldWithdrawFromAccount() {
           account.deposit(BigDecimal.valueOf(300L));
           assertEquals(BigDecimal.valueOf(300L), account.getBalance());

           account.withdraw(BigDecimal.valueOf(200L));
           assertEquals(BigDecimal.valueOf(100L), account.getBalance());
    }

    @ParameterizedTest
    @ValueSource(longs = {0, -5, -500})
    void shouldNotWithdrawIfAmountIsInvalid(long amount) {
        account.deposit(BigDecimal.valueOf(500L));
        assertEquals(BigDecimal.valueOf(500L), account.getBalance());

        assertThrows(IllegalArgumentException.class, () ->
                account.withdraw(BigDecimal.valueOf(amount)));

    }

    @Test
    void shouldFailWithdrawalIfAccountHasInsufficientFunds() {
        assertEquals(BigDecimal.ZERO, account.getBalance());
        assertThrows(InsufficientFundsException.class, () ->
                account.withdraw(BigDecimal.TEN));
    }

    @Test
    void shouldNotWithdrawIfAmountIsNull() {
        assertThrows(
                IllegalArgumentException.class,
                () -> account.withdraw(null)
        );
    }

    // --- transfer ---
    @Test
    void shouldTransferToAnotherAccount() {
        account.deposit(BigDecimal.valueOf(50L));
        long transferAmount = 5;

        Account destinationAccount = new Account(
                customer,
                BigDecimal.TEN,
                AccountType.SAVINGS,
                AccountStatus.ACTIVE
        );
        destinationAccount.assignAccountNumber("880000002");

        account.transfer(
                BigDecimal.valueOf(transferAmount),
                destinationAccount);

        assertEquals(BigDecimal.valueOf(45L), account.getBalance());
        assertEquals(BigDecimal.valueOf(15L), destinationAccount.getBalance());
    }

    @Test
    void shouldFailToTransferIfDestinationAccountIsNull() {
        AccountNotFoundException exception = assertThrows(AccountNotFoundException.class, () ->
                account.transfer(BigDecimal.TEN, null)
        );

        assertEquals(
                "Account was not not found",
                exception.getMessage());
    }

    @Test
    void shouldFailToTransferIfDestinationIsTheSourceAccount() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                account.transfer(BigDecimal.TEN, account));

        assertEquals(
                "Cannot transfer to the same account",
                exception.getMessage());
    }

    @ParameterizedTest
    @ValueSource(longs = {0, -5, -500})
    void shouldFailToTransferIfAmountIsInvalid(long amount) {
        Account destinationAccount = new Account(
                customer,
                BigDecimal.TEN,
                AccountType.SAVINGS,
                AccountStatus.ACTIVE
        );
        assertThrows(IllegalArgumentException.class, () ->
                account.transfer(BigDecimal.valueOf(amount), destinationAccount));
    }

    @Test
    void shouldFailToTransferIfAccountHasInsufficientFunds() {
        Account destinationAccount = new Account(
                customer,
                BigDecimal.TEN,
                AccountType.SAVINGS,
                AccountStatus.ACTIVE
        );
        destinationAccount.assignAccountNumber("880000002");

        assertEquals(BigDecimal.ZERO, account.getBalance());
        assertThrows(InsufficientFundsException.class, () ->
                account.transfer(BigDecimal.TEN, destinationAccount));
    }

    @Test
    void shouldNotTransferIfTransferAmountIsNull() {
        Account destinationAccount = new Account(
                customer,
                BigDecimal.TEN,
                AccountType.SAVINGS,
                AccountStatus.ACTIVE
        );

        assertThrows(IllegalArgumentException.class, () ->
                account.transfer(null, destinationAccount));

    }

}
