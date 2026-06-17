package com.syber.banking.controller;

import com.syber.banking.entitiy.Account;
import com.syber.banking.entitiy.Customer;
import com.syber.banking.repository.AccountRepository;
import com.syber.banking.repository.CustomerRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("Account/")
public class AccountController {

    private final CustomerRepository customerRepository;
    private final AccountRepository accountRepository;

    public AccountController(CustomerRepository customerRepository, AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
        this.customerRepository = customerRepository;
    }

    @PostMapping("")
    public Account createAccount(Long customerId) {

        Customer customer = customerRepository.findById(customerId).orElseThrow();
//        Account account = new Account(customer, BigDecimal.ZERO,)

        return new Account();
    }

}
