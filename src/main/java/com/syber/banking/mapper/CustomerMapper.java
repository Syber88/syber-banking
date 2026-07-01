package com.syber.banking.mapper;

import com.syber.banking.dto.request.CreateCustomerRequest;
import com.syber.banking.dto.response.CustomerResponse;
import com.syber.banking.entity.Customer;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class CustomerMapper {

    public CustomerResponse toResponse(Customer customer) {
        return new CustomerResponse(
                customer.getId(),
                customer.getFirstName(),
                customer.getLastName(),
                customer.getEmail()
        );
    }

    public Customer toEntity(CreateCustomerRequest request) {
        Customer customer = new Customer();

        customer.setFirstName(request.getFirstName());
        customer.setLastName(request.getLastName());
        customer.setEmail(request.getEmail());

        return customer;
    }
}
