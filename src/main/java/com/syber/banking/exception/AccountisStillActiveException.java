package com.syber.banking.exception;

public class AccountisStillActiveException extends RuntimeException {
    public AccountisStillActiveException(String message) {
        super(message);
    }
}
