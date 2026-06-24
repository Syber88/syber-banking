package com.syber.banking.controller;

import com.syber.banking.dto.request.CreateCustomerRequest;
import com.syber.banking.dto.response.CustomerResponse;
import com.syber.banking.entitiy.Customer;
import com.syber.banking.service.CustomerService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customer")
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
    public CustomerResponse getCustomerById(@PathVariable Long customerId){
        return customerService.getCustomer(customerId);

    }

    @PostMapping
    public CustomerResponse createCustomer(@RequestBody CreateCustomerRequest request){
        return customerService.createCustomer(request);

    }
}
