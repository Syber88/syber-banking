package com.syber.banking.exception;

public class CustomerEmailAlreadyExistsException extends RuntimeException {
        public CustomerEmailAlreadyExistsException(String message) {
                super(message);
        }
}
