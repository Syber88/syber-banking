package com.syber.banking.controller;

import com.syber.banking.dto.response.CustomerResponse;
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
    private CustomerResponse getCustomerById(@PathVariable Long customerId){
        Customer customer =  customerRepository.findById(customerId).orElseThrow();
        return new CustomerResponse(
                customer.getId(),
                customer.getAccounts(),
                customer.getFirstName(),
                customer.getLastName(),
                customer.getEmail()
        );
    }

    @PostMapping
    private CustomerResponse createCustomer(@RequestBody Customer customer){
        customerRepository.save(customer);
        return new CustomerResponse(
                customer.getId(),
                customer.getAccounts(),
                customer.getFirstName(),
                customer.getLastName(),
                customer.getEmail()
        );
    }
}
