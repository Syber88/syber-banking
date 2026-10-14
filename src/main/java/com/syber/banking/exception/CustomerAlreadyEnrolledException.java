package com.syber.banking.exception;

public class CustomerAlreadyEnrolledException extends RuntimeException {
    public CustomerAlreadyEnrolledException(String message) {
        super(message);
    }
}
