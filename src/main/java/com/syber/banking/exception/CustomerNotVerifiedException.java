package com.syber.banking.exception;

public class CustomerNotVerifiedException extends RuntimeException {
    public CustomerNotVerifiedException(String message) {
        super(message);
    }
}
