package com.syber.banking.repository;

import com.syber.banking.entitiy.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Account findByAccountId(Long Account);
}
