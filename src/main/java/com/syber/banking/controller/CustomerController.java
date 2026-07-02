package com.syber.banking.controller;

import com.syber.banking.dto.request.CreateCustomerRequest;
import com.syber.banking.dto.request.UpdateCustomerRequest;
import com.syber.banking.dto.response.CustomerResponse;
import com.syber.banking.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @GetMapping
    public ResponseEntity<List<CustomerResponse>> getCustomers() {
        List<CustomerResponse> customers = customerService.getCustomers();
        return ResponseEntity.ok(customers);
    }

    @GetMapping("/{customerId}")
    @Operation(
            summary = "Find customer by ID",
            description = "Returns a customer if it exists."
    )
    public ResponseEntity<CustomerResponse> getCustomerById(@PathVariable Long customerId){
        CustomerResponse customer = customerService.getCustomer(customerId);
        return ResponseEntity.ok(customer);

    }

    @PostMapping
    public ResponseEntity<CustomerResponse> createCustomer(@RequestBody CreateCustomerRequest request){
        CustomerResponse customer = customerService.createCustomer(request);
        URI location = URI.create("/api/v1/customers/" + customer.getId());
        return ResponseEntity.created(location).body(customer);

    }

    @PutMapping("/{customerId}")
    public ResponseEntity<CustomerResponse> updateCustomer(@PathVariable Long customerId, @RequestBody UpdateCustomerRequest request) {
        CustomerResponse customer = customerService.updateCustomer(customerId, request);
        return ResponseEntity.ok(customer);
    }

    @DeleteMapping("/{customerId}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long customerId) {
        customerService.deleteCustomer(customerId);
        return ResponseEntity.noContent().build();
    }



}
