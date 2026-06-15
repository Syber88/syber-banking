package com.syber.banking.controller;

import com.syber.banking.entitiy.Customer;
import com.syber.banking.repository.CustomerRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customer")
public class CustomerController {

    private final CustomerRepository customerRepository;

    public CustomerController(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @GetMapping
    private List<Customer> getCustomers() {
        return customerRepository.findAll();
    }

    @GetMapping("/{customerId}")
    private Customer getCustomerById(@PathVariable Long customerId){
        return customerRepository.findById(customerId).orElseThrow();
    }

    @PostMapping
    private Customer createCustomer(@RequestBody Customer customer){
        return customerRepository.save(customer);
    }
}
