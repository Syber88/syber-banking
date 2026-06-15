package com.syber.banking.repository;

import com.syber.banking.entitiy.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
}
