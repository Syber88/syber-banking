package com.syber.banking.service;

import com.syber.banking.dto.request.CreateCustomerRequest;
import com.syber.banking.dto.request.UpdateCustomerRequest;
import com.syber.banking.dto.response.CustomerResponse;
import com.syber.banking.entity.Customer;
import com.syber.banking.entity.VerificationStatus;
import com.syber.banking.exception.CustomerEmailAlreadyExistsException;
import com.syber.banking.exception.CustomerNationalIdAlreadyExistsException;
import com.syber.banking.exception.CustomerNotFoundException;
import com.syber.banking.exception.InvalidVerificationTransitionException;
import com.syber.banking.mapper.CustomerMapper;
import com.syber.banking.repository.CustomerRepository;
import com.syber.banking.verification.IdentityVerifier;
import com.syber.banking.verification.VerificationResult;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;
    private final IdentityVerifier identityVerifier;          // add

    public CustomerService(CustomerRepository customerRepository,
                           CustomerMapper mapper,
                           IdentityVerifier identityVerifier) {   // add
        this.customerRepository = customerRepository;
        this.customerMapper = mapper;
        this.identityVerifier = identityVerifier;                 // add
    }

    public CustomerResponse getCustomer(Long customerId) {
        Customer customer = findCustomerOrThrow(customerId);
        return customerMapper.toResponse(customer);
    }

    public List<CustomerResponse> getCustomers() {
        return customerRepository.findAll()
                .stream()
                .map(customerMapper::toResponse)
                .toList();
    }

    public List<CustomerResponse> getPendingCustomers() {
        return customerRepository.findByVerificationStatus(VerificationStatus.PENDING)
                .stream()
                .map(customerMapper::toResponse)
                .toList();
    }

    public CustomerResponse createCustomer(CreateCustomerRequest request) {
        if (customerRepository.existsByEmailIgnoreCase(request.getEmail())) {
            throw new CustomerEmailAlreadyExistsException("Email already exists");
        }
        if (customerRepository.existsByNationalId(request.getNationalId())) {
            throw new CustomerNationalIdAlreadyExistsException("ID Already exists");
        }

        Customer customer = new Customer();
        customer.setFirstName(request.getFirstName());
        customer.setNationalId(request.getNationalId());
        customer.setLastName(request.getLastName());
        customer.setEmail(request.getEmail());

        VerificationResult result = identityVerifier.verify(
                request.getNationalId(), request.getFirstName(), request.getLastName());

        applyResult(customer, result);

        return customerMapper.toResponse(customerRepository.save(customer));
    }

    private void applyResult(Customer customer, VerificationResult result) {
        customer.setVerificationStatus(result.status());
        if (result.status() != VerificationStatus.PENDING) {
            customer.setVerifiedBy(result.source());
            customer.setVerifiedAt(Instant.now());
        }
    }

    public CustomerResponse verifyCustomer(Long customerId, String staffUsername) {
        Customer customer = findCustomerOrThrow(customerId);
        requirePending(customer);
        applyResult(customer, VerificationResult.verified(staffUsername));
        return customerMapper.toResponse(customerRepository.save(customer));
    }

    public CustomerResponse rejectCustomer(Long customerId, String staffUsername) {
        Customer customer = findCustomerOrThrow(customerId);
        requirePending(customer);
        customer.setVerificationStatus(VerificationStatus.REJECTED);
        customer.setVerifiedBy(staffUsername);
        customer.setVerifiedAt(Instant.now());
        return customerMapper.toResponse(customerRepository.save(customer));
    }

    private void requirePending(Customer customer) {
        if (customer.getVerificationStatus() != VerificationStatus.PENDING) {
            throw new InvalidVerificationTransitionException(
                    "Customer is already " + customer.getVerificationStatus());
        }
    }

    public CustomerResponse updateCustomer(Long customerId, UpdateCustomerRequest request) {
        Customer customer = findCustomerOrThrow(customerId);
        if (request.getFirstName() != null) {
            customer.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            customer.setLastName(request.getLastName());
        }
        if (request.getEmail() != null) {
            customer.setEmail(request.getEmail());
        }

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
