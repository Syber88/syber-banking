package com.syber.banking.service;

import com.syber.banking.dto.request.CreateCustomerRequest;
import com.syber.banking.dto.request.UpdateCustomerRequest;
import com.syber.banking.dto.response.CustomerResponse;
import com.syber.banking.entity.Customer;
import com.syber.banking.exception.CustomerEmailAlreadyExistsException;
import com.syber.banking.exception.CustomerNationalIdAlreadyExistsException;
import com.syber.banking.exception.CustomerNotFoundException;
import com.syber.banking.mapper.CustomerMapper;
import com.syber.banking.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    public CustomerService (CustomerRepository customerRepository, CustomerMapper mapper) {
        this.customerRepository = customerRepository;
        this.customerMapper = mapper;
    }

    public CustomerResponse getCustomer(Long customerId) {
        Customer customer = findCustomerOrThrow(customerId);
        return customerMapper.toResponse(customer);
    }

    public CustomerResponse createCustomer(CreateCustomerRequest request) {
        if(customerRepository.existsByEmailIgnoreCase(request.getEmail())) {
            throw new CustomerEmailAlreadyExistsException("Email already exists");
        }
        if(customerRepository.existsByNationalId(request.getNationalId())) {
            throw new CustomerNationalIdAlreadyExistsException("ID Already exists");
        }
        Customer customer = new Customer();
        customer.setFirstName(request.getFirstName());
        customer.setNationalId(request.getNationalId());
        customer.setLastName(request.getLastName());
        customer.setEmail(request.getEmail());

        Customer savedCustomer = customerRepository.save(customer);
        return customerMapper.toResponse(savedCustomer);
    }

    public List<CustomerResponse> getCustomers() {
        return customerRepository.findAll().stream()
                .map(customer -> new CustomerResponse(
                        customer.getId(),
                        customer.getFirstName(),
                        customer.getLastName(),
                        customer.getEmail()
                ))
                .toList();
    }

    public CustomerResponse updateCustomer(Long customerId, UpdateCustomerRequest request) {
        Customer customer = findCustomerOrThrow(customerId);
        customer.setFirstName(request.getFirstName());
        customer.setLastName(request.getLastName());
        customer.setEmail(request.getEmail());

        Customer savedCustomer = customerRepository.save(customer);
        return customerMapper.toResponse(savedCustomer);
    }

    public void deleteCustomer(Long customerId) {
        if (!customerRepository.existsById(customerId)) {
            throw new CustomerNotFoundException("Customer Not found");
        }
        customerRepository.deleteById(customerId);
    }

    private Customer findCustomerOrThrow(Long customerId) {
        return customerRepository.findById(customerId).orElseThrow(() -> new CustomerNotFoundException("Customer not found"));
    }
}
