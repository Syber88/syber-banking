package com.syber.banking.exception;

public class AccountHasBalanceException extends RuntimeException {
    public AccountHasBalanceException(String message) {
        super(message);
    }
}
