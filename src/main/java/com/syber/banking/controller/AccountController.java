package com.syber.banking.controller;

import com.syber.banking.entitiy.Account;
import com.syber.banking.entitiy.AccountStatus;
import com.syber.banking.entitiy.AccountType;
import com.syber.banking.entitiy.Customer;
import com.syber.banking.repository.AccountRepository;
import com.syber.banking.repository.CustomerRepository;
import com.syber.banking.service.AccountService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("Account/")
public class AccountController {

    private final CustomerRepository customerRepository;
    private final AccountRepository accountRepository;
    private final AccountService accountService;

    public AccountController(CustomerRepository customerRepository, AccountRepository accountRepository, AccountService accountService) {
        this.accountRepository = accountRepository;
        this.customerRepository = customerRepository;
        this.accountService = accountService;
    }

    @PostMapping("")
    public Account createAccount(Long customerId, AccountType accountType) {

        Customer customer = customerRepository.findById(customerId).orElseThrow();
        Account account = new Account(customer, BigDecimal.ZERO, accountType, AccountStatus.ACTIVE);
        accountService.assignAccountNumber(account.getId());

        return account;
    }

}
