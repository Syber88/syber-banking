package com.syber.banking.repository;

import com.syber.banking.entitiy.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
}
