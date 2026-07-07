package com.syber.banking.exception;

public class AccountisStillActive extends RuntimeException {
    public AccountisStillActive(String message) {
        super(message);
    }
}
