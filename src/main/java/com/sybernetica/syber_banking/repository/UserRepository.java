package com.sybernetica.syber_banking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.sybernetica.syber_banking.entity.User;

public interface UserRepository extends JpaRepository<User, Long>{
    Boolean existsByEmail(String Email){

    }
}
