package com.syber.banking.controller;

import com.syber.banking.dto.request.CreateCustomerRequest;
import com.syber.banking.dto.response.CustomerResponse;
import com.syber.banking.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

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
    public List<CustomerResponse> getCustomers() {
        return customerService.getCustomers();
    }

    @GetMapping("/{customerId}")
    @Operation(
            summary = "Find customer by ID",
            description = "Returns a customer if it exists."
    )
    public CustomerResponse getCustomerById(@PathVariable Long customerId){
        return customerService.getCustomer(customerId);

    }

    @PostMapping
    public CustomerResponse createCustomer(@RequestBody CreateCustomerRequest request){
        return customerService.createCustomer(request);

    }


}
