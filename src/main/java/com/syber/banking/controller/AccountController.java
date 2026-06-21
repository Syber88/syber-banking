package com.syber.banking.controller;

import com.syber.banking.dto.request.CreateAccountRequest;
import com.syber.banking.dto.response.AccountResponse;
import com.syber.banking.entitiy.Account;
import com.syber.banking.repository.AccountRepository;
import com.syber.banking.repository.CustomerRepository;
import com.syber.banking.service.AccountService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/Accounts")
public class AccountController {

    private final CustomerRepository customerRepository;
    private final AccountService accountService;

    public AccountController(CustomerRepository customerRepository, AccountRepository accountRepository, AccountService accountService) {
        this.customerRepository = customerRepository;
        this.accountService = accountService;
    }

    @PostMapping("")
    public AccountResponse createAccount(@RequestBody CreateAccountRequest request) {
         Account account=  accountService.createAccount(request.getCustomerId(),request.getAccountType());
         return new AccountResponse(
                 account.getId(),
                 account.getAccountNumber(),
                 account.getBalance(),
                 account.getAccountType(),
                 account.getStatus()
         );
    }

}
