package com.syber.banking;

import com.syber.banking.dto.request.CreateCustomerRequest;
import com.syber.banking.dto.response.CustomerResponse;
import com.syber.banking.entity.Customer;
import com.syber.banking.exception.CustomerEmailAlreadyExistsException;
import com.syber.banking.exception.CustomerNationalIdAlreadyExistsException;
import com.syber.banking.exception.CustomerNotFoundException;
import com.syber.banking.mapper.CustomerMapper;
import com.syber.banking.repository.CustomerRepository;
import com.syber.banking.service.CustomerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceTest {

    @Mock
    CustomerRepository customerRepository;

    @Mock
    CustomerMapper customerMapper;

    @InjectMocks
    CustomerService customerService;

    // --- createCustomer ---
    @Test
    void shouldCreateCustomer() {
        CreateCustomerRequest request = createCustomerRequest();
        Customer customer = createCustomer(1L, request);
        CustomerResponse expectedResponse = createResponse(customer);

        when(customerRepository.existsByEmailIgnoreCase(request.getEmail()))
                .thenReturn(false);
        when(customerRepository.save(any(Customer.class)))
                .thenReturn(customer);
        when(customerMapper.toResponse(customer))
                .thenReturn(expectedResponse);

        CustomerResponse response = customerService.createCustomer(request);

        assertNotNull(response);
        assertEquals("Siyabonga", response.getFirstName());
        assertEquals("Magnum", response.getLastName());
        assertEquals("Siya@gmail.com", response.getEmail());

        verify(customerRepository).existsByEmailIgnoreCase(customer.getEmail());
        verify(customerRepository).save(any(Customer.class));
        verify(customerMapper).toResponse(customer);
    }

    @Test
    void shouldFailToCreateCustomerIfEmailExists() {
        CreateCustomerRequest request = createCustomerRequest();

        when(customerRepository.existsByEmailIgnoreCase(request.getEmail()))
                .thenReturn(true);

        assertThrows(CustomerEmailAlreadyExistsException.class, () ->
                customerService.createCustomer(request));

        verifyNoMoreInteractions(
                customerRepository,
                customerMapper
        );

    }

    @Test
    void shouldFailToCreateCustomerIfNationalIdExists() {
        CreateCustomerRequest request = createCustomerRequest();
        when(customerRepository.existsByEmailIgnoreCase(request.getEmail()))
                .thenReturn(false);
        when(customerRepository.existsByNationalId(request.getNationalId()))
                .thenReturn(true);

        assertThrows(CustomerNationalIdAlreadyExistsException.class, () ->
                customerService.createCustomer(request));

        verifyNoMoreInteractions(
                customerRepository,
                customerMapper
        );
    }

    // --- getCustomer ---
    @Test
    void shouldFetchASingleCustomer() {
        Customer customer = createCustomer(1L, createCustomerRequest());
        CustomerResponse response = createResponse(customer);

        when(customerRepository.findById(customer.getId()))
                .thenReturn(Optional.of(customer));
        when(customerMapper.toResponse(customer))
                .thenReturn(response);

        CustomerResponse result = customerService.getCustomer(customer.getId());

        assertNotNull(result);
        assertEquals(response, result);
        assertEquals("Siya@gmail.com", result.getEmail());

        verify(customerRepository).findById(customer.getId());
        verify(customerMapper).toResponse(customer);
    }

    @Test
    void shouldFailToFetchACustomerIfTheyDoNotExist() {
        Customer customer = createCustomer(1L, createCustomerRequest());
        when(customerRepository.findById(customer.getId()))
                .thenReturn(Optional.empty());

        assertThrows(CustomerNotFoundException.class, () ->
                customerService.getCustomer(customer.getId()));

        verifyNoInteractions(customerMapper);
    }

    // --- getCustomers ---
    @Test
    void shouldBeAbleToFetchMultipleCustomers() {
        Customer customer1 = createCustomer(1L, createCustomerRequest());
        Customer customer2 = createCustomer(
                2L,
                new CreateCustomerRequest(
                        "John",
                        "Doe",
                        "0123456789",
                        "john@gmail.com"
                )
        );

        CustomerResponse response1 = createResponse(customer1);
        CustomerResponse response2 = createResponse(customer2);

        when(customerRepository.findAll())
                .thenReturn(List.of(customer1, customer2));
        when(customerMapper.toResponse(customer1))
                .thenReturn(response1);
        when(customerMapper.toResponse(customer2))
                .thenReturn(response2);

        List<CustomerResponse> results = customerService.getCustomers();

        assertEquals(response1, results.get(0));
        assertEquals(response2, results.get(1));
        assertEquals(2, results.size());

        verify(customerRepository).findAll();
        verify(customerMapper).toResponse(customer1);
        verify(customerMapper).toResponse(customer2);
    }

    private CreateCustomerRequest createCustomerRequest() {
        return new CreateCustomerRequest(
                "Siyabonga",
                "Magnum",
                "012345678",
                "Siya@gmail.com"
        );
    }

    private Customer createCustomer(Long id, CreateCustomerRequest request) {
        Customer customer = new Customer();
        customer.setId(id);
        customer.setFirstName(request.getFirstName());
        customer.setNationalId(request.getNationalId());
        customer.setLastName(request.getLastName());
        customer.setEmail(request.getEmail());
        return customer;
    }

    private CustomerResponse createResponse(Customer customer) {
        return new CustomerResponse(
                1L,
                customer.getFirstName(),
                customer.getLastName(),
                customer.getEmail()
        );
    }
}
