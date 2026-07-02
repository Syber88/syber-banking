package com.syber.banking.controller;

import com.syber.banking.dto.request.CreateAccountRequest;
import com.syber.banking.dto.request.DepositRequest;
import com.syber.banking.dto.response.AccountResponse;
import com.syber.banking.dto.response.DepositResponse;
import com.syber.banking.entity.Account;
import com.syber.banking.service.AccountService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController( AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping("")
    public AccountResponse createAccount(@RequestBody CreateAccountRequest request) {
         return accountService.createAccount(request.getCustomerId(),request.getAccountType());
    }

    @PostMapping("/{accountId}/deposit")
    public DepositResponse deposit(@PathVariable Long accountId, @RequestBody DepositRequest request){
        return  accountService.deposit(accountId, request.getAmount());
    }

    @GetMapping("/{accountId}")
    public AccountResponse getAccountById(@PathVariable Long accountId) {
        Account account = accountService.getAccountById(accountId);
        return new AccountResponse(
                account.getId(),
                account.getAccountNumber(),
                account.getBalance(),
                account.getAccountType(),
                account.getStatus()
        );
    }
}
