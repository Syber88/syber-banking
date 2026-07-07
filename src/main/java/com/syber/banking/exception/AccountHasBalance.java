package com.syber.banking.exception;

public class AccountHasBalance extends RuntimeException {
    public AccountHasBalance(String message) {
        super(message);
    }
}
