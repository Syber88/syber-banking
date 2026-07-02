package com.syber.banking.controller;

import com.syber.banking.dto.request.CreateAccountRequest;
import com.syber.banking.dto.request.DepositRequest;
import com.syber.banking.dto.response.AccountResponse;
import com.syber.banking.dto.response.DepositResponse;
import com.syber.banking.entity.Account;
import com.syber.banking.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/accounts")
@Tag(name = "Accounts", description = "Operations for managing customer bank accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController( AccountService accountService) {
        this.accountService = accountService;
    }

    @Operation(
            summary = "Create a bank account",
            description = "Creates a new bank account for an existing customer."
    )
    @PostMapping("")
    public AccountResponse createAccount(@RequestBody CreateAccountRequest request) {
         return accountService.createAccount(request.getCustomerId(),request.getAccountType());
    }

    @Operation(
            summary = "Deposit money",
            description = "Deposits funds into an existing bank account."
    )
    @PostMapping("/{accountId}/deposit")
    public DepositResponse deposit(@PathVariable Long accountId, @RequestBody DepositRequest request){
        return  accountService.deposit(accountId, request.getAmount());
    }

    @Operation(
            summary = "Retrieve account",
            description = "Returns account details using the account ID."
    )
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
