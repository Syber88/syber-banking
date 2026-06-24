package com.syber.banking.controller;

import com.syber.banking.dto.request.CreateAccountRequest;
import com.syber.banking.dto.request.DepositRequest;
import com.syber.banking.dto.response.AccountResponse;
import com.syber.banking.dto.response.DepositResponse;
import com.syber.banking.entitiy.Account;
import com.syber.banking.entitiy.Transaction;
import com.syber.banking.service.AccountService;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Path;

@RestController
@RequestMapping("/Accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController( AccountService accountService) {
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

    @PostMapping("/{accountId}/deposit")
    public DepositResponse deposit(@PathVariable Long accountId, @RequestBody DepositRequest request){
        Transaction tx = accountService.deposit(accountId, request.getAmount());
        return new DepositResponse(
                tx.getId(),
                tx.getToAccountNumber(),
                tx.getAmount(),
                tx.getCreatedAt(),
                tx.getStatus()
        );
    }

    @GetMapping("/{accountId}")
    public AccountResponse getAccountById(@PathVariable Long accountId) {
        Account account = accountService
    }
}
