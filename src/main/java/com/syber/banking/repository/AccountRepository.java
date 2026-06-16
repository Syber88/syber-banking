package com.syber.banking.repository;

import com.syber.banking.entitiy.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {
}
