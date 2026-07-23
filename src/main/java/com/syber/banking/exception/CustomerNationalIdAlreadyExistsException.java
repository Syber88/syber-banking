package com.syber.banking.exception;

public class CustomerNationalIdAlreadyExistsException extends RuntimeException {
  public CustomerNationalIdAlreadyExistsException(String message) {
    super(message);
  }
}
