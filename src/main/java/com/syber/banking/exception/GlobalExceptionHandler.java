package com.syber.banking.exception;

import com.syber.banking.dto.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomerNotFound.class)
    public ResponseEntity<ErrorResponse> handleCustomerNotFound (
            CustomerNotFound ex,
            HttpServletRequest request
    ) {
        ErrorResponse error = new ErrorResponse() {
        }
    }
}
