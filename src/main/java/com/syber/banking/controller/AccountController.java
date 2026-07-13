package com.syber.banking.controller;

import com.syber.banking.dto.request.CreateAccountRequest;
import com.syber.banking.dto.request.DepositRequest;
import com.syber.banking.dto.request.TransferRequest;
import com.syber.banking.dto.request.WithdrawRequest;
import com.syber.banking.dto.response.AccountResponse;
import com.syber.banking.dto.response.TransactionResponse;
import com.syber.banking.entity.Account;
import com.syber.banking.mapper.AccountMapper;
import com.syber.banking.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/accounts")
@Tag(name = "Accounts", description = "Operations for managing customer bank accounts")
public class AccountController {

    private final AccountService accountService;
    private final AccountMapper mapper;

    public AccountController( AccountService accountService, AccountMapper mapper) {
        this.mapper = mapper;
        this.accountService = accountService;
    }

    @Operation(
            summary = "Create a bank account",
            description = "Creates a new bank account for an existing customer."
    )
    @PostMapping("")
    public ResponseEntity<AccountResponse> createAccount(@Valid @RequestBody CreateAccountRequest request) {
        AccountResponse response = accountService.createAccount(request);
        URI location = URI.create("/api/v1/accounts/" + response.getId());
        return ResponseEntity.created(location).body(response);
    }

    @Operation(
            summary = "Deposit money",
            description = "Deposits funds into an existing bank account."
    )
    @PostMapping("/{accountId}/deposit")
    public ResponseEntity<TransactionResponse> deposit(@PathVariable Long accountId, @Valid @RequestBody DepositRequest request){
        TransactionResponse response = accountService.deposit(accountId, request);
        return ResponseEntity.ok(response);

    }

    @Operation(
            summary = "withdraw money",
            description = "withdraw funds from an existing bank account."
    )
    @PostMapping("/{accountId}/withdraw")
    public ResponseEntity<TransactionResponse> withdraw(@PathVariable Long accountId, @Valid @RequestBody WithdrawRequest request) {
        TransactionResponse response = accountService.withdraw(accountId, request);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Retrieve account",
            description = "Returns account details using the account ID."
    )
    @GetMapping("/{accountId}")
    public ResponseEntity<AccountResponse> getAccountById(@PathVariable Long accountId) {
        Account account = accountService.getAccountById(accountId);
        AccountResponse response = mapper.toResponse(account);
        return ResponseEntity.ok(response);
    }
    @Operation (
            summary = "Transfer money",
            description = "Transfers money between 2 existing accounts."
    )
    @PostMapping("/{accountId}/transfer")
    public ResponseEntity<TransactionResponse> transfer(@PathVariable Long accountId,
                                                        @Valid @RequestBody TransferRequest request
    ) {
        TransactionResponse response = accountService.transfer(accountId, request);
        return ResponseEntity.ok(response);
    }

    @Operation (
            summary = "Delete account",
            description = "Deletes the account using the account ID."
    )
    @DeleteMapping("/{accountId}")
    public ResponseEntity<Void> deleteAccount(@PathVariable Long accountId) {
        accountService.deleteAccount(accountId);
        return ResponseEntity.noContent().build();
    }
}
