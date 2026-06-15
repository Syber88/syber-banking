package com.syber.banking.controller;

import com.syber.banking.repository.AccountRepository;
import com.syber.banking.repository.CustomerRepository;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class AccountController {

    private final CustomerRepository customerRepository;
    private final AccountRepository accountRepository;

    public AccountController(CustomerRepository customerRepository, AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
        this.customerRepository = customerRepository;
    }


}
