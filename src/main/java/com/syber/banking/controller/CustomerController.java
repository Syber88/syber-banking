package com.syber.banking.controller;

import com.syber.banking.dto.request.CreateCustomerRequest;
import com.syber.banking.dto.request.UpdateCustomerRequest;
import com.syber.banking.dto.response.CustomerResponse;
import com.syber.banking.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController


@RequestMapping("/api/v1/customers")
@Tag(name = "Customer", description = "Customer Management APIs")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @Operation(
            summary = "Get all customers",
            description = "Returns all customers registered in the banking system."
    )
    @GetMapping
    public ResponseEntity<List<CustomerResponse>> getCustomers() {
        List<CustomerResponse> customers = customerService.getCustomers();
        return ResponseEntity.ok(customers);
    }

    @Operation(
            summary = "Find customer by ID",
            description = "Returns a customer if it exists."
    )
    @GetMapping("/{customerId}")
    public ResponseEntity<CustomerResponse> getCustomerById(@PathVariable Long customerId){
        CustomerResponse customer = customerService.getCustomer(customerId);
        return ResponseEntity.ok(customer);

    }

    @Operation(
            summary = "Creates a customer",
            description = "Creates a new customer in the banking system."
    )
    @PostMapping()
    public ResponseEntity<CustomerResponse> createCustomer(@Valid @RequestBody CreateCustomerRequest request){
        CustomerResponse customer = customerService.createCustomer(request);
        URI location = URI.create("/api/v1/customers/" + customer.getId());
        return ResponseEntity.created(location).body(customer);
    }

    @Operation(
            summary = "Update customer",
            description = "Updates an existing customer's information."
    )
    @PatchMapping("/{customerId}")
    public ResponseEntity<CustomerResponse> updateCustomer(  @PathVariable Long customerId, @Valid @RequestBody UpdateCustomerRequest request) {
        CustomerResponse customer = customerService.updateCustomer(customerId, request);
        return ResponseEntity.ok(customer);
    }

    @Operation(
            summary = "Delete customer",
            description = "Deletes a customer by ID."
    )
    @DeleteMapping("/{customerId}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long customerId) {
        customerService.deleteCustomer(customerId);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Verify a customer",
            description = "Marks a PENDING customer as VERIFIED. Admin only."
    )
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{customerId}/verify")
    public ResponseEntity<CustomerResponse> verify(@PathVariable Long customerId,
                                                   Authentication authentication) {
        return ResponseEntity.ok(customerService.verifyCustomer(customerId, authentication.getName()));
    }

    @Operation(
            summary = "Reject a customer",
            description = "Marks a PENDING customer as REJECTED. Admin only."
    )
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{customerId}/reject")
    public ResponseEntity<CustomerResponse> reject(@PathVariable Long customerId,
                                                   Authentication authentication) {
        return ResponseEntity.ok(customerService.rejectCustomer(customerId, authentication.getName()));
    }

    @Operation(
            summary = "Review queue",
            description = "Lists customers awaiting identity verification. Admin only."
    )
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/pending")
    public ResponseEntity<List<CustomerResponse>> pending() {
        return ResponseEntity.ok(customerService.getPendingCustomers());
    }
}
